package org.grumpyf0x48.liar.update;

import org.grumpyf0x48.liar.update.SoftwareUpdateOptions.SoftwareUrlIncrementPolicy;
import org.grumpyf0x48.liar.update.SoftwareUpdateOptions.SoftwareVersionFieldIncrementPolicy;
import org.grumpyf0x48.liar.update.SoftwareUpdateOptions.SoftwareVersionIncrementPolicy;
import org.grumpyf0x48.liar.update.exceptions.SoftwareException;
import org.grumpyf0x48.liar.update.exceptions.SoftwareVersionNotIncrementableException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SoftwareUrlTest
{
    private static final String URL = "https://download.jetbrains.com/idea/ideaIU-2018.3.3.tar.gz";
    private static final String NEXT_URL = "https://download.jetbrains.com/idea/ideaIU-2018.3.4.tar.gz";

    private static SoftwareUpdateOptions updateOptions;

    @BeforeClass
    public static void initialize()
    {
        final SoftwareVersionIncrementPolicy versionIncrementPolicy = new SoftwareVersionIncrementPolicy(
                        SoftwareVersionFieldIncrementPolicy.withMaxIncrement(2));
        updateOptions = new SoftwareUpdateOptions(SoftwareUrlIncrementPolicy.NEXT, versionIncrementPolicy);
    }

    @Test
    public void getUrl() throws SoftwareException
    {
        final SoftwareUrl softwareUrl = new SoftwareUrl(URL, updateOptions);
        Assert.assertEquals(softwareUrl.getUrl(), URL);
    }

    @Test
    public void getSoftwareVersion() throws SoftwareException
    {
        final SoftwareUrl softwareUrl = new SoftwareUrl(URL, updateOptions);
        final SoftwareVersion softwareVersion = softwareUrl.getVersion();
        Assert.assertEquals(new SoftwareVersion("2018.3.3"), softwareVersion);
    }

    @Test
    public void urlCanIncrement() throws SoftwareException
    {
        final SoftwareUrl softwareUrl = new SoftwareUrl(URL, updateOptions);
        final SoftwareUrl nextSoftwareUrl = softwareUrl.getNext();
        Assert.assertEquals(NEXT_URL, nextSoftwareUrl.getUrl());
    }

    @Test
    public void urlCanIncrementUntilMaxIncrement() throws SoftwareException
    {
        SoftwareUrl softwareUrl = new SoftwareUrl(URL, updateOptions);
        try
        {
            while ((softwareUrl = softwareUrl.getNext()) != null)
            {
            }
            Assert.fail();
        }
        catch (final SoftwareVersionNotIncrementableException e)
        {
            Assert.assertEquals("Version: 2020.0.0 cannot be incremented", e.getMessage());
        }
    }

    @Test
    public void urlWithoutPatchCanIncrement() throws SoftwareException
    {
        final SoftwareUpdateOptions softwareUpdateOptions = new SoftwareUpdateOptions( //
                        SoftwareUrlIncrementPolicy.NEXT, //
                        SoftwareVersionIncrementPolicy.withDefault());

        final String url = "https://github.com/gohugoio/hugo/releases/download/v0.52/hugo_0.52_Linux-64bit.tar.gz";
        SoftwareUrl softwareUrl = new SoftwareUrl(url, softwareUpdateOptions);

        softwareUrl = softwareUrl.getNext();
        Assert.assertEquals("https://github.com/gohugoio/hugo/releases/download/v0.53/hugo_0.53_Linux-64bit.tar.gz", softwareUrl.getUrl());

        softwareUrl = softwareUrl.getNext();
        Assert.assertEquals("https://github.com/gohugoio/hugo/releases/download/v0.54/hugo_0.54_Linux-64bit.tar.gz", softwareUrl.getUrl());

        softwareUrl = softwareUrl.getNext();
        Assert.assertEquals("https://github.com/gohugoio/hugo/releases/download/v0.55/hugo_0.55_Linux-64bit.tar.gz", softwareUrl.getUrl());

        softwareUrl = softwareUrl.getNext();
        Assert.assertEquals("https://github.com/gohugoio/hugo/releases/download/v1.0/hugo_1.0_Linux-64bit.tar.gz", softwareUrl.getUrl());

        softwareUrl = softwareUrl.getNext();
        Assert.assertEquals("https://github.com/gohugoio/hugo/releases/download/v1.1/hugo_1.1_Linux-64bit.tar.gz", softwareUrl.getUrl());

        softwareUrl = softwareUrl.getNext();
        Assert.assertEquals("https://github.com/gohugoio/hugo/releases/download/v1.2/hugo_1.2_Linux-64bit.tar.gz", softwareUrl.getUrl());

        softwareUrl = softwareUrl.getNext();
        Assert.assertEquals("https://github.com/gohugoio/hugo/releases/download/v1.3/hugo_1.3_Linux-64bit.tar.gz", softwareUrl.getUrl());
    }

    @Test
    public void lastExistingUrlTest() throws SoftwareException
    {
        final SoftwareVersionIncrementPolicy versionIncrementPolicy = SoftwareVersionIncrementPolicy.withDefault();
        final SoftwareUpdateOptions updateOptions = new SoftwareUpdateOptions(SoftwareUrlIncrementPolicy.LAST_EXISTING, versionIncrementPolicy);
        final SoftwareUrl softwareUrl = new SoftwareUrl("https://github.com/jbangdev/jbang/releases/download/v0.28.0/jbang-0.28.0.zip", updateOptions);
        final SoftwareUrl lastSoftwareUrl = softwareUrl.getNext();
        Assert.assertEquals("https://github.com/jbangdev/jbang/releases/download/v0.31.0/jbang-0.31.0.zip", lastSoftwareUrl.getUrl());
    }

    @Test
    public void equalsTest() throws SoftwareException
    {
        final SoftwareUrl softwareUrl = new SoftwareUrl(URL, updateOptions);
        final SoftwareUrl nextSoftwareUrl = new SoftwareUrl(NEXT_URL, updateOptions);
        Assert.assertNotEquals(nextSoftwareUrl, softwareUrl);
    }
}