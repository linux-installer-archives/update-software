package org.grumpyf0x48.liar.update;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class NetworkServiceImpl implements NetworkService
{
    private final HttpClient client;

    public NetworkServiceImpl()
    {
        client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(5000))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
    }

    public int statusCode(final String url) throws IOException, URISyntaxException, InterruptedException
    {
        final HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .build();
        final HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        return response.statusCode();
    }

    @Override
    public boolean urlExists(final String url) throws ConnectException
    {
        try
        {
            return (statusCode(url) == 200);
        }
        catch (final IOException e)
        {
            throw new ConnectException(e.getMessage());
        }
        catch (final URISyntaxException e)
        {
            throw new ConnectException(e.getMessage());
        }
        catch (final InterruptedException e)
        {
            throw new ConnectException(e.getMessage());
        }
    }
}
