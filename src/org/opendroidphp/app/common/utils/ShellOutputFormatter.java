package org.opendroidphp.app.common.utils;

public class ShellOutputFormatter {

    public static String toHTML(String mString) {

        String templateView = "</style>.prettyprint {\n" +
                "        padding: 8px;\n" +
                "        background-color: #f7f7f9;\n" +
                "        border: 1px solid #e1e1e8;\n" +
                "        }</style>" + "<pre class=\"prettyprint\">Hell</pre>";
        /**
         * Replace all repeating whitespace with one single whitespace
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
