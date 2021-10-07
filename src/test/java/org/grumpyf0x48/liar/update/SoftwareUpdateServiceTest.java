package org.grumpyf0x48.liar.update;

import org.grumpyf0x48.liar.update.SoftwareUpdateOptions.SoftwareUrlIncrementPolicy;
import org.grumpyf0x48.liar.update.SoftwareUpdateOptions.SoftwareVersionIncrementPolicy;
import org.grumpyf0x48.liar.update.exceptions.SoftwareException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import static org.apache.commons.io.FileUtils.contentEquals;
import static org.apache.commons.io.FileUtils.copyFile;

public class SoftwareUpdateServiceTest
{
    private static SoftwareUpdateService nextUpdateService;
    private static SoftwareUpdateService nextExistingUpdateService;
    private static SoftwareUpdateService lastExistingUpdateService;
    private static SoftwareUpdateOptions nextSoftwareUpdateOptions;

    @BeforeClass
    public static void initialize()
    {
        nextUpdateService = getSoftwareUpdateService();

        nextSoftwareUpdateOptions = new SoftwareUpdateOptions( //
                        SoftwareUrlIncrementPolicy.NEXT, //
                        SoftwareVersionIncrementPolicy.withDefault());

        nextUpdateService.setUpdateOptions(nextSoftwareUpdateOptions);

        nextExistingUpdateService = getSoftwareUpdateService();
        lastExistingUpdateService = getLastExistingSoftwareUpdateService();
    }

    private static SoftwareUpdateService getLastExistingSoftwareUpdateService()
    {
        final SoftwareUpdateService updateService = new SoftwareUpdateServiceImpl("./target/test-classes/liar-software");
        final SoftwareUpdateOptions updateOptions = new SoftwareUpdateOptions( //
                SoftwareUrlIncrementPolicy.LAST_EXISTING, //
                SoftwareVersionIncrementPolicy.withDefault(), //
                false);
        updateService.setUpdateOptions(updateOptions);
        return updateService;
    }

    @Test
    public void getSoftwareResourceInClassPath() throws IOException
    {
        final SoftwareUpdateService softwareUpdateService = new SoftwareUpdateServiceImpl("/liar-software");
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
        Assert.assertEquals("./target/test-classes/liar-software", softwareFilePath);
    }

    @Test
    public void getSoftwareListTest() throws IOException
    {
        final String[] softwareList = nextUpdateService.getSoftwareList();
        Assert.assertEquals(83, softwareList.length);
    }

