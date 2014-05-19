package org.opendroidphp.app.common.tasks;

import org.opendroidphp.app.Constants;
import org.opendroidphp.app.common.shell.SH;

import java.util.ArrayList;
import java.util.List;

public class DestroyServer implements Runnable {


    @Override
    public void run() {


        initialize();
    }

    protected void initialize() {

        List<String> command = new ArrayList<String>();

        command.add(Constants.BUSYBOX_SBIN_LOCATION + " killall lighttpd");
        command.add(Constants.BUSYBOX_SBIN_LOCATION + " killall mysqld");
        command.add(Constants.BUSYBOX_SBIN_LOCATION + " killall php");

        SH.run(command);
    }
}
