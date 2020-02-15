package org.grumpyf0x48.liar.update.exceptions;

public class SoftwareException extends Exception
{
    private int maxTries;

    public SoftwareException(final String message)
    {
        super(message);
    }

    public SoftwareException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public int getMaxTries()
    {
        return maxTries;
    }

    public SoftwareException setMaxTries(final int maxTries)
    {
        this.maxTries = maxTries;
        return this;
    }
}
