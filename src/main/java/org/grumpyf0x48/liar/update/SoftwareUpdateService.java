package org.grumpyf0x48.liar.update;

import org.grumpyf0x48.liar.update.exceptions.SoftwareException;

import java.io.IOException;
import java.util.Properties;

public interface SoftwareUpdateService
{
    static SoftwareUpdateService getInstance()
    {
        return new SoftwareUpdateServiceImpl();
    }

    String getSoftwareResource();

    boolean updateSoftwareResource() throws IOException, SoftwareException;

    SoftwareUpdateOptions getUpdateOptions();

    void setUpdateOptions(final SoftwareUpdateOptions updateOptions);

    Properties getSoftwareProperties() throws IOException;

    String[] getSoftwareList() throws IOException;

    SoftwareUrl getUrl(final SoftwareDefinition software) throws IOException, SoftwareException;

    SoftwareUrl getNextUrl(final SoftwareDefinition software) throws IOException, SoftwareException;

    SoftwareUrl getNextUrl(final SoftwareUrl softwareUrl) throws SoftwareException;
}
