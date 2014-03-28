/*
 * *
 *  * This file is part of DroidPHP
 *  *
 *  * (c) 2014 Shushant Kumar
 *  *
 *  * For the full copyright and license information, please view the LICENSE
 *  * file that was distributed with this source code.
 *
 */

package org.opendroidphp.app.common.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Utility for parsing lighttpd configuration
 */

public class ParseConfiguration {

    /**
     * Parse the lighttpd configuration
     *
     * @param fileName A lighttpd configuration
     * @return HashMap
     */
    public HashMap<String, String> Parser(String fileName) {

        String confValue = "";

        try {

            confValue = org.apache.commons.io.FileUtils.readFileToString(new File(
                    fileName), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] confValueArray = confValue.split("\n");

        HashMap<String, String> map = new HashMap<String, String>();

        for (int i = 0; i < confValueArray.length; ++i) {

            // If Current line does not contain "var." just skip it
            if (!confValueArray[i].contains("var.")) {
                continue;
            }
            /**
             * Now we have found what we are looking for just spit it from `=` and
             * convert it in to key and value pair
             */

            String[] NodeList = confValueArray[i].split("=", 2);//OffSet is 2

            String confMapKey = NodeList[0].trim().replace("var.", "");
            String confMapValue = filterValue(NodeList[1]).substring(1).trim();

            map.put(confMapKey, confMapValue);

        }
        return map;

    }

    /**
     * Used for filtering the string
     *
     * @param filterValue String the filter
     * @return String Filtered string
     */

    public static String filterValue(String filterValue) {

        StringBuffer sb = new StringBuffer(filterValue);

        if (filterValue.startsWith("\"") || filterValue.startsWith("'")) {
            sb.setCharAt(0, ' ');

        }
        if (filterValue.endsWith("\"") || filterValue.endsWith("'")) {
            sb.deleteCharAt(filterValue.length() - 1);

        }

        return sb.toString().trim();

    }
}