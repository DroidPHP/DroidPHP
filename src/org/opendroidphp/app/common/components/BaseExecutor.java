package org.opendroidphp.app.common.components;

import android.os.Environment;

import org.apache.commons.io.FileUtils;
import org.opendroidphp.app.ComponentProviderInterface;
import org.opendroidphp.app.Constants;
import org.opendroidphp.app.common.shell.SH;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shushant on 3/20/14.
 */
public class BaseExecutor implements ComponentProviderInterface {

    protected static String EXTERNAL_DIR;

    /**
     * chmod to 777  is really a security issue
     * i should not really depend on busybox to do all my stuff
     */
    protected final static String CHANGE_SBIN_PERMISSION = "/system/bin/chmod 777";

    @Override
    public void connect() {

//        if (SU.isSU()){
//
//        }
        List<String> command = new ArrayList<String>();


        command.add(CHANGE_SBIN_PERMISSION + " " + Constants.LIGHTTPD_SBIN_LOCATION);
        command.add(CHANGE_SBIN_PERMISSION + " " + Constants.PHP_SBIN_LOCATION);
        command.add(CHANGE_SBIN_PERMISSION + " " + Constants.MYSQL_DAEMON_SBIN_LOCATION);
        //wtf, how could i forgot about swiss army knife
        command.add(CHANGE_SBIN_PERMISSION + " " + Constants.BUSYBOX_SBIN_LOCATION);

        try {
            SH.run(command);

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

        command.add("/system/bin/chmod 755 " + Constants.INTERNAL_LOCATION + "/tmp");


    }

    @Override
    public void destroy() {

    }

    protected void checkFilesystem() {

        File f = new File(Constants.PROJECT_LOCATION + "/logs/lighttpd");

        if (!f.exists()) {
            f.mkdirs();
        }
        f = new File(Constants.PROJECT_LOCATION + "/logs/mysql");

        if (!f.exists()) {
            f.mkdirs();
        }
        f = new File(Constants.PROJECT_LOCATION + "/logs/php");

        if (!f.exists()) {
            f.mkdirs();
        }
        f = new File(Constants.PROJECT_LOCATION + "/conf");

        if (!f.exists()) {
            f.mkdirs();
        }
        f = new File(Constants.PROJECT_LOCATION + "/sessions");
        if (!f.exists()) {
            f.mkdirs();
        }

        f = new File(Constants.INTERNAL_LOCATION + "/tmp");
        if (!f.exists()) {
            f.mkdirs();
        }

    }

    protected void createOrRestoreConfiguration(String confFilename, String fileName) throws Exception {

        EXTERNAL_DIR = Environment.getExternalStorageDirectory().getPath() + "/droidphp/";

        if (new File(
                EXTERNAL_DIR + "/conf/" + fileName).exists()) {
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
