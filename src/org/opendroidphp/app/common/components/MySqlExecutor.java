package org.opendroidphp.app.common.components;

import org.opendroidphp.app.ComponentProviderInterface;
import org.opendroidphp.app.Constants;
import org.opendroidphp.app.common.shell.SH;

import java.util.Locale;

/**
 * Created by shushant on 3/19/14.
 */
public class MySqlExecutor implements ComponentProviderInterface {

    @Override
    public void connect() {

        String command = String.format(

                Locale.ENGLISH,
                "%s --defaults-file=%s --user=root --language=%s",
                Constants.MYSQL_DAEMON_SBIN_LOCATION,
                Constants.PROJECT_LOCATION + "/conf/mysql.ini",
                Constants.MYSQL_SHARE_DATA_LOCATION + "/mysql/english"
        );

        SH.run(command);

    }

    @Override
    public void destroy() {

        SH.run(
                Constants.BUSYBOX_SBIN_LOCATION + " killall mysqld");

    }
}