    @Test
    public void updateSoftwareResourceTest() throws IOException, SoftwareException
    {
        final File testDir = new File("src/test/resources");
        final File initialSoftwareFile = new File(testDir, "liar-software");
        final File updatedSoftwareFile = new File(testDir, initialSoftwareFile.getName() + ".updated");
        updatedSoftwareFile.deleteOnExit();
        copyFile(initialSoftwareFile, updatedSoftwareFile);

        final SoftwareUpdateService softwareUpdateService = new SoftwareUpdateServiceImpl(updatedSoftwareFile.getAbsolutePath());
        softwareUpdateService.setUpdateOptions(nextSoftwareUpdateOptions);
        final boolean updated = softwareUpdateService.updateSoftwareResource(null);

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
        final LegacyNetworkServiceImpl networkService = new LegacyNetworkServiceImpl();
        for (final SoftwareDefinition software : SoftwareDefinition.values())
        {
            if (software == SoftwareDefinition.TORBROWSER)
            {
                // Old TORBROWSER URL are removed :-(
                continue;
            }
            final SoftwareUrl url = nextUpdateService.getUrl(software);
            Assert.assertTrue("URL " + url + " for software: " + software + " does not exist",
                            networkService.urlExists(url.getUrl()));
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
        final SoftwareUrl initialUrl = new SoftwareUrl(SoftwareDefinition.GRAALVM8, "https://github.com/oracle/graal/releases/download/vm-19.2.0/graalvm-ce-linux-amd64-19.2.0.tar.gz");
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
    public void getNextUrlMongoDBTest() throws SoftwareException
    {
        final SoftwareUrl initialUrl = new SoftwareUrl(SoftwareDefinition.MONGODB, "https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-debian10-4.4.1.tgz");
        final SoftwareUrl nextUrl = nextUpdateService.getNextUrl(initialUrl);
        Assert.assertEquals("https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-debian10-4.4.2.tgz", nextUrl.getUrl());
    }

    @Test
    public void getNextUrlEvansTest() throws SoftwareException
    {
        final SoftwareUrl initialUrl = new SoftwareUrl(SoftwareDefinition.EVANS, "https://github.com/ktr0731/evans/releases/download/0.8.4/evans_linux_amd64.tar.gz");
        final SoftwareUrl nextUrl = nextUpdateService.getNextUrl(initialUrl);
        Assert.assertEquals("https://github.com/ktr0731/evans/releases/download/0.8.5/evans_linux_amd64.tar.gz", nextUrl.getUrl());
    }

    @Test
    public void getNextUrlMavenTest() throws SoftwareException
    {
        final SoftwareUrl initialUrl = new SoftwareUrl(SoftwareDefinition.MAVEN, "http://apache.mediamirrors.org/maven/maven-3/3.6.0/binaries/apache-maven-3.6.0-bin.tar.gz");
        final SoftwareUrl nextUrl = nextUpdateService.getNextUrl(initialUrl);
        Assert.assertEquals("http://apache.mediamirrors.org/maven/maven-3/3.6.1/binaries/apache-maven-3.6.1-bin.tar.gz", nextUrl.getUrl());
    }

    @Test
    public void getNextExistingUrlAtomTest() throws SoftwareException
    {
        final SoftwareUrl initialAtomUrl = new SoftwareUrl(SoftwareDefinition.ATOM, "https://github.com/atom/atom/releases/download/v1.32.2/atom-amd64.tar.gz");
        final SoftwareUrl nextAtomUrl = nextExistingUpdateService.getNextUrl(initialAtomUrl);
        Assert.assertNotEquals(nextAtomUrl.getUrl(), initialAtomUrl.getUrl());
        Assert.assertEquals("https://github.com/atom/atom/releases/download/v1.33.0/atom-amd64.tar.gz", nextAtomUrl.getUrl());
        Assert.assertEquals(SoftwareDefinition.ATOM, nextAtomUrl.getSoftware());
        Assert.assertEquals("1.33.0", nextAtomUrl.getVersion().toString());
    }

    // Quand ce test échouera à la sortie d'une nouvelle version
    // Mocker le NetworkService pour stabiliser le test
    @Test
    public void getLastExistingUrlEasyRsaTest() throws SoftwareException
    {
        final SoftwareUrl initialUrl = new SoftwareUrl(SoftwareDefinition.EASY_RSA, "https://github.com/OpenVPN/easy-rsa/releases/download/v3.0.7/EasyRSA-3.0.7.tgz");
        final SoftwareUrl nextUrl = lastExistingUpdateService.getNextUrl(initialUrl);
        Assert.assertEquals("https://github.com/OpenVPN/easy-rsa/releases/download/v3.0.8/EasyRSA-3.0.8.tgz", nextUrl.getUrl());
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
        Assert.assertEquals("Bad number of updatable software", 54, count);
    }

    private static SoftwareUpdateService getSoftwareUpdateService()
    {
        return new SoftwareUpdateServiceImpl("./target/test-classes/liar-software");
    }

    private static void checkSoftwareProperties(final Properties softwareProperties)
    {
        Assert.assertTrue(softwareProperties.containsKey("software_list"));
        Assert.assertTrue(softwareProperties.containsKey("gradle"));
        Assert.assertTrue(softwareProperties.containsKey("idea_ultimate"));
    }
}
