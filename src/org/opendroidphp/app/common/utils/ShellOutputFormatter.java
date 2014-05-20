package org.opendroidphp.app.common.utils;

import java.util.Locale;

public class ShellOutputFormatter {

    public static String toHTML(String fromOutput) {

        /**
         * Replace all repeating whitespace with one single whitespace
         */

        fromOutput = fromOutput.replaceAll("\\s+", " ");
        return String.format(Locale.ENGLISH, fromOutput);

    }

}
