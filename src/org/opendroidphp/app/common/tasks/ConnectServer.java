package org.opendroidphp.app.common.tasks;

import android.os.Environment;

import org.opendroidphp.app.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class ConnectServer implements Runnable {

    protected final static String CHANGE_PERMISSION = "/system/bin/chmod 755 ";

    protected final static String SERVER_DAEMON_LIGHTTPD = "lighttpd";
    protected final static String SERVER_DAEMON_NGINX = "nginx";

    protected static String EXTERNAL_DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/droidphp/";
    protected String baseShell;
    protected String basePort;
    protected String serverDaemon;

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
        if (port == null || port.equals("")) {
            port = "8080";
        }
        basePort = port;
        return this;
    }

    /**
     * Set Server Daemon to use
     *
     * @param server server daemon to use
     */

    public ConnectServer setServer(String server) {

        if (server == null || server.equals("")) {
            server = SERVER_DAEMON_LIGHTTPD;
        }
        serverDaemon = server;
        return this;
    }


    @Override
    public void run() {
        initialize();
    }


    protected void initialize() {
        List<String> command = new ArrayList<String>();
        command.add(CHANGE_PERMISSION.concat(Constants.INTERNAL_LOCATION + "/scripts/server-sh.sh"));

        checkFilesystem();
        String daemon = serverDaemon.equals(SERVER_DAEMON_NGINX) ? "nginx" : "lighttpd";

        String shellScript = String.format("%s/scripts/server-sh.sh %s %s",
                Constants.INTERNAL_LOCATION,
                daemon,
                basePort
        );

        command.add(shellScript);

        String commands[] = new String[command.size()];
        int i = 0;
        //@TODO: simple way to convert List to String[]
        for (String eachCommand : command) {
            commands[i] = eachCommand;
            i++;
        }
        command.clear();

        if (baseShell == null) baseShell = "sh";

        Shell.run(baseShell, commands, null, false);
    }

    protected void checkFilesystem() {

        String[] filesUri = new String[]{
                Constants.PROJECT_LOCATION + "/logs/lighttpd",
                Constants.PROJECT_LOCATION + "/logs/mysql",
                Constants.PROJECT_LOCATION + "/logs/php",
                Constants.PROJECT_LOCATION + "/logs/nginx",
                Constants.PROJECT_LOCATION + "/conf",
                Constants.PROJECT_LOCATION + "/conf/nginx/logs",
                Constants.PROJECT_LOCATION + "/hosts/nginx",
                Constants.PROJECT_LOCATION + "/hosts/lighttpd",
                Constants.PROJECT_LOCATION + "/sessions",
                Constants.INTERNAL_LOCATION + "/tmp",
                Constants.EXTERNAL_STORAGE + "/htdocs"
        };

        for (String fileUri : filesUri) {

            File file = new File(fileUri);
            if (!file.exists()) file.mkdirs();
        }
    }
}
