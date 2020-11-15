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
    public static final String LIAR_SOFTWARE = ".config/liar/liar-software";

    private final String softwareResource;
    private SoftwareUpdateOptions updateOptions = SoftwareUpdateOptions.withDefault();

    public SoftwareUpdateServiceImpl()
    {
        this(System.getProperty("user.home") + "/" + LIAR_SOFTWARE);
    }

    public SoftwareUpdateServiceImpl(final String softwareResource)
    {
        this.softwareResource = softwareResource;
    }

    @Override
    public String getSoftwareResource()
    {
        return softwareResource;
    }

    @Override
    public boolean updateSoftwareResource(final SoftwareUpdatePeriodicity softwareUpdatePeriodicity) throws IOException, SoftwareException
    {
        boolean updated = false;
        final List<String> lines = FileUtils.readLines(getSoftwareFile(), StandardCharsets.UTF_8);
        for (final SoftwareDefinition softwareDefinition : getSoftwareToUpdate(softwareUpdatePeriodicity))
        {
            try
            {
                final SoftwareUrl url = getUrl(softwareDefinition);
                final SoftwareUrl nextUrl = getNextUrl(softwareDefinition);
                if (!nextUrl.getUrl().equals(url.getUrl()))
                {
                    updateSoftwareDefinition(softwareDefinition, nextUrl, lines);
                    updated = true;
                    System.out.println("Updated URL for: " + softwareDefinition);
                }
            }
            catch (final SoftwareNotFoundException e)
            {
                if (updateOptions.skipSoftwareNotFound)
                {
                    continue;
                }
                throw e;
            }
            catch (final SoftwareVersionNotIncrementableException e)
            {
                System.err.println("Gave up while searching next URL for: " + softwareDefinition);
            }
            catch (final SoftwareUrlNotParsableException e)
            {
                System.err.println("Don't know how to parse URL for: " + softwareDefinition);
            }
        }
        if (updated)
        {
            FileUtils.writeLines(getSoftwareFile(), lines);
        }
        return updated;
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
        return new SoftwareUrl(software, url, updateOptions);
    }

    @Override
    public SoftwareUrl getNextUrl(final SoftwareDefinition software) throws IOException, SoftwareException
    {
        return getUrl(software).getNext();
    }

    @Override
    public SoftwareUrl getNextUrl(final SoftwareUrl softwareUrl) throws SoftwareException
    {
        return new SoftwareUrl(softwareUrl.getSoftware(), softwareUrl.getUrl(), updateOptions).getNext();
    }

    private static List<SoftwareDefinition> getSoftwareToUpdate(final SoftwareUpdatePeriodicity softwareUpdatePeriodicity)
    {
        return Arrays
            .stream(SoftwareDefinition.values())
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

    private void updateSoftwareDefinition(final SoftwareDefinition softwareDefinition, final SoftwareUrl url, final List<String> lines)
                    throws SoftwareException
    {
        final int index = searchLine(softwareDefinition, lines);
        if (index == -1)
        {
            throw new SoftwareException("Failed to update SoftwareResource for: " + softwareDefinition);
        }
        final String newline = softwareDefinition.name().toLowerCase() + "=" + url.getUrl();
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
