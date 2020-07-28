package org.grumpyf0x48.liar.update;

import org.grumpyf0x48.liar.update.SoftwareUpdateOptions.SoftwareUrlIncrementPolicy;
import org.grumpyf0x48.liar.update.SoftwareUpdateOptions.SoftwareVersionIncrementPolicy;
import org.grumpyf0x48.liar.update.exceptions.SoftwareException;
import org.grumpyf0x48.misc.NetworkUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
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
        final Properties softwareProperties = softwareUpdateService.getSoftwareProperties();
        checkSoftwareProperties(softwareProperties);
    }

    @Test(expected = FileNotFoundException.class)
    public void getSoftwareResourceNotInClassPath() throws IOException
    {
        final SoftwareUpdateService softwareUpdateService = new SoftwareUpdateServiceImpl("/not_found");
        softwareUpdateService.getSoftwareProperties();
    }

    @Test
    public void getSoftwareResourceInFilePath() throws IOException
    {
        final Properties softwareProperties = nextUpdateService.getSoftwareProperties();
        checkSoftwareProperties(softwareProperties);
    }

    @Test
    public void getSoftwareFilePathTest()
    {
        final String softwareFilePath = nextUpdateService.getSoftwareResource();
        Assert.assertEquals(System.getProperty("user.home") + "/.liar_software", softwareFilePath);
    }

    @Test
    public void getSoftwareListTest() throws IOException
    {
        final String[] softwareList = nextUpdateService.getSoftwareList();
        Assert.assertEquals(57, softwareList.length);
    }

    @Test
    public void updateSoftwareResourceTest() throws IOException, SoftwareException
    {
        final File testDir = new File("src/test/resources");
        final File initialSoftwareFile = new File(testDir, ".liar_software");
        final File updatedSoftwareFile = new File(testDir, initialSoftwareFile.getName() + ".updated");
        updatedSoftwareFile.deleteOnExit();
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
    public void getUrlTest() throws IOException, SoftwareException
    {
        for (final SoftwareDefinition software : SoftwareDefinition.values())
        {
            final SoftwareUrl url = nextUpdateService.getUrl(software);
            Assert.assertTrue("URL " + url + " for software: " + software + " does not exist",
                            NetworkUtils.urlExists(url.getUrl()));
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
    public void getNextUrlGraalVmTest() throws SoftwareException
    {
        final SoftwareUrl initialUrl = new SoftwareUrl(SoftwareDefinition.GRAALVM, "https://github.com/oracle/graal/releases/download/vm-19.2.0/graalvm-ce-linux-amd64-19.2.0.tar.gz");
        final SoftwareUrl nextUrl = nextUpdateService.getNextUrl(initialUrl);
        Assert.assertEquals("https://github.com/oracle/graal/releases/download/vm-19.2.1/graalvm-ce-linux-amd64-19.2.1.tar.gz", nextUrl.getUrl());
    }

    @Test
    public void getNextUrlIdeaTest() throws SoftwareException
    {
        final SoftwareUrl initialUrl = new SoftwareUrl(SoftwareDefinition.IDEA_COMMUNITY, "https://download.jetbrains.com/idea/ideaIC-2019.2.tar.gz");
        final SoftwareUrl nextUrl = nextUpdateService.getNextUrl(initialUrl);
        Assert.assertEquals("https://download.jetbrains.com/idea/ideaIC-2019.3.tar.gz", nextUrl.getUrl());
    }

    @Test
    public void getNextUrlJbangTest() throws SoftwareException
    {
        final SoftwareUrl initialUrl = new SoftwareUrl(SoftwareDefinition.JBANG, "https://github.com/jbangdev/jbang/releases/download/v0.25.0/jbang-0.25.0.zip");
        final SoftwareUrl nextUrl = nextUpdateService.getNextUrl(initialUrl);
        Assert.assertEquals("https://github.com/jbangdev/jbang/releases/download/v0.25.1/jbang-0.25.1.zip", nextUrl.getUrl());
    }

    @Test
    public void getNextUrlXsvTest() throws SoftwareException
    {
        final SoftwareUrl initialUrl = new SoftwareUrl(SoftwareDefinition.XSV, "https://github.com/BurntSushi/xsv/releases/download/0.13.0/xsv-0.13.0-x86_64-unknown-linux-musl.tar.gz");
        final SoftwareUrl nextUrl = nextUpdateService.getNextUrl(initialUrl);
        Assert.assertEquals("https://github.com/BurntSushi/xsv/releases/download/0.13.1/xsv-0.13.1-x86_64-unknown-linux-musl.tar.gz", nextUrl.getUrl());
    }

    @Test
    public void getNextUrlSpringTest() throws SoftwareException
    {
        final SoftwareUrl initialUrl = new SoftwareUrl(SoftwareDefinition.SPRING, "https://repo.spring.io/release/org/springframework/boot/spring-boot-cli/2.3.0.RELEASE/spring-boot-cli-2.3.0.RELEASE-bin.zip");
        final SoftwareUrl nextUrl = nextUpdateService.getNextUrl(initialUrl);
        Assert.assertEquals("https://repo.spring.io/release/org/springframework/boot/spring-boot-cli/2.3.1.RELEASE/spring-boot-cli-2.3.1.RELEASE-bin.zip", nextUrl.getUrl());
    }

    @Test
    public void getNextExistingURLTest() throws SoftwareException
    {
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
        Assert.assertEquals("Bad number of updatable software", 28, count);
    }

    private static void checkSoftwareProperties(final Properties softwareProperties)
    {
        Assert.assertTrue(softwareProperties.containsKey("software_list"));
        Assert.assertTrue(softwareProperties.containsKey("gradle"));
        Assert.assertTrue(softwareProperties.containsKey("idea_ultimate"));
    }
}
