package org.grumpyf0x48.liar.update;

import org.grumpyf0x48.liar.update.SoftwareUpdateOptions.SoftwareUrlIncrementPolicy;
import org.grumpyf0x48.liar.update.SoftwareUpdateOptions.SoftwareVersionIncrementPolicy;
import org.grumpyf0x48.liar.update.exceptions.SoftwareException;
import org.grumpyf0x48.misc.NetworkUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Properties;

import static org.apache.commons.io.FileUtils.contentEquals;
import static org.apache.commons.io.FileUtils.copyFile;

public class SoftwareUpdateServiceTest
{
    private static SoftwareUpdateService nextUpdateService;
    private static SoftwareUpdateService nextExistingUpdateService;
    private static SoftwareUpdateOptions nextSoftwareUpdateOptions;

    @BeforeClass
    public static void initialize()
    {
        nextUpdateService = SoftwareUpdateService.getInstance();

        nextSoftwareUpdateOptions = new SoftwareUpdateOptions( //
                        SoftwareUrlIncrementPolicy.NEXT, //
                        SoftwareVersionIncrementPolicy.withDefault());

        nextUpdateService.setUpdateOptions(nextSoftwareUpdateOptions);

        nextExistingUpdateService = SoftwareUpdateService.getInstance();
    }

    @Test
    public void getSoftwareResourceInClassPath() throws IOException
    {
        final SoftwareUpdateService softwareUpdateService = new SoftwareUpdateServiceImpl("/.liar_software");
        Assert.assertNotNull(softwareUpdateService.getSoftwareProperties());
    }

    @Test(expected = FileNotFoundException.class)
    public void getSoftwareResourceNotInClassPath() throws IOException
    {
        final SoftwareUpdateService softwareUpdateService = new SoftwareUpdateServiceImpl("/not_found");
        softwareUpdateService.getSoftwareProperties();
    }

    @Test
    public void getSoftwareFilePathTest()
    {
        final String softwareFilePath = nextUpdateService.getSoftwareResource();
        Assert.assertEquals(System.getProperty("user.home") + "/.liar_software", softwareFilePath);
    }

    @Test
    public void updateSoftwareResourceTest() throws IOException, SoftwareException
    {
        final File testDir = new File("src/test/resources");
        final File initialSoftwareFile = new File(testDir, ".liar_software");
        final File updatedSoftwareFile = new File(testDir, initialSoftwareFile.getName() + ".updated");
        copyFile(initialSoftwareFile, updatedSoftwareFile);

        final SoftwareUpdateService softwareUpdateService = new SoftwareUpdateServiceImpl(updatedSoftwareFile.getAbsolutePath());
        softwareUpdateService.setUpdateOptions(nextSoftwareUpdateOptions);
        final boolean updated = softwareUpdateService.updateSoftwareResource();

        Assert.assertTrue(updated);
        Assert.assertFalse(contentEquals(initialSoftwareFile, updatedSoftwareFile));
        final File expectedSoftwareFile = new File(testDir,  initialSoftwareFile.getName() + ".expected");
        Assert.assertTrue(contentEquals(expectedSoftwareFile, updatedSoftwareFile));
    }

    @Test
    public void getUpdateOptionsTest()
    {
        final SoftwareUpdateOptions softwareUpdateOptions = nextUpdateService.getUpdateOptions();
        Assert.assertNotNull(softwareUpdateOptions);
    }

    @Test
    public void getSoftwarePropertiesTest() throws IOException
    {
        final Properties softwareProperties = nextUpdateService.getSoftwareProperties();
        Assert.assertNotNull(softwareProperties.getProperty("software"));
    }

    @Test
    public void getSoftwareListTest() throws IOException
    {
        final String[] softwareList = nextUpdateService.getSoftwareList();
        Assert.assertEquals(31, softwareList.length);
    }

    @Test
    public void getUrlTest() throws IOException, SoftwareException
    {
        for (final SoftwareDefinition software : SoftwareDefinition.values())
        {
            final SoftwareUrl url = nextUpdateService.getUrl(software);
            Assert.assertTrue("URL for software: " + software + " does not exist", NetworkUtils.urlExists(url.getUrl()));
        }
    }

    @Test
    public void getNextUrlVersionTest() throws SoftwareException
    {
        final SoftwareUrl initialUrl = new SoftwareUrl(SoftwareDefinition.GOLAND, "https://download.jetbrains.com/go/goland-2018.3.2.tar.gz");
        final SoftwareUrl nextUrl = nextUpdateService.getNextUrl(initialUrl);
        Assert.assertEquals("https://download.jetbrains.com/go/goland-2018.3.3.tar.gz", nextUrl.getUrl());
    }

