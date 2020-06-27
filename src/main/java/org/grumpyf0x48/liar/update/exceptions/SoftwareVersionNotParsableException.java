package org.grumpyf0x48.liar.update.exceptions;

public class SoftwareVersionNotParsableException extends SoftwareException
{
    public SoftwareVersionNotParsableException(final String message)
    {
        super(message);
    }

    public SoftwareVersionNotParsableException(final String message, final Throwable cause, final int maxTries)
    {
        super(message, cause, maxTries);
    }
}
