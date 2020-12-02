package org.grumpyf0x48.liar.update;

import org.apache.commons.io.FileUtils;
import org.grumpyf0x48.liar.update.exceptions.SoftwareException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SoftwareUpdateRepositoryTest
{
    @Test
    public void emptyParametersTest()
    {
        try
        {
            SoftwareUpdateRepository.main(new String[]{});
            Assert.fail();
        }
        catch (final SoftwareException exception)
        {
            Assert.assertEquals("Usage: SoftwareUpdateRepository <software resource path> <daily | weekly | monthly>", exception.getMessage());
        }
    }

    @Test
    public void softwareResourceNotFoundTest()
    {
        try
        {
            SoftwareUpdateRepository.main(new String[]{ "file", "DAILY" });
            Assert.fail();
        }
        catch (final SoftwareException exception)
        {
            Assert.assertEquals(FileNotFoundException.class, exception.getCause().getClass());
        }
    }

    @Test
    public void invalidPeriodicityTest() throws SoftwareException
    {
        try
        {
            SoftwareUpdateRepository.main(new String[]{ "file", "invalidPerodicity" });
            Assert.fail();
        }
        catch (final IllegalArgumentException exception)
        {
            Assert.assertEquals("No enum constant org.grumpyf0x48.liar.update.SoftwareUpdatePeriodicity.INVALIDPERODICITY", exception.getMessage());
        }
    }

    @Test
    public void matchingPeriodicityTest() throws IOException, SoftwareException
    {
        final File tempFile = File.createTempFile("matchingPeriodicityTest", ".properties");
        final String content = "atom=https://github.com/atom/atom/releases/download/v1.51.0/atom-amd64.tar.gz";
        FileUtils.writeStringToFile(tempFile, content, StandardCharsets.UTF_8);
        SoftwareUpdateRepository.main(new String[] { tempFile.getAbsolutePath() , "DAILY", "true" });
        final String updatedContent = FileUtils.readFileToString(tempFile, StandardCharsets.UTF_8);
        Assert.assertNotEquals("URL should have been updated", content,updatedContent);
    }

    @Test
    public void nonMatchingPeriodicityTest() throws IOException, SoftwareException
    {
        final File tempFile = File.createTempFile("nonMatchingPeriodicityTest", ".properties");
        final String content = "atom=https://github.com/atom/atom/releases/download/v1.51.0/atom-amd64.tar.gz";
        FileUtils.writeStringToFile(tempFile, content, StandardCharsets.UTF_8);
        SoftwareUpdateRepository.main(new String[] { tempFile.getAbsolutePath() , "WEEKLY", "true" });
        final String updatedContent = FileUtils.readFileToString(tempFile, StandardCharsets.UTF_8);
        Assert.assertEquals("URL should not have been updated", content,updatedContent);
    }
}