    @Test
    public void getNextExistingURLTest() throws SoftwareException
    {
        final SoftwareUrl initialAntUrl = new SoftwareUrl(SoftwareDefinition.ANT, "http://mirrors.standaloneinstaller.com/apache/ant/binaries/apache-ant-1.10.1-bin.zip");
        final SoftwareUrl nextAntUrl = nextExistingUpdateService.getNextUrl(initialAntUrl);
        Assert.assertEquals("http://mirrors.standaloneinstaller.com/apache/ant/binaries/apache-ant-1.10.5-bin.zip", nextAntUrl.getUrl());
        Assert.assertEquals(SoftwareDefinition.ANT, nextAntUrl.getSoftware());
        Assert.assertEquals("1.10.5", nextAntUrl.getVersion().toString());

        final SoftwareUrl initialAtomUrl = new SoftwareUrl(SoftwareDefinition.ATOM, "https://github.com/atom/atom/releases/download/v1.32.2/atom-amd64.tar.gz");
        final SoftwareUrl nextAtomUrl = nextExistingUpdateService.getNextUrl(initialAtomUrl);
        Assert.assertNotEquals(nextAtomUrl.getUrl(), initialAtomUrl);
        Assert.assertEquals("https://github.com/atom/atom/releases/download/v1.33.0/atom-amd64.tar.gz", nextAtomUrl.getUrl());
        Assert.assertEquals(SoftwareDefinition.ATOM, nextAtomUrl.getSoftware());
        Assert.assertEquals("1.33.0", nextAtomUrl.getVersion().toString());

        final SoftwareUrl initialGoUrl = new SoftwareUrl(SoftwareDefinition.GO, "https://dl.google.com/go/go1.11.4.linux-amd64.tar.gz");
        final SoftwareUrl nextGoUrl = nextExistingUpdateService.getNextUrl(initialGoUrl);
        Assert.assertNotEquals(nextGoUrl.getUrl(), initialGoUrl);
        Assert.assertEquals("https://dl.google.com/go/go1.11.5.linux-amd64.tar.gz", nextGoUrl.getUrl());
        Assert.assertEquals(SoftwareDefinition.GO, nextGoUrl.getSoftware());
        Assert.assertEquals("1.11.5", nextGoUrl.getVersion().toString());
    }

    @Test
    public void checkUpdatableSoftwareTest()
    {
        int count = 0;
        for (final SoftwareDefinition software : SoftwareDefinition.values())
        {
            try
            {
                nextUpdateService.getNextUrl(software);
                count++;
            }
            catch (final Exception e)
            {
                Assert.fail(software + " is not updatable: " + e.getMessage());
            }
        }
        Assert.assertEquals("Bad number of updatable software", 20, count);
    }

    @Ignore
    public void guessNextExistingURLAllTest()
    {
        for (final SoftwareDefinition softwareDefinition : SoftwareDefinition.values())
        {
            guessNextExistingURL(softwareDefinition);
        }
    }

    @Ignore
    public void guessNextExistingURLHalfTest()
    {
        for (final SoftwareDefinition softwareDefinition : SoftwareDefinition.values())
        {
            final int softwareValue = softwareDefinition.ordinal() % 2;
            final int dateValue = LocalDate.now().getDayOfWeek().getValue() % 2;
            if (softwareValue == dateValue)
            {
                guessNextExistingURL(softwareDefinition);
            }
        }
    }

    private void guessNextExistingURL(final SoftwareDefinition softwareDefinition)
    {
        try
        {
            System.out.println("Checking nextUrl for: " + softwareDefinition);
            final SoftwareUrl url = nextExistingUpdateService.getUrl(softwareDefinition);
            final SoftwareUrl nextUrl = nextExistingUpdateService.getNextUrl(softwareDefinition);
            if (!nextUrl.getUrl().equals(url.getUrl()))
            {
                System.out.println(softwareDefinition + " URL needs to be updated");
                System.out.println(softwareDefinition.name().toLowerCase() + "=" + nextUrl.getUrl());
            }
        }
        catch (final IOException e)
        {
            System.out.println("Failed to guess nextUrl for: " + softwareDefinition + ", " + e.getMessage());
        }
        catch (final SoftwareException e)
        {
            System.out.println("Failed to guess nextUrl for: " + softwareDefinition + ", " + e.getMessage() + " after " + e.getMaxTries() + " tries");
        }
    }
}
