package org.grumpyf0x48.liar.update.exceptions;

public class SoftwareExceptionFactory
{
    private SoftwareExceptionFactory()
    {
    }

    public static SoftwareException build(final SoftwareException softwareException, final int maxTries)
    {
        if (softwareException instanceof SoftwareNotFoundException)
        {
            return new SoftwareNotFoundException(softwareException.getMessage(), softwareException.getCause(), maxTries);
        }
        else if (softwareException instanceof SoftwareUrlNotParsableException)
        {
            return new SoftwareUrlNotParsableException(softwareException.getMessage(), softwareException.getCause(), maxTries);
        }
        else if (softwareException instanceof SoftwareVersionNotIncrementableException)
        {
            return new SoftwareVersionNotIncrementableException(softwareException.getMessage(), softwareException.getCause(), maxTries);
        }
        else if (softwareException instanceof SoftwareVersionNotParsableException)
        {
            return new SoftwareVersionNotParsableException(softwareException.getMessage(), softwareException.getCause(), maxTries);
        }
        else
        {
            return new SoftwareException(softwareException.getMessage(), softwareException.getCause(), maxTries);
        }
    }
}
