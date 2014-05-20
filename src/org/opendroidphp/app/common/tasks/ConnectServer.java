package org.opendroidphp.app.common.tasks;

import android.os.Environment;

import org.apache.commons.io.FileUtils;
import org.opendroidphp.app.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.chainfire.libsuperuser.Shell;

public class ConnectServer implements Runnable {

    protected static String EXTERNAL_DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/droidphp/";
    protected final static String CHANGE_SBIN_PERMISSION = "/system/bin/chmod 777";


    @Override
    public void run() {
        initialize();
    }


    protected void initialize() {
        List<String> command = new ArrayList<String>();


        command.add(CHANGE_SBIN_PERMISSION + " " + Constants.LIGHTTPD_SBIN_LOCATION);
        command.add(CHANGE_SBIN_PERMISSION + " " + Constants.PHP_SBIN_LOCATION);
        command.add(CHANGE_SBIN_PERMISSION + " " + Constants.MYSQL_DAEMON_SBIN_LOCATION);
        //wtf, how could i forgot about swiss army knife
        command.add(CHANGE_SBIN_PERMISSION + " " + Constants.BUSYBOX_SBIN_LOCATION);

        try {
            Shell.SH.run(command);

        } catch (Exception e) {

            e.printStackTrace();
        }

        try {

            checkFilesystem();

            createOrRestoreConfiguration(
                    Constants.LIGTTTPD_CONF_LOCATION, "lighttpd.conf");
            createOrRestoreConfiguration(
                    Constants.PHP_INI_LOCATION, "php.ini");
            createOrRestoreConfiguration(
                    Constants.MYSQL_INI_LOCATION, "mysql.ini");

        } catch (Exception e) {

            e.printStackTrace();
        }
        command.add(String.format(

                Locale.ENGLISH,
                "%s -f %s -D",
                Constants.LIGHTTPD_SBIN_LOCATION,
                Constants.PROJECT_LOCATION + "/conf/lighttpd.conf"
        ));
        command.add(String.format(

                Locale.ENGLISH,
                "%s --defaults-file=%s --user=root --language=%s",
                Constants.MYSQL_DAEMON_SBIN_LOCATION,
                Constants.PROJECT_LOCATION + "/conf/mysql.ini",
                Constants.MYSQL_SHARE_DATA_LOCATION + "/mysql/english"
        ));
        command.add("/system/bin/chmod 755 " + Constants.INTERNAL_LOCATION + "/tmp");


        Shell.SH.run(command);



    }

    protected void checkFilesystem() {

        String[] filesUri = new String[]{
                Constants.PROJECT_LOCATION + "/logs/lighttpd",
                Constants.PROJECT_LOCATION + "/logs/mysql",
                Constants.PROJECT_LOCATION + "/logs/php",
                Constants.PROJECT_LOCATION + "/conf",
                Constants.PROJECT_LOCATION + "/sessions",
                Constants.INTERNAL_LOCATION + "/tmp"

        };

        for (String fileUri : filesUri) {

            File file = new File(fileUri);
            if (!file.exists()) file.mkdirs();
        }


    }

    protected void createOrRestoreConfiguration(String confFilename, String fileName) throws Exception {

        if (new File(
                EXTERNAL_DIRECTORY + "/conf/" + fileName).exists()) {
            //ya, file does exist we don't need to recreate the configuration
            // lets return from the method
            return;
        }

        String confValue = FileUtils.readFileToString(
                new File(confFilename), "UTF-8");

        FileUtils.writeStringToFile(new File(
                        Environment.getExternalStorageDirectory().getPath() + "/droidphp/conf/" + fileName), confValue, "UTF-8"
        );

    }

}
