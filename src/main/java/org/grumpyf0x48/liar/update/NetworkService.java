package org.grumpyf0x48.liar.update;

import java.net.ConnectException;

public interface NetworkService
{
    boolean urlExists(final String url) throws ConnectException;
}
