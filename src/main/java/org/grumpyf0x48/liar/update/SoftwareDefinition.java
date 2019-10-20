package org.grumpyf0x48.liar.update;

public enum SoftwareDefinition {
    ANT,
    ATOM,
    FIREFOX,
    GO,
    GOLAND,
    GRAALVM,
    GRADLE,
    GROOVY,
    HUGO,
    ICECAT,
    IDEA_COMMUNITY,
    IDEA_ULTIMATE,
    // JDK*, Cannot parse because of the presence of SHA or build numbers in URLs
    MARP_CLI,
    // MAVEN, Cannot parse because of 'maven-3' in URL
    // POSTMAN, Cannot parse because always the same URL
    PYCHARM_COMMUNITY,
    PYCHARM_PROFESSIONAL,
    // ROBO3T Cannot parse because of the presence of SHA in URL
    RUBYMINE,
    SCALA,
    // SUBLIMEMERGE, Cannot parse because of the presence of build number in URL
    // SUBLIMETEXT, Cannot parse because of the presence of build number in URL
    TORBROWSER,
    // VISUALVM, Cannot parse because of the version in the form mMp
    WEBSTORM,
    XSV
}
