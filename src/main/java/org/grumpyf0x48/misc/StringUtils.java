package org.grumpyf0x48.misc;

public class StringUtils
{
    private StringUtils()
    {
    }

    public static String unquote(final String string)
    {
        return string.replaceAll("^\"|\"$", "");
    }
}
