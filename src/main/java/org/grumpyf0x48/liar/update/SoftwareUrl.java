package org.grumpyf0x48.liar.update;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.grumpyf0x48.liar.update.exceptions.SoftwareException;
import org.grumpyf0x48.liar.update.exceptions.SoftwareUrlNotParsableException;
import org.grumpyf0x48.misc.NetworkUtils;

import java.net.ConnectException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoftwareUrl implements SoftwareIncrementable<SoftwareUrl>
{
    private final SoftwareDefinition software;
    private final SoftwareVersion version;
    private final SoftwareUpdateOptions updateOptions;

    private String url;

    public SoftwareUrl(final SoftwareDefinition software, final String url) throws SoftwareException
    {
        this(software, url, SoftwareUpdateOptions.withDefault());
    }

    public SoftwareUrl(final String url, final SoftwareUpdateOptions updateOptions) throws SoftwareException
    {
        this(null, url, updateOptions);
    }

    public SoftwareUrl(final SoftwareDefinition software, final String url, final SoftwareUpdateOptions updateOptions) throws SoftwareException
    {
        this.software = software;
        this.version = parse(url, updateOptions);
        this.url = url;
        this.updateOptions = updateOptions;
    }

    public SoftwareDefinition getSoftware()
    {
        return software;
    }

    public SoftwareVersion getVersion()
    {
        return version;
    }

    public String getUrl()
    {
        return url;
    }

    @Override
    public SoftwareUrl getNext() throws SoftwareException
    {
        getNextUrl();

        if (updateOptions.urlIncrementPolicy == SoftwareUpdateOptions.SoftwareUrlIncrementPolicy.NEXT_EXISTING)
        {
            int maxTries = 0;
            try
            {
                while (!NetworkUtils.urlExists(getUrl()))
                {
                    getNextUrl();
                    maxTries++;
                }
            }
            catch (final SoftwareException softwareException)
            {
                throw softwareException.setMaxTries(maxTries);
            }
            catch (final ConnectException connectException)
            {
                throw new SoftwareException("Failed to connect to: " + getUrl(), connectException).setMaxTries(maxTries);
            }
        }

        return this;
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

        final SoftwareUrl that = (SoftwareUrl) o;

        return new EqualsBuilder() //
                        .append(url, that.url) //
                        .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37) //
                        .append(url) //
                        .toHashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this) //
                        .append("url", url) //
                        .toString();
    }

    private void getNextUrl() throws SoftwareException
    {
        url = url.replaceAll(version.toString(), version.getNext().toString());
    }

    private static SoftwareVersion parse(final String url, final SoftwareUpdateOptions updateOptions) throws SoftwareException
    {
        for (final String versionRegexp : SoftwareVersion.getParsableRegexps())
        {
            final String urlRegexp = "[^\\d]*(?<version>" + versionRegexp + ").*(\\k<version>)?[^\\d]*";
            final Pattern urlPattern = Pattern.compile(urlRegexp);
            final Matcher urlMatcher = urlPattern.matcher(url);
            if (urlMatcher.matches())
            {
                return new SoftwareVersion(urlMatcher.group("version"), updateOptions.versionIncrementPolicy);
            }
        }

        throw new SoftwareUrlNotParsableException(url + " is not parsable");
    }
}
