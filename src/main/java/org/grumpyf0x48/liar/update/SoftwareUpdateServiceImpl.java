package org.grumpyf0x48.liar.update;

import org.grumpyf0x48.liar.update.exceptions.*;
import org.grumpyf0x48.misc.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

public class SoftwareUpdateServiceImpl implements SoftwareUpdateService
{
    public static final String LIAR_SOFTWARE = ".liar_software";

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
    public boolean updateSoftwareResource() throws IOException, SoftwareException
    {
        boolean updated = false;
        final List lines = IOUtils.readLines(new FileInputStream(getSoftwareFile()));
        for (final SoftwareDefinition softwareDefinition : SoftwareDefinition.values())
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
            IOUtils.writeLines(lines, null, new FileOutputStream(getSoftwareFile()));
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
        try (final FileInputStream inputStream = new FileInputStream(getSoftwareFile()))
        {
            softwareProperties.load(inputStream);
        }
        return softwareProperties;
    }

    @Override
    public String[] getSoftwareList() throws IOException
    {
        final String softwareAsString = StringUtils.unquote(getSoftwareProperties().getProperty("software"));
        return softwareAsString.split("\\s+");
    }

    @Override
    public SoftwareUrl getUrl(final SoftwareDefinition software) throws IOException, SoftwareException
    {
        final String url = getSoftwareProperties().getProperty(software.name().toLowerCase());
        if (url == null)
        {
            throw new SofwareNotFoundException("Software: " + software + " does not exist");
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

    private File getSoftwareFile() throws FileNotFoundException
    {
        final File softwareFile = new File(getSoftwareResource());
        if (softwareFile.exists())
        {
            return softwareFile;
        }
        final URL softwareResource = getClass().getResource(getSoftwareResource());
        if (softwareResource != null)
        {
            return new File(softwareResource.getFile());
        }
        throw new FileNotFoundException(getSoftwareResource());
    }

    private void updateSoftwareDefinition(final SoftwareDefinition softwareDefinition, final SoftwareUrl url, final List lines)
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

    private int searchLine(final SoftwareDefinition softwareDefinition, final List lines)
    {
        for (int index = 0; index < lines.size(); index++)
        {
            final String line = (String) lines.get(index);
            if (line.startsWith(softwareDefinition.name().toLowerCase() + "="))
            {
                return index;
            }
        }
        return -1;
    }
}
