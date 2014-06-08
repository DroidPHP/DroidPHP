package org.opendroidphp.app;

import android.os.Environment;

public class Constants {

    public static final String EXTERNAL_STORAGE = Environment.getExternalStorageDirectory().getPath();

    public static final String SERVER_LOCATION = EXTERNAL_STORAGE + "/htdocs";
    public static final String PROJECT_LOCATION = EXTERNAL_STORAGE + "/droidphp";

    public static final String INTERNAL_LOCATION = "/data/data/org.opendroidphp.app";

    public static final String LIGHTTPD_SBIN_LOCATION = INTERNAL_LOCATION + "/components/lighttpd/sbin/lighttpd";
    public static final String LIGTTTPD_CONF_LOCATION = INTERNAL_LOCATION + "/components/lighttpd/conf/lighttpd.conf";

    public static final String PHP_SBIN_LOCATION = INTERNAL_LOCATION + "/components/php/sbin/php-cgi";
    public static final String PHP_INI_LOCATION = INTERNAL_LOCATION + "/components/php/conf/php.ini";

    public static final String MYSQL_DATA_DATA_LOCATION = INTERNAL_LOCATION + "/components/mysql/sbin/data";
    public static final String MYSQL_SHARE_DATA_LOCATION = INTERNAL_LOCATION + "/components/mysql/sbin/share";
    public static final String MYSQL_DAEMON_SBIN_LOCATION = INTERNAL_LOCATION + "/components/mysql/sbin/mysqld";

    public static final String MYSQL_INI_LOCATION = INTERNAL_LOCATION + "/components/mysql/conf/mysql.ini";
    public static final String MYSQL_MONITOR_SBIN_LOCATION = INTERNAL_LOCATION + "/components/mysql/sbin/mysql-monitor";

    public static final String UPDATE_FROM_EXTERNAL_REPOSITORY = EXTERNAL_STORAGE + "/droidphp/repositroy/update.zip";
    public static final String BUSYBOX_SBIN_LOCATION = INTERNAL_LOCATION + "/components/busybox/sbin/busybox";
}
