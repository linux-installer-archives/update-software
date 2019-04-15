package org.grumpyf0x48.liar.update;

import org.grumpyf0x48.liar.update.exceptions.SoftwareException;

import java.io.File;
import java.io.IOException;

public class SoftwareUpdateRepository
{
    public static void main(final String[] args) throws SoftwareException
    {
        String softwareResource;
        if (args.length > 0)
        {
            softwareResource = args[0];
        }
        else
        {
            final File softwareFile = new File("/home/pyfourmond/Sources/liar", ".liar_software");
            softwareResource = softwareFile.getAbsolutePath();
        }

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
