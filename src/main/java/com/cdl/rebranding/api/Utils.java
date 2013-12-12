package com.cdl.rebranding.api;

public class Utils {

    public static String makeReplacement(String string, String from, String to) {
        //todo move to more correct place
        String toPlaceholder = "1___to___1";
        String stringWithToPlaceholder = string.replaceAll(to, toPlaceholder);
        String result = stringWithToPlaceholder.replaceAll(from, to);
        return result.replaceAll(toPlaceholder, to);
    }
}
