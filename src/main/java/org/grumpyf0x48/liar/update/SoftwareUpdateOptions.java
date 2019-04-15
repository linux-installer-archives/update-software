package org.grumpyf0x48.liar.update;

public class SoftwareUpdateOptions
{
    final SoftwareUrlIncrementPolicy urlIncrementPolicy;
    final SoftwareVersionIncrementPolicy versionIncrementPolicy;

    public SoftwareUpdateOptions(final SoftwareUrlIncrementPolicy urlIncrementPolicy,
                    final SoftwareVersionIncrementPolicy versionIncrementPolicy)
    {
        this.urlIncrementPolicy = urlIncrementPolicy;
        this.versionIncrementPolicy = versionIncrementPolicy;
    }

    public static SoftwareUpdateOptions withDefault()
    {
        return new SoftwareUpdateOptions( //
                        SoftwareUrlIncrementPolicy.NEXT_EXISTING, //
                        SoftwareVersionIncrementPolicy.withDefault());
    }

    public enum SoftwareUrlIncrementPolicy
    {
        NEXT, NEXT_EXISTING
    }

    public static class SoftwareVersionFieldIncrementPolicy
    {
        private final Integer maxIncrement;
        private final Integer maxValue;

        private SoftwareVersionFieldIncrementPolicy(final Integer maxIncrement, final Integer maxValue)
        {
            this.maxIncrement = maxIncrement;
            this.maxValue = maxValue;
        }

        public boolean canIncrement(final Integer initialValue, final Integer nextValue)
        {
            return (maxIncrement == null || nextValue - initialValue <= maxIncrement)
                            && (maxValue == null || nextValue <= maxValue);
        }

        public static SoftwareVersionFieldIncrementPolicy withMaxIncrement(final Integer maxIncrement)
        {
            return new SoftwareVersionFieldIncrementPolicy(maxIncrement, null);
        }

        public static SoftwareVersionFieldIncrementPolicy withMaxValue(final Integer maxValue)
        {
            return new SoftwareVersionFieldIncrementPolicy(null, maxValue);
        }
    }

    public static class SoftwareVersionIncrementPolicy
    {
        final SoftwareVersionFieldIncrementPolicy majorIncrementPolicy;
        final SoftwareVersionFieldIncrementPolicy minorIncrementPolicy;
        final SoftwareVersionFieldIncrementPolicy patchIncrementPolicy;

        public SoftwareVersionIncrementPolicy(final SoftwareVersionFieldIncrementPolicy commonIncrementPolicy)
        {
            this(commonIncrementPolicy, commonIncrementPolicy, commonIncrementPolicy);
        }

        SoftwareVersionIncrementPolicy( //
                        final SoftwareVersionFieldIncrementPolicy majorIncrementPolicy,
                        final SoftwareVersionFieldIncrementPolicy minorIncrementPolicy,
                        final SoftwareVersionFieldIncrementPolicy patchIncrementPolicy)
        {
            this.majorIncrementPolicy = majorIncrementPolicy;
            this.minorIncrementPolicy = minorIncrementPolicy;
            this.patchIncrementPolicy = patchIncrementPolicy;
        }

        public static SoftwareVersionIncrementPolicy withDefault()
        {
            return new SoftwareVersionIncrementPolicy( //
                            SoftwareVersionFieldIncrementPolicy.withMaxIncrement(1), //
                            SoftwareVersionFieldIncrementPolicy.withMaxIncrement(3), //
                            SoftwareVersionFieldIncrementPolicy.withMaxIncrement(5));
        }
    }
}
