package org.grumpyf0x48.liar.update;

import org.grumpyf0x48.liar.update.exceptions.SoftwareException;

import java.io.IOException;

public class SoftwareUpdateRepository
{
    public static void main(final String[] args) throws SoftwareException
    {
        if (args.length == 0)
        {
            throw new SoftwareException("Missing softwareResource");
        }

        final String softwareResource = args[0];
        try
        {
            final SoftwareUpdateService updateService = new SoftwareUpdateServiceImpl(softwareResource);
            updateService.updateSoftwareResource();
        }
        catch (final SoftwareException | IOException e)
        {
            throw new SoftwareException(softwareResource + " updated failed");
        }
    }
}
