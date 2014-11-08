package org.opendroidphp.app.common.utils;

import org.opendroidphp.app.Constants;

import java.io.File;

/**
 * Static method for helping user to validates servers configurations
 */

public class FileUtils {
    /**
     * Check if required file exist
     *
     * @return boolean
     */
    public static boolean checkIfExecutableExists() {
        return new File(Constants.LIGHTTPD_SBIN_LOCATION).exists() &&
                new File(Constants.PHP_SBIN_LOCATION).exists() &&
                new File(Constants.MYSQL_DAEMON_SBIN_LOCATION).exists() &&
                new File(Constants.MYSQL_MONITOR_SBIN_LOCATION).exists();
    }
}
