package org.grumpyf0x48.liar.update;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class LegacyNetworkServiceImpl implements NetworkService
{
    @Override
    public boolean urlExists(final String url) throws ConnectException
    {
        try
        {
            final URL siteURL = new URL(url);
            final HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.connect();
            final int code = connection.getResponseCode();
            if (code == 200 || code == 301 || code == 302)
            {
                return true;
            }
        }
        catch (final ConnectException connectException)
        {
            throw connectException;
        }
        catch (final IOException ioException)
        {
            // Ignore
        }
        return false;
    }
}
