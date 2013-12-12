package com.cdl.rebranding.api;

public class Utils {

    public static final String CONF_PROPERTIES_FILE = "conf.properties";
    public static final String BAKUP_EXTENSION = ".bak";
    public static final String XML_EXTENSION = ".xml";
    public static final String XSL_EXTENSION = ".xsl";
    public static final String PROP_REBRANDING_FROM = "rebranding.from";
    public static final String PROP_REBRANDING_TO = "rebranding.to";

    public static String makeReplacement(String string, String from, String to) {
        String toPlaceholder = "1___to___1";
        String stringWithToPlaceholder = string.replaceAll(to, toPlaceholder);
        String result = stringWithToPlaceholder.replaceAll(from, to);
        return result.replaceAll(toPlaceholder, to);
    }
}
