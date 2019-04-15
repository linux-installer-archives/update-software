package org.grumpyf0x48.liar.update;

import org.grumpyf0x48.liar.update.SoftwareUpdateOptions.SoftwareVersionFieldIncrementPolicy;
import org.grumpyf0x48.liar.update.SoftwareUpdateOptions.SoftwareVersionIncrementPolicy;
import org.grumpyf0x48.liar.update.exceptions.SoftwareException;
import org.grumpyf0x48.liar.update.exceptions.SoftwareVersionNotIncrementableException;
import org.grumpyf0x48.liar.update.exceptions.SoftwareVersionNotParsableException;
import org.junit.Assert;
import org.junit.Test;

public class SoftwareVersionTest
{
    @Test(expected = SoftwareVersionNotParsableException.class)
    public void invalidVersionThrowsException() throws SoftwareVersionNotParsableException
    {
        new SoftwareVersion("a.b.c");
    }

    @Test
    public void validVersionIsParsedCorrectly() throws SoftwareVersionNotParsableException
    {
        final SoftwareVersion version = new SoftwareVersion("1.2");
        Assert.assertEquals((Integer) 1, version.getMajor());
        Assert.assertEquals((Integer) 2, version.getMinor());
        Assert.assertNull(version.getPatch());
        Assert.assertEquals("1.2", version.toString());
    }

    @Test
    public void validVersionWithPatchIsParsedCorrectly() throws SoftwareVersionNotParsableException
    {
        final SoftwareVersion version = new SoftwareVersion("1.2.10");
        Assert.assertEquals((Integer) 1, version.getMajor());
        Assert.assertEquals((Integer) 2, version.getMinor());
        Assert.assertEquals((Integer) 10, version.getPatch());
        Assert.assertEquals("1.2.10", version.toString());
    }

    @Test
    public void equalsTest() throws SoftwareVersionNotParsableException
    {
        final SoftwareVersion version1 = new SoftwareVersion("1.0.0");
        final SoftwareVersion version101 = new SoftwareVersion("1.0.1");
        Assert.assertNotEquals(version1, version101);
    }

    @Test
    public void versionCanIncrementUntilMaxIncrement() throws SoftwareException
    {
        final SoftwareVersionIncrementPolicy versionIncrementPolicy = new SoftwareVersionIncrementPolicy(
                        SoftwareVersionFieldIncrementPolicy.withMaxIncrement(2));

        SoftwareVersion version = new SoftwareVersion("1.0.0", versionIncrementPolicy);
        version = version.getNext();
        Assert.assertEquals("1.0.1", version.toString());
        version = version.getNext();
        Assert.assertEquals("1.0.2", version.toString());
        version = version.getNext();
        Assert.assertEquals("1.1.0", version.toString());
        version = version.getNext();
        Assert.assertEquals("1.1.1", version.toString());
        version = version.getNext();
        Assert.assertEquals("1.1.2", version.toString());
        version = version.getNext();
        Assert.assertEquals("1.2.0", version.toString());
        version = version.getNext();
        Assert.assertEquals("1.2.1", version.toString());
        version = version.getNext();
        Assert.assertEquals("1.2.2", version.toString());
        version = version.getNext();
        Assert.assertEquals("2.0.0", version.toString());
        version = version.getNext();
        Assert.assertEquals("2.0.1", version.toString());
        version = version.getNext();
        Assert.assertEquals("2.0.2", version.toString());
        version = version.getNext();
        Assert.assertEquals("2.1.0", version.toString());
        version = version.getNext();
        Assert.assertEquals("2.1.1", version.toString());
        version = version.getNext();
        Assert.assertEquals("2.1.2", version.toString());
        version = version.getNext();
        Assert.assertEquals("2.2.0", version.toString());
        version = version.getNext();
        Assert.assertEquals("2.2.1", version.toString());
        version = version.getNext();
        Assert.assertEquals("2.2.2", version.toString());
        version = version.getNext();
        Assert.assertEquals("3.0.0", version.toString());
        version = version.getNext();
        Assert.assertEquals("3.0.1", version.toString());
        version = version.getNext();
        Assert.assertEquals("3.0.2", version.toString());
        version = version.getNext();
        Assert.assertEquals("3.1.0", version.toString());
        version = version.getNext();
        Assert.assertEquals("3.1.1", version.toString());
        version = version.getNext();
        Assert.assertEquals("3.1.2", version.toString());
        version = version.getNext();
        Assert.assertEquals("3.2.0", version.toString());
        version = version.getNext();
        Assert.assertEquals("3.2.1", version.toString());
        version = version.getNext();
        Assert.assertEquals("3.2.2", version.toString());
        try
        {
            version.getNext();
            Assert.fail();
        }
        catch (final SoftwareVersionNotIncrementableException e)
        {
            // Expected
        }
    }
}
