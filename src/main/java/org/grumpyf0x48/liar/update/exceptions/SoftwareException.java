package org.grumpyf0x48.liar.update.exceptions;

public class SoftwareException extends Exception
{
    private int maxTries;

    public SoftwareException(final String message)
    {
        super(message);
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
