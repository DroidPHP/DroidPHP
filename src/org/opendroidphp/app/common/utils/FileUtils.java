package org.opendroidphp.app.common.utils;

import org.opendroidphp.app.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Static method for helping user to validates servers configurations
 */

public class FileUtils {

    /**
     * If is used to find if required binaries exist
     *
     * @return boolean
     */

    public static boolean checkIfExecutableExists() {

        //checking all file is cpu expensive task so only check required file

        if (
                new File(Constants.LIGHTTPD_SBIN_LOCATION).exists() &&
                        new File(Constants.PHP_SBIN_LOCATION).exists() &&
                        new File(Constants.MYSQL_DAEMON_SBIN_LOCATION).exists() &&
                        new File(Constants.MYSQL_MONITOR_SBIN_LOCATION).exists()
                ) {
            //so, all the required files exists
            return true;

        }
        //so, some of the required file are missing :(
        return false;

    }

    public static String convertToString(InputStream is) {

        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        String tmp;
        String buff = "";
        try {
            while ((tmp = bf.readLine()) != null) {
                buff += tmp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buff;
    }

}
