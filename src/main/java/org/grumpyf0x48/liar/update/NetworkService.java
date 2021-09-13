package org.grumpyf0x48.liar.update;

import java.net.ConnectException;

public interface NetworkService
{
    static NetworkService getInstance()
    {
        return new LegacyNetworkServiceImpl();
    }

    boolean urlExists(final String url) throws ConnectException;
}
