package org.opendroidphp.app.common.utils;

public class FilenameUtils {

    public static String getFilename(String filePath) {
        return filePath.substring(filePath.lastIndexOf('/') + 1);
    }

    public static String getBasename(String filePath) {
        return org.apache.commons.io.FilenameUtils.getBaseName(filePath);
    }
}
