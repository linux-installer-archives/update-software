package org.grumpyf0x48.liar.update;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.grumpyf0x48.liar.update.SoftwareUpdateOptions.SoftwareVersionIncrementPolicy;
import org.grumpyf0x48.liar.update.exceptions.SoftwareException;
import org.grumpyf0x48.liar.update.exceptions.SoftwareVersionNotIncrementableException;
import org.grumpyf0x48.liar.update.exceptions.SoftwareVersionNotParsableException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoftwareVersion implements SoftwareIncrementable<SoftwareVersion>
{
    private static final String MAJOR_FIELD = "major";
    private static final String MINOR_FIELD = "minor";
    private static final String PATCH_FIELD = "patch";

    private static final String VERSION_REGEXP = "(?<" + MAJOR_FIELD + ">\\d+)\\.(?<" + MINOR_FIELD + ">\\d+)";
    private static final String VERSION_WITH_PATCH_REGEXP = VERSION_REGEXP + "\\.(?<" + PATCH_FIELD + ">\\d+)";

    private final SoftwareVersionIncrementPolicy versionIncrementPolicy;

    private final Integer initialMajor;
    private Integer initialMinor;
    private Integer initialPatch;

    private Integer major;
    private Integer minor;
    private Integer patch;

    private SoftwareVersionField versionField;

    public SoftwareVersion(final String version) throws SoftwareVersionNotParsableException
    {
        this(version, SoftwareVersionIncrementPolicy.withDefault());
    }

    public SoftwareVersion(final String version, final SoftwareVersionIncrementPolicy versionIncrementPolicy)
                    throws SoftwareVersionNotParsableException
    {
        this.versionIncrementPolicy = versionIncrementPolicy;
        for (final String versionRegexp : getParsableRegexps())
        {
            final Pattern versionPattern = Pattern.compile(versionRegexp);
            final Matcher versionMatcher = versionPattern.matcher(version);
            if (!versionMatcher.matches())
            {
                continue;
            }
            initialMajor = major = Integer.parseInt(versionMatcher.group(MAJOR_FIELD));
            initialMinor = minor = Integer.parseInt(versionMatcher.group(MINOR_FIELD));
            final int groupCount = versionMatcher.groupCount();
            if (groupCount == 3)
            {
                patch = Integer.parseInt(versionMatcher.group(PATCH_FIELD));
                versionField = SoftwareVersionField.PATCH;
            }
            else if (groupCount == 2)
            {
                versionField = SoftwareVersionField.MINOR;
            }
            initialPatch = patch;
            return;
        }

        throw new SoftwareVersionNotParsableException(version + " is not parsable");
    }

    public Integer getMajor()
    {
        return major;
    }

    public Integer getMinor()
    {
        return minor;
    }

    public Integer getPatch()
    {
        return patch;
    }

    @Override
    public SoftwareVersion getNext() throws SoftwareException
    {
        if (versionField == SoftwareVersionField.PATCH)
        {
            if (versionIncrementPolicy.patchIncrementPolicy.canIncrement(initialPatch, patch + 1))
            {
                patch += 1;
                return this;
            }
            initialPatch = patch = 0;
            versionField = SoftwareVersionField.MINOR;
        }

        if (versionField == SoftwareVersionField.MINOR)
        {
            if (versionIncrementPolicy.minorIncrementPolicy.canIncrement(initialMinor, minor + 1))
            {
                minor += 1;
                if (initialPatch != null)
                {
                    versionField = SoftwareVersionField.PATCH;
                }
                return this;
            }
            initialMinor = minor = 0;
            versionField = SoftwareVersionField.MAJOR;
        }

        if (versionField == SoftwareVersionField.MAJOR)
        {
            if (versionIncrementPolicy.majorIncrementPolicy.canIncrement(initialMajor, major + 1))
            {
                major += 1;
                versionField = initialPatch != null ? SoftwareVersionField.PATCH : SoftwareVersionField.MINOR;
                return this;
            }
            versionField = null;
        }

        throw new SoftwareVersionNotIncrementableException("Version: " + toString() + " cannot be incremented");
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        final SoftwareVersion that = (SoftwareVersion) o;

        return new EqualsBuilder() //
                        .append(major, that.major) //
                        .append(minor, that.minor) //
                        .append(patch, that.patch) //
                        .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37) //
                        .append(major) //
                        .append(minor) //
                        .append(patch) //
                        .toHashCode();
    }

    @Override
    public String toString()
    {
        String version = major + "." + minor;
        if (patch != null)
        {
            version += "." + patch;
        }
        return version;
    }

    public static String[] getParsableRegexps()
    {
        return new String[] {
                        VERSION_WITH_PATCH_REGEXP,
                        VERSION_REGEXP
        };
    }

    public enum SoftwareVersionField
    {
        MAJOR, MINOR, PATCH
    }
}
