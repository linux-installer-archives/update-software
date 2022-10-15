package org.grumpyf0x48.liar.update.cli;

import org.grumpyf0x48.liar.update.NetworkService;
import org.grumpyf0x48.liar.update.SoftwareUpdateService;
import org.grumpyf0x48.liar.update.SoftwareUpdateServiceImpl;
import org.grumpyf0x48.liar.update.exceptions.SoftwareException;

import java.io.IOException;

public class SoftwareCheckRepository
{
    public static void main(String[] args) throws SoftwareException
    {
        if (args.length < 1)
        {
            throw new SoftwareException("Usage: SoftwareCheckRepository <software resource path>");
        }
        final String softwareResource = args[0];
        final SoftwareUpdateService updateService = new SoftwareUpdateServiceImpl(softwareResource, NetworkService.getInstance());
        try
        {
            final boolean check = updateService.checkSoftwareResource();
            System.exit(check ? 1 : 0);
        }
        catch (final SoftwareException | IOException e)
        {
            throw new SoftwareException(softwareResource + " check failed", e);
        }
    }
}
