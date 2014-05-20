package org.opendroidphp.app.common.utils;

public class ShellOutputFormatter {

    public static String toHTML(String mString) {

        /**
         * Replace all repeating whitespace with one single whilespace
         */

        mString = mString.replaceAll("\\s+", " ");
        // String[] mArrStr = mString.split("\n");
        // String s = "";
        // for (int i = 0; i < mArrStr.length; i++) {
        mString = "<strong>" + mString.trim() + "</strong><br/>";

        // }
        return mString.trim();

    }

}
