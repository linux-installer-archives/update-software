package org.grumpyf0x48.liar.update;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.ConnectException;

public class LegacyNetworkServiceImplTest
{
    static NetworkService networkService;

    @BeforeClass
    public static void initService()
    {
        networkService = new LegacyNetworkServiceImpl();
    }

    @Test
    public void urlExistsTest() throws ConnectException
    {
        Assert.assertTrue(networkService.urlExists("https://framasoft.org"));
    }

    @Test
    public void urlDoesNotExistTest() throws ConnectException
    {
        Assert.assertFalse(networkService.urlExists("https://framasoft.org/not-found"));
    }
}