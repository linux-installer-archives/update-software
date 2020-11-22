package org.grumpyf0x48.liar.update;

import org.grumpyf0x48.liar.update.SoftwareUpdateOptions.SoftwareUrlIncrementPolicy;
import org.grumpyf0x48.liar.update.SoftwareUpdateOptions.SoftwareVersionIncrementPolicy;
import org.grumpyf0x48.liar.update.exceptions.SoftwareException;

import java.io.IOException;

public class SoftwareUpdateRepository
{
    public static void main(final String[] args) throws SoftwareException
    {
        if (args.length < 1)
        {
            throw new SoftwareException("Usage: SoftwareUpdateRepository <software resource path> <daily | weekly | monthly>");
        }
        final String softwareResource = args[0];
        final SoftwareUpdatePeriodicity softwareUpdatePeriodicity = args.length > 1 ? SoftwareUpdatePeriodicity.valueOf(args[1].toUpperCase()) : null;
        final boolean skipSoftwareNotFound = args.length > 2 ? Boolean.parseBoolean(args[2]) : false;
        try
        {
            final SoftwareUpdateService updateService = new SoftwareUpdateServiceImpl(softwareResource);
            final SoftwareUpdateOptions updateOptions = new SoftwareUpdateOptions( //
                SoftwareUrlIncrementPolicy.LAST_EXISTING, //
                SoftwareVersionIncrementPolicy.withDefault(), //
                skipSoftwareNotFound);
            updateService.setUpdateOptions(updateOptions);
            updateService.updateSoftwareResource(softwareUpdatePeriodicity);
        }
        catch (final SoftwareException | IOException e)
        {
            throw new SoftwareException(softwareResource + " updated failed", e);
        }
    }
}
