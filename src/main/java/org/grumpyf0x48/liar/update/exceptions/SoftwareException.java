package org.grumpyf0x48.liar.update.exceptions;

public class SoftwareException extends Exception
{
    private final int maxTries;

    public SoftwareException(final String message)
    {
        super(message);
        this.maxTries = -1;
    }

    public SoftwareException(final String message, final Throwable cause)
    {
        super(message, cause);
        this.maxTries = -1;
    }

    public SoftwareException(final String message, final Throwable cause, final int maxTries)
    {
        super(message, cause);
        this.maxTries = maxTries;
    }

    public int getMaxTries()
    {
        return maxTries;
    }

    public static SoftwareException from(final SoftwareException softwareException, final int maxTries)
    {
        return new SoftwareException(softwareException.getMessage(), softwareException.getCause(), maxTries);
    }
}
