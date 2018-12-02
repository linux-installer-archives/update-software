package org.grumpyf0x48.liar;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

public class UpdateSoftwareTest
{
    @Test
    public void getPropertiesTest() throws IOException
    {
        final Properties properties = UpdateSoftware.getApplicationProperties();
        final String userHome = System.getProperty("user.home");
        // Assert.assertEquals(userHome + "/Sources/liar", properties.get("liar_home"));
        Assert.assertEquals(userHome + "/.liar_software", properties.get("liar_software"));
    }

    @Test
    public void getSoftwarePropertiesTest() throws IOException
    {
        final Properties softwareProperties = UpdateSoftware.getSoftwareProperties();
        Assert.assertNotNull(softwareProperties.getProperty("software"));
    }
}
