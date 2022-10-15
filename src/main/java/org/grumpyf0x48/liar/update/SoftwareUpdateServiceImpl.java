package org.grumpyf0x48.liar.update;

import org.apache.commons.io.FileUtils;
import org.grumpyf0x48.liar.update.exceptions.*;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class SoftwareUpdateServiceImpl implements SoftwareUpdateService
{
    private final String softwareResource;
    private final NetworkService networkService;
    private SoftwareUpdateOptions updateOptions = SoftwareUpdateOptions.withDefault();

    public SoftwareUpdateServiceImpl(final String softwareResource, final NetworkService networkService)
    {
        this.softwareResource = softwareResource;
        this.networkService = networkService;
    }

    @Override
    public String getSoftwareResource()
    {
        return softwareResource;
    }

    @Override
    public boolean updateSoftwareResource(final SoftwareUpdatePeriodicity softwareUpdatePeriodicity) throws IOException, SoftwareException
    {
        final Map<SoftwareDefinition, SoftwareUrl> urlMap = updateSoftwareDefinitions(softwareUpdatePeriodicity);
        if (!urlMap.isEmpty())
        {
            final File softwareFile = getSoftwareFile();
            final List<String> lines = FileUtils.readLines(softwareFile, StandardCharsets.UTF_8);
            urlMap.forEach((softwareDefinition, softwareUrl) ->
            {
                try
                {
                    updateSoftwareDefinition(softwareDefinition, softwareUrl, lines);
                    System.out.println("Updated URL for: " + softwareDefinition);
                }
                catch (final SoftwareNotFoundException e)
                {
                    System.err.println(e.getMessage());
                }
            });
            final String lastUpdates = urlMap.keySet()
                    .toString()
                    .toLowerCase()
                    .replaceAll(",", "")
                    .replace("[", "(")
                    .replace("]", ")");
            updateSoftwareDefinition(SoftwareDefinition.LAST_UPDATES, lastUpdates, lines);
            FileUtils.writeLines(softwareFile, lines);
        }
        return !urlMap.isEmpty();
    }

    @Override
    public boolean checkSoftwareResource() throws IOException, SoftwareException
    {
        boolean check = false;
        for (final SoftwareDefinition softwareDefinition : SoftwareDefinition.getValues())
        {
            final SoftwareUrl softwareUrl = getUrl(softwareDefinition);
            final String url = softwareUrl.getUrl();
            System.out.println("Checking: " + url);
            if (!networkService.urlExists(url))
            {
                check = true;
                System.err.println(url);
            }
        }
        return check;
    }

    private Map<SoftwareDefinition, SoftwareUrl> updateSoftwareDefinitions(final SoftwareUpdatePeriodicity softwareUpdatePeriodicity) throws IOException, SoftwareException
    {
        final EnumMap<SoftwareDefinition, SoftwareUrl> urlMap = new EnumMap<>(SoftwareDefinition.class);
        for (final SoftwareDefinition softwareDefinition : getSoftwareToUpdate(softwareUpdatePeriodicity))
        {
            System.out.println("Searching updates for: " + softwareDefinition);
            try
            {
                final SoftwareUrl url = getUrl(softwareDefinition);
                final SoftwareUrl nextUrl = getNextUrl(softwareDefinition);
                if (!nextUrl.getUrl().equals(url.getUrl()))
                {
                    urlMap.put(softwareDefinition, nextUrl);
                }
            }
            catch (final SoftwareNotFoundException e)
            {
                if (!updateOptions.skipSoftwareNotFound)
                {
                    throw e;
                }
            }
            catch (final SoftwareVersionNotIncrementableException e)
            {
                System.err.println("Gave up while searching next URL for: " + softwareDefinition + " after: " + e.getMaxTries() + " tries");
            }
            catch (final SoftwareUrlNotParsableException e)
            {
                System.err.println("Don't know how to parse URL for: " + softwareDefinition);
            }
            catch (final SoftwareException e)
            {
                System.err.println("An error occurred when searching updates for: " + softwareDefinition);
                e.printStackTrace(System.err);
            }
        }
        return urlMap;
    }

    @Override
    public SoftwareUpdateOptions getUpdateOptions()
    {
        return updateOptions;
    }

    @Override
    public void setUpdateOptions(final SoftwareUpdateOptions updateOptions)
    {
        this.updateOptions = updateOptions;
    }

    @Override
    public Properties getSoftwareProperties() throws IOException
    {
        final Properties softwareProperties = new Properties();
        try (final InputStream inputStream = new BufferedInputStream(new FileInputStream(getSoftwareFile())))
        {
            softwareProperties.load(inputStream);
        }
        return softwareProperties;
    }

    @Override
    public String[] getSoftwareList() throws IOException
    {
        final List<String> softwareList = new ArrayList<>();
        final Enumeration<Object> softwareEnumeration = retainSoftwareProperties(getSoftwareProperties()).keys();
        while (softwareEnumeration.hasMoreElements())
        {
            softwareList.add(softwareEnumeration.nextElement().toString());
        }
        Collections.sort(softwareList);
        return softwareList.toArray(new String[]{});
    }

    @Override
    public SoftwareUrl getUrl(final SoftwareDefinition software) throws IOException, SoftwareException
    {
        final String url = getSoftwareProperties().getProperty(software.name().toLowerCase());
        if (url == null)
        {
            throw new SoftwareNotFoundException("Software: " + software + " does not exist");
        }
        return new SoftwareUrl(software, url, updateOptions, networkService);
    }

    @Override
    public SoftwareUrl getNextUrl(final SoftwareDefinition software) throws IOException, SoftwareException
    {
        return getUrl(software).getNext();
    }

    @Override
    public SoftwareUrl getNextUrl(final SoftwareUrl softwareUrl) throws SoftwareException
    {
        return new SoftwareUrl(softwareUrl.getSoftware(), softwareUrl.getUrl(), updateOptions, networkService).getNext();
    }

    private static List<SoftwareDefinition> getSoftwareToUpdate(final SoftwareUpdatePeriodicity softwareUpdatePeriodicity)
    {
        return Arrays
            .stream(SoftwareDefinition.getValues())
            .filter(softwareDefinition -> softwareUpdatePeriodicity == null || softwareDefinition
                .getPeriodicity()
                .equals(softwareUpdatePeriodicity))
            .collect(Collectors.toList());
    }

    private File getSoftwareFile() throws FileNotFoundException
    {
        final File file = new File(getSoftwareResource());
        if (file.exists())
        {
            return file;
        }
        final URL resource = getClass().getResource(getSoftwareResource());
        if (resource != null)
        {
            return new File(resource.getFile());
        }
        throw new FileNotFoundException(getSoftwareResource());
    }

    private void updateSoftwareDefinition(final SoftwareDefinition softwareDefinition, final SoftwareUrl url, final List<String> lines) throws SoftwareNotFoundException
    {
        updateSoftwareDefinition(softwareDefinition, url.getUrl(), lines);
    }

    private void updateSoftwareDefinition(final SoftwareDefinition softwareDefinition, final String updatedValue, final List<String> lines) throws SoftwareNotFoundException {
        final int index = searchLine(softwareDefinition, lines);
        if (index == -1)
        {
            throw new SoftwareNotFoundException("Failed to find: " + softwareDefinition + " in software file");
        }
        final String newline = softwareDefinition.name().toLowerCase() + "=" + updatedValue;
        lines.set(index, newline);
    }

    private int searchLine(final SoftwareDefinition softwareDefinition, final List<String> lines)
    {
        for (int index = 0; index < lines.size(); index++)
        {
            final String line = lines.get(index);
            if (line.startsWith(softwareDefinition.name().toLowerCase() + "="))
            {
                return index;
            }
        }
        return -1;
    }

    private Properties retainSoftwareProperties(final Properties properties)
    {
        final Properties softwareProperties = new Properties();
        softwareProperties.putAll(properties);
        for (final Map.Entry<Object, Object> entry : properties.entrySet())
        {
            if (!entry.getValue().toString().startsWith("http"))
            {
                softwareProperties.remove(entry.getKey());
            }
        }
        return softwareProperties;
    }
}
