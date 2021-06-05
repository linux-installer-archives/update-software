package org.grumpyf0x48.liar.update;

import org.grumpyf0x48.liar.update.SoftwareUpdateOptions.SoftwareUrlIncrementPolicy;
import org.grumpyf0x48.liar.update.SoftwareUpdateOptions.SoftwareVersionFieldIncrementPolicy;
import org.grumpyf0x48.liar.update.SoftwareUpdateOptions.SoftwareVersionIncrementPolicy;
import org.grumpyf0x48.liar.update.exceptions.SoftwareException;
import org.grumpyf0x48.liar.update.exceptions.SoftwareVersionNotIncrementableException;
import org.grumpyf0x48.liar.update.exceptions.SoftwareUrlNotParsableException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.ConnectException;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SoftwareUrlTest
{
    private static SoftwareVersionIncrementPolicy versionIncrementPolicy;
    private static SoftwareUpdateOptions updateOptions;
    private static NetworkService networkService;
    private static Function<Integer, String> ideaUrlFunction;
    private static String URL, NEXT_URL;

    @BeforeClass
    public static void initialize()
    {
        versionIncrementPolicy = new SoftwareVersionIncrementPolicy(
                        SoftwareVersionFieldIncrementPolicy.withMaxIncrement(2));
        updateOptions = new SoftwareUpdateOptions(SoftwareUrlIncrementPolicy.NEXT, versionIncrementPolicy);
        networkService = mock(NetworkService.class);
        ideaUrlFunction = (patch) -> String.format("https://download.jetbrains.com/idea/ideaIU-2018.3.%d.tar.gz", patch);
        URL = ideaUrlFunction.apply(3);
        NEXT_URL = ideaUrlFunction.apply(4);
    }

    @Test
    public void getUrl() throws SoftwareException
    {
        final SoftwareUrl softwareUrl = new SoftwareUrl(URL, updateOptions);
        Assert.assertEquals(URL, softwareUrl.getUrl());
    }

    @Test(expected = SoftwareUrlNotParsableException.class)
    public void notParsableUrl() throws SoftwareException
    {
        new SoftwareUrl("https://downloads.apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz", updateOptions);
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
            do
            {
                softwareUrl = softwareUrl.getNext();
            }
            while (softwareUrl != null);
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

        final BiFunction<Integer, Integer, String> urlFunction = (major, minor) -> String.format("https://github.com/gohugoio/hugo/releases/download/v%d.%d/hugo_%d.%d_Linux-64bit.tar.gz", major, minor, major, minor);
        final String url = urlFunction.apply(0, 52);
        SoftwareUrl softwareUrl = new SoftwareUrl(url, softwareUpdateOptions);

        softwareUrl = softwareUrl.getNext();
        Assert.assertEquals(urlFunction.apply(0, 53), softwareUrl.getUrl());

        softwareUrl = softwareUrl.getNext();
        Assert.assertEquals(urlFunction.apply(0, 54), softwareUrl.getUrl());

        softwareUrl = softwareUrl.getNext();
        Assert.assertEquals(urlFunction.apply(0, 55), softwareUrl.getUrl());

        softwareUrl = softwareUrl.getNext();
        Assert.assertEquals(urlFunction.apply(1, 0), softwareUrl.getUrl());

        softwareUrl = softwareUrl.getNext();
        Assert.assertEquals(urlFunction.apply(1, 1), softwareUrl.getUrl());

        softwareUrl = softwareUrl.getNext();
        Assert.assertEquals(urlFunction.apply(1, 2), softwareUrl.getUrl());

        softwareUrl = softwareUrl.getNext();
        Assert.assertEquals(urlFunction.apply(1, 3), softwareUrl.getUrl());
    }

    @Test
    public void lastExistingUrlTest() throws SoftwareException, ConnectException {
        final SoftwareUpdateOptions nextExistingUpdateOptions = new SoftwareUpdateOptions(SoftwareUrlIncrementPolicy.LAST_EXISTING, versionIncrementPolicy);
        final String initialUrl = ideaUrlFunction.apply(3);
        final SoftwareUrl softwareUrl = new SoftwareUrl(null, initialUrl, nextExistingUpdateOptions, networkService);
        when(networkService.urlExists(initialUrl)).thenReturn(true);
        when(networkService.urlExists(ideaUrlFunction.apply(4))).thenReturn(true);
        when(networkService.urlExists(ideaUrlFunction.apply(5))).thenReturn(true);
        final SoftwareUrl nextSoftwareUrl = softwareUrl.getNext();
        Assert.assertEquals(ideaUrlFunction.apply(5), nextSoftwareUrl.getUrl());
    }

    @Test
    public void equalsTest() throws SoftwareException
    {
        final SoftwareUrl softwareUrl = new SoftwareUrl(URL, updateOptions);
        final SoftwareUrl nextSoftwareUrl = new SoftwareUrl(NEXT_URL, updateOptions);
        Assert.assertNotEquals(nextSoftwareUrl, softwareUrl);
    }
}