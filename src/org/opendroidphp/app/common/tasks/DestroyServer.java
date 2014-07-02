package org.opendroidphp.app.common.tasks;

import org.opendroidphp.app.Constants;

import eu.chainfire.libsuperuser.Shell;

public class DestroyServer implements Runnable {

    protected String baseShell;

    /**
     * Set shell binary to use. Usually "sh" or "su"
     *
     * @param shell Shell to use
     */

    public DestroyServer setShell(String shell) {
        baseShell = shell;
        return this;
    }

    @Override
    public void run() {
        initialize();
    }

    protected void initialize() {

        String[] command = new String[]{
                Constants.BUSYBOX_SBIN_LOCATION + " killall -SIGTERM lighttpd",
                Constants.BUSYBOX_SBIN_LOCATION + " killall -SIGTERM mysqld",
                Constants.BUSYBOX_SBIN_LOCATION + " killall -SIGTERM php-cgi",
                Constants.BUSYBOX_SBIN_LOCATION + " killall -SIGTERM nginx"
        };

        if (baseShell == null) baseShell = "sh";

        Shell.run(baseShell, command, null, false);
    }
}
