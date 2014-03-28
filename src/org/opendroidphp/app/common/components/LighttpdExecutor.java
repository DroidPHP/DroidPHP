package org.opendroidphp.app.common.components;

/**
 * Created by shushant on 3/18/14.
 */

import org.opendroidphp.app.ComponentProviderInterface;
import org.opendroidphp.app.Constants;
import org.opendroidphp.app.common.shell.SH;

import java.util.Locale;

public class LighttpdExecutor implements ComponentProviderInterface {

    @Override
    public void connect() {

        String command = String.format(

                Locale.ENGLISH,
                "%s -f %s -D",
                Constants.LIGHTTPD_SBIN_LOCATION,
                Constants.PROJECT_LOCATION + "/conf/lighttpd.conf"
        );

        SH.run(command);
        //Log.d("CMD", command);

    }

    @Override
    public void destroy() {

        SH.run(
                Constants.BUSYBOX_SBIN_LOCATION + " killall lighttpd");

    }
}
