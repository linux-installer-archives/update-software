package org.grumpyf0x48.liar.update.exceptions;

public class SoftwareUrlNotParsableException extends SoftwareException
{
    public SoftwareUrlNotParsableException(final String message)
    {
        super(message);
    }

    public SoftwareUrlNotParsableException(final String message, final Throwable cause, final int maxTries)
    {
        super(message, cause, maxTries);
    }
}
