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

    protected final static String CHANGE_PERMISSION = "/system/bin/chmod 755 ";
    protected static String EXTERNAL_DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/droidphp/";
    protected String SERV_PORT_REGEX = "server.port.*";
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


        command.add(CHANGE_PERMISSION
                .concat(Constants.LIGHTTPD_SBIN_LOCATION));

        command.add(CHANGE_PERMISSION
                .concat(Constants.PHP_SBIN_LOCATION));

        command.add(CHANGE_PERMISSION
                .concat(Constants.MYSQL_DAEMON_SBIN_LOCATION));

        command.add(CHANGE_PERMISSION
                .concat(Constants.MYSQL_MONITOR_SBIN_LOCATION));

        command.add(CHANGE_PERMISSION
                .concat(Constants.BUSYBOX_SBIN_LOCATION));

        command.add(CHANGE_PERMISSION
                .concat(Constants.INTERNAL_LOCATION + "/tmp"));


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

        // hack for running process in parallel
        command.add(String.format(
                Locale.ENGLISH,
                "%s -f %s -D 1>/dev/null 1>/dev/null 2>&1 & pid_lighttpd=$!",
                Constants.LIGHTTPD_SBIN_LOCATION,
                Constants.PROJECT_LOCATION + "/conf/lighttpd.conf"
        ));

        command.add(String.format(
                Locale.ENGLISH,
                "%s --defaults-file=%s --user=root --language=%s 1>/dev/null 2>&1 & pid_mysql=$!",
                Constants.MYSQL_DAEMON_SBIN_LOCATION,
                Constants.PROJECT_LOCATION + "/conf/mysql.ini",
                Constants.MYSQL_SHARE_DATA_LOCATION + "/mysql/english"
        ));

        command.add(String.format(
                Locale.ENGLISH,
                "%s -b 127.0.0.1:9786 -c %s 1>/dev/null 2>&1 & pid_php=$!",
                Constants.PHP_SBIN_LOCATION,
                Constants.PHP_INI_LOCATION
        ));

//        command.add("php=$pid_php, lighttpd=$pid_lighttpd, mysql=$pid_mysql");

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
                Constants.INTERNAL_LOCATION + "/tmp",
                Constants.EXTERNAL_STORAGE + "/htdocs"
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

        if (f.exists()) return;

        String confValue = FileUtils.readFileToString(
                new File(confFilename), "UTF-8");

        FileUtils.writeStringToFile(f, confValue, "UTF-8");

    }
}
