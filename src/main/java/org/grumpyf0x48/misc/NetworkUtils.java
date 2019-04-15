package org.grumpyf0x48.misc;

import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils
{
    private NetworkUtils()
    {
    }

    public static boolean urlExists(final String url)
    {
        try
        {
            final URL siteURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.connect();
            final int code = connection.getResponseCode();
            if (code == 200 || code == 301 || code == 302)
            {
                return true;
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
