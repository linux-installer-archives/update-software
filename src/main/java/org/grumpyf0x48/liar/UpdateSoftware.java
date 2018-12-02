package org.grumpyf0x48.liar;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class UpdateSoftware
{
    private UpdateSoftware()
    {
    }

    public static void generateSoftwareEnum()
    {

    }

    public static Properties getApplicationProperties() throws IOException
    {
        final Properties properties = new Properties();
        properties.load(UpdateSoftware.class.getResourceAsStream("/application.properties"));
        return properties;
    }

    public static Properties getSoftwareProperties() throws IOException
    {
        final Properties applicationProperties = getApplicationProperties();
        final String liarSoftwareFile = applicationProperties.getProperty("liar_software");
        FileInputStream inputStream = null;
        final Properties softwareProperties = new Properties();
        try
        {
            inputStream = new FileInputStream(liarSoftwareFile);
            softwareProperties.load(inputStream);
        }
        finally
        {
            if (inputStream != null)
            {
                inputStream.close();
            }
        }
        return softwareProperties;
    }
}
