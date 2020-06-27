package org.grumpyf0x48.liar.update.exceptions;

public class SoftwareVersionNotIncrementableException extends SoftwareException
{
    public SoftwareVersionNotIncrementableException(final String message)
    {
        super(message);
    }

    public SoftwareVersionNotIncrementableException(final String message, final Throwable cause, final int maxTries)
    {
        super(message, cause, maxTries);
    }
}
