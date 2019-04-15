package org.grumpyf0x48.liar.update;

import org.grumpyf0x48.liar.update.SoftwareUpdateOptions.SoftwareVersionFieldIncrementPolicy;
import org.junit.Assert;
import org.junit.Test;

public class SoftwareVersionFieldIncrementPolicyTest
{
    @Test
    public void canIncrementWithNullMaxIncrement()
    {
        final SoftwareVersionFieldIncrementPolicy incrementPolicy = SoftwareVersionFieldIncrementPolicy
                        .withMaxIncrement(null);
        Assert.assertTrue(incrementPolicy.canIncrement(1, 2));
    }

    @Test
    public void canIncrementBeforeMaxIncrement()
    {
        final SoftwareVersionFieldIncrementPolicy incrementPolicy = SoftwareVersionFieldIncrementPolicy
                        .withMaxIncrement(10);
        Assert.assertTrue(incrementPolicy.canIncrement(1, 11));
    }

    @Test
    public void cantIncrementAfterMaxIncrement()
    {
        final SoftwareVersionFieldIncrementPolicy incrementPolicy = SoftwareVersionFieldIncrementPolicy
                        .withMaxIncrement(10);
        Assert.assertFalse(incrementPolicy.canIncrement(1, 12));
    }

    @Test
    public void canIncrementWithNullMaxValue()
    {
        final SoftwareVersionFieldIncrementPolicy incrementPolicy = SoftwareVersionFieldIncrementPolicy
                        .withMaxValue(null);
        Assert.assertTrue(incrementPolicy.canIncrement(1, 2));
    }

    @Test
    public void canIncrementBeforeMaxValue()
    {
        final SoftwareVersionFieldIncrementPolicy incrementPolicy = SoftwareVersionFieldIncrementPolicy
                        .withMaxValue(5);
        Assert.assertTrue(incrementPolicy.canIncrement(1, 5));
    }

    @Test
    public void cantIncrementAfterMaxValue()
    {
        final SoftwareVersionFieldIncrementPolicy incrementPolicy = SoftwareVersionFieldIncrementPolicy
                        .withMaxValue(5);
        Assert.assertFalse(incrementPolicy.canIncrement(1, 6));
    }
}