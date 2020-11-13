package org.grumpyf0x48.liar.update;

public enum SoftwareDefinition
{
    // ANT, Frequent connection problems
    ATOM,
    BAT,
    CHEAT,
    CLION,
    DATAGRIP,
    EASY_RSA,
    EVANS(new String[]{ "ktr0731" }),
    FIREFOX,
    GH,
    GLAB(new String[]{ "pre2" }),
    GO,
    GOLAND,
    GRAALVM,
    GRADLE,
    GROOVY,
    GRPCUI,
    GRPCURL,
    HADOLINT,
    HUGO,
    ICECAT,
    IDEA_COMMUNITY,
    IDEA_EDU,
    IDEA_ULTIMATE,
    JBANG,
    JD_CMD,
    // JDK*, Cannot parse because of the presence of SHA or build numbers in URLs
    JGO,
    JQ,
    MARP_CLI,
    MAVEN(new String[]{ "maven/maven-3" }),
    MICRONAUT,
    MONGODB(new String[] { "x86_64", "debian10" }),
    // NODE,
    // POSTMAN, Cannot parse because always the same URL
    PYCHARM_COMMUNITY,
    PYCHARM_EDU,
    PYCHARM_PROFESSIONAL,
    // ROBO3T Cannot parse because of the presence of SHA in URL
    RUBYMINE,
    SCALA,
    SPRING,
    // SUBLIMEMERGE, Cannot parse because of the presence of build number in URL
    // SUBLIMETEXT, Cannot parse because of the presence of build number in URL
    TORBROWSER,
    // VISUALVM, Cannot parse because of the version in the form mMp
    WEBSTORM,
    XSV,
    YQ;

    private String[] regexps;

    SoftwareDefinition()
    {
        this(new String[] {});
    }

    SoftwareDefinition(final String[] regexps)
    {
        this.regexps = regexps;
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
