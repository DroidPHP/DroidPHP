/*
 * *
 *  * This file is part of DroidPHP
 *  *
 *  * (c) 2014 Shushant Kumar
 *  *
 *  * For the full copyright and license information, please view the LICENSE
 *  * file that was distributed with this source code.
 *
 */

package org.opendroidphp.app.common.shell;

import java.util.List;

/**
 * This class provides utility functions to easily execute commands using Busybox
 */
public class Busybox {

    /**
     * Runs command and return output
     *
     * @param command The command to run
     * @return Output of the command, or null in case of an error
     */
    public static List<String> run(String command) {
        return Shell.run("busybox", new String[]{command}, null, false);
    }

    /**
     * Runs commands and return output
     *
     * @param commands The commands to run
     * @return Output of the commands, or null in case of an error
     */
    public static List<String> run(List<String> commands) {
        return Shell.run("busybox", commands.toArray(new String[commands.size()]), null, false);
    }

    /**
     * Runs commands and return output
     *
     * @param commands The commands to run
     * @return Output of the commands, or null in case of an error
     */
    public static List<String> run(String[] commands) {
        return Shell.run("busybox", commands, null, false);
    }
}
