package org.grumpyf0x48.liar.update;

import static org.grumpyf0x48.liar.update.SoftwareUpdatePeriodicity.DAILY;
import static org.grumpyf0x48.liar.update.SoftwareUpdatePeriodicity.MONTHLY;
import static org.grumpyf0x48.liar.update.SoftwareUpdatePeriodicity.WEEKLY;

public enum SoftwareDefinition
{
    ACT(WEEKLY),
    // ANT, Frequent connection problems
    ATOM(DAILY),
    BAT(MONTHLY),
    CHEAT(MONTHLY),
    CLION(MONTHLY),
    CLOC(MONTHLY),
    DATAGRIP(MONTHLY),
    DBEAVER(WEEKLY),
    EASY_RSA(MONTHLY),
    EXA(MONTHLY),
    EVANS(MONTHLY,new String[]{ "ktr0731" }),
    FIREFOX(DAILY),
    GH(MONTHLY),
    GLAB(MONTHLY,new String[]{ "pre2" }),
    GO(DAILY),
    GOLAND(DAILY),
    GRAALVM8(WEEKLY),
    GRAALVM11(WEEKLY),
    GRAALVM16(WEEKLY),
    GRADLE(DAILY),
    GRAILS(MONTHLY),
    GROOVY(WEEKLY),
    GRPCUI(MONTHLY),
    GRPCURL(MONTHLY),
    HADOLINT(MONTHLY),
    HUGO(DAILY),
    HYPERFINE(MONTHLY),
    ICECAT(MONTHLY),
    IDEA_COMMUNITY(DAILY),
    IDEA_EDU(MONTHLY),
    IDEA_ULTIMATE(DAILY),
    JBANG(DAILY),
    // JDK*, Cannot parse because of the presence of SHA or build numbers in URLs
    JD_CMD(MONTHLY),
    JGO(MONTHLY),
    JMETER(MONTHLY),
    JQ(MONTHLY),
    JRELEASER(WEEKLY),
    MARP_CLI(WEEKLY),
    MAVEN(WEEKLY,new String[]{ "maven/maven-3" }),
    MCS(WEEKLY),
    MICRONAUT(MONTHLY),
    MONGODB(MONTHLY,new String[] { "x86_64", "debian10" }),
    // NODE,
    // POSTMAN, Cannot parse because always the same URL
    PYCHARM_COMMUNITY(WEEKLY),
    PYCHARM_EDU(MONTHLY),
    PYCHARM_PROFESSIONAL(WEEKLY),
    // ROBO3T Cannot parse because of the presence of SHA in URL
    RUBYMINE(DAILY),
    SBT(WEEKLY),
    SCALA(WEEKLY),
    SHELLCHECK(MONTHLY),
    SPRING(WEEKLY),
    // SUBLIMEMERGE, Cannot parse because of the presence of build number in URL
    // SUBLIMETEXT, Cannot parse because of the presence of build number in URL
    TORBROWSER(DAILY),
    VSCODIUM(WEEKLY),
    WEBSTORM(DAILY),
    XSV(MONTHLY),
    YQ(MONTHLY);

    private final SoftwareUpdatePeriodicity periodicity;
    private final String[] regexps;

    SoftwareDefinition(final SoftwareUpdatePeriodicity periodicity)
    {
        this(periodicity, new String[] {});
    }

    SoftwareDefinition(final SoftwareUpdatePeriodicity periodicity, final String[] regexps)
    {
        this.periodicity = periodicity;
        this.regexps = regexps;
    }

    public SoftwareUpdatePeriodicity getPeriodicity()
    {
        return periodicity;
    }

    public String getSanitizedUrl(final String url)
    {
        String sanitizedUrl = url;
        for (final String regexp : regexps)
        {
            sanitizedUrl = sanitizedUrl.replaceAll(regexp, "");
        }
        return sanitizedUrl;
    }
}
