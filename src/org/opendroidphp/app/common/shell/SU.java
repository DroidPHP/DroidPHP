
/*
 * Copyright (C) 2012-2013 Jorrit "Chainfire" Jongma
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opendroidphp.app.common.shell;

import java.util.List;

/**
 * This class provides utility functions to easily execute commands using SU
 * (root shell), as well as detecting whether or not root is available, and
 * if so which version.
 */
public class SU {
    /**
     * Runs command as root (if available) and return output
     *
     * @param command The command to run
     * @return Output of the command, or null if root isn't available or in case of an error
     */
    public static List<String> run(String command) {
        return Shell.run("su", new String[]{command}, null, false);
    }

    /**
     * Runs commands as root (if available) and return output
     *
     * @param commands The commands to run
     * @return Output of the commands, or null if root isn't available or in case of an error
     */
    public static List<String> run(List<String> commands) {
        return Shell.run("su", commands.toArray(new String[commands.size()]), null, false);
    }

    /**
     * Runs commands as root (if available) and return output
     *
     * @param commands The commands to run
     * @param env      The Environment variable to include
     * @return Output of the commands, or null if root isn't available or in case of an error
     */
    public static List<String> run(List<String> commands, String[] env) {
        return Shell.run("su", commands.toArray(new String[commands.size()]), env, false);
    }

    /**
     * Runs commands as root (if available) and return output
     *
     * @param commands The commands to run
     * @return Output of the commands, or null if root isn't available or in case of an error
     */
    public static List<String> run(String[] commands) {
        return Shell.run("su", commands, null, false);
    }

    /**
     * Detects whether or not superuser access is available, by checking the output
     * of the "id" command if available, checking if a shell runs at all otherwise
     *
     * @return True if superuser access available
     */
    public static boolean available() {
        // this is only one of many ways this can be done

        List<String> ret = run(Shell.availableTestCommands);
        return Shell.parseAvailableResult(ret, true);
    }

    /**
     * <p>Detects the version of the su binary installed (if any), if supported by the binary.
     * Most binaries support two different version numbers, the public version that is
     * displayed to users, and an internal version number that is used for version number
     * comparisons. Returns null if su not available or retrieving the version isn't supported.</p>
     * <p/>
     * <p>Note that su binary version and GUI (APK) version can be completely different.</p>
     *
     * @param internal Request human-readable version or application internal version
     * @return String containing the su version or null
     */
    public static String version(boolean internal) {
        List<String> ret = Shell.run(
                internal ? "su -V" : "su -v",
                new String[]{},
                null,
                false
        );
        if (ret == null) return null;

        for (String line : ret) {
            if (!internal) {
                if (line.contains(".")) return line;
            } else {
                try {
                    if (Integer.parseInt(line) > 0) return line;
                } catch (NumberFormatException e) {
                }
            }
        }
        return null;
    }

    /**
     * Attempts to deduce if the shell command refers to a su shell
     *
     * @param shell Shell command to run
     * @return Shell command appears to be su
     */
    public static boolean isSU(String shell) {
        // Strip parameters
        int pos = shell.indexOf(' ');
        if (pos >= 0) {
            shell = shell.substring(0, pos);
        }

        // Strip path
        pos = shell.lastIndexOf('/');
        if (pos >= 0) {
            shell = shell.substring(pos + 1);
        }

        return shell.equals("su");
    }
}