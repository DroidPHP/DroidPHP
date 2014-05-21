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

    protected String SERV_PORT_REGEX = "server.port.*";
    protected static String EXTERNAL_DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/droidphp/";
    protected final static String CHANGE_SBIN_PERMISSION = "/system/bin/chmod 777";
    protected String baseShell;
    protected String basePort;

    /**
     * Set shell binary to use. Usually "sh" or "su"
     *
     * @param shell Shell to use
     */

    public ConnectServer setShell(String shell) {
        baseShell = shell;
        return this;
    }

    /**
     * Set port to use
     *
     * @param port port to use
     */

    public ConnectServer setServerPort(String port) {
        basePort = port;
        return this;
    }

    @Override
    public void run() {
        initialize();
    }


    protected void initialize() {
        List<String> command = new ArrayList<String>();


        command.add(CHANGE_SBIN_PERMISSION + " " + Constants.LIGHTTPD_SBIN_LOCATION);
        command.add(CHANGE_SBIN_PERMISSION + " " + Constants.PHP_SBIN_LOCATION);
        command.add(CHANGE_SBIN_PERMISSION + " " + Constants.MYSQL_DAEMON_SBIN_LOCATION);
        command.add(CHANGE_SBIN_PERMISSION + " " + Constants.MYSQL_MONITOR_SBIN_LOCATION);
        command.add(CHANGE_SBIN_PERMISSION + " " + Constants.BUSYBOX_SBIN_LOCATION);

        try {

            checkFilesystem();

            createOrRestoreConfiguration(
                    Constants.LIGTTTPD_CONF_LOCATION, "lighttpd.conf");
            createOrRestoreConfiguration(
                    Constants.PHP_INI_LOCATION, "php.ini");
            createOrRestoreConfiguration(
                    Constants.MYSQL_INI_LOCATION, "mysql.ini");

            if (basePort != null) {
                changeServerData(basePort);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        command.add(String.format(
                Locale.ENGLISH,
                "%s -b 127.0.0.1:9786 -c %s >> %s",
                Constants.PHP_SBIN_LOCATION,
                Constants.PHP_INI_LOCATION,
                EXTERNAL_DIRECTORY + "/logs/php/fcgiserver.log"));

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

        //PHP Environment Variable
        String[] envs = new String[]{
                "PHP_FCGI_CHILDERN=3",
                "PHP_FCGI_MAX_REQUEST=1000"
        };

        String commands[] = new String[command.size()];
        int i = 0;
        //@TODO: simpler way to convert List to String[]
        for (String eachCommand : command) {
            commands[i] = eachCommand;
            i++;
        }
        command.clear();

        if (baseShell == null) baseShell = "sh";

        Shell.run(baseShell, commands, envs, false);

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

    protected void changeServerData(String port) {

        try {

            File confFile = new File(EXTERNAL_DIRECTORY + "/conf/lighttpd.conf");

            String sb = FileUtils.readFileToString(confFile, "UTF-8");
            sb.replaceFirst(SERV_PORT_REGEX, String.format(Locale.ENGLISH, "server.port                              = %s", port));

            FileUtils.writeStringToFile(confFile, sb, "UTF-8");
        } catch (Exception e) {

        }
    }

    protected void createOrRestoreConfiguration(String confFilename, String fileName) throws Exception {

        File f = new File(EXTERNAL_DIRECTORY + "/conf/" + fileName);

        if (f.exists()) {
            //ya, file does exist we don't need to recreate the configuration
            // lets return from the method
            return;
        }

        String confValue = FileUtils.readFileToString(
                new File(confFilename), "UTF-8");

        FileUtils.writeStringToFile(f, confValue, "UTF-8");

    }

}
