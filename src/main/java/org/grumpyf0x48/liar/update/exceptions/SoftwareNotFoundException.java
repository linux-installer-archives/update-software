package org.grumpyf0x48.liar.update.exceptions;

public class SoftwareNotFoundException extends SoftwareException
{
    public SoftwareNotFoundException(final String message)
    {
        super(message);
    }

    public SoftwareNotFoundException(final String message, final Throwable cause, final int maxTries)
    {
        super(message, cause, maxTries);
    }
}
