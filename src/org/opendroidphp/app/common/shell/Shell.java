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

import org.opendroidphp.app.common.utils.Debug;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Class providing functionality execute commands in a (root) shell
 */
public class Shell {

    /**
     * Executor
     * <p>Runs commands using the supplied shell, and returns the output, or null in
     * case of errors.</p>
     * <p/>
     * <p>Note that due to compatibility with older Android versions,
     * wantSTDERR is not implemented using redirectErrorStream, but rather appended
     * to the output. STDOUT and STDERR are thus not guaranteed to be in the correct
     * order in the output.</p>
     * <p/>
     * <p>Note as well that this code will intentionally crash when run in debug mode
     * from the main thread of the application. You should always execute shell
     * commands from a background thread.</p>
     * <p/>
     * <p>When in debug mode, the code will also excessively log the commands passed to
     * and the output returned from the shell.</p>
     * <p/>
     * <p>Though this function uses background threads to gobble STDOUT and STDERR so
     * a deadlock does not occur if the shell produces massive output, the output is
     * still stored in a List&lt;String&gt;, and as such doing something like <em>'ls -lR /'</em>
     * will probably have you run out of memory.</p>
     *
     * @param shell       The shell to use for executing the commands
     * @param commands    The commands to execute
     * @param environment List of all environment variables (in 'key=value' format) or null for defaults
     * @param wantSTDERR  Return STDERR in the output ?
     * @return Output of the commands, or null in case of an error
     */
    public static List<String> run(String shell, String[] commands, String[] environment, boolean wantSTDERR) {
        String shellUpper = shell.toUpperCase(Locale.ENGLISH);

        if (Debug.getSanityChecksEnabledEffective() && Debug.onMainThread()) {
            // check if we're running in the main thread, and if so, crash if we're in debug mode,
            // to let the developer know attention is needed here.

            Debug.log(ShellOnMainThreadException.EXCEPTION_COMMAND);
            throw new ShellOnMainThreadException(ShellOnMainThreadException.EXCEPTION_COMMAND);
        }
        Debug.logCommand(String.format("[%s%%] START", shellUpper));

        List<String> res = Collections.synchronizedList(new ArrayList<String>());

        try {
            // Combine passed environment with system environment
            if (environment != null) {
                Map<String, String> newEnvironment = new HashMap<String, String>();
                newEnvironment.putAll(System.getenv());
                int split;
                for (String entry : environment) {
                    if ((split = entry.indexOf("=")) >= 0) {
                        newEnvironment.put(entry.substring(0, split), entry.substring(split + 1));
                    }
                }
                int i = 0;
                environment = new String[newEnvironment.size()];
                for (Map.Entry<String, String> entry : newEnvironment.entrySet()) {
                    environment[i] = entry.getKey() + "=" + entry.getValue();
                    i++;
                }
            }

            // setup our process, retrieve STDIN stream, and STDOUT/STDERR gobblers
            Process process = Runtime.getRuntime().exec(shell, environment);
            DataOutputStream STDIN = new DataOutputStream(process.getOutputStream());
            StreamGobbler STDOUT = new StreamGobbler(shellUpper + "-", process.getInputStream(), res);
            StreamGobbler STDERR = new StreamGobbler(shellUpper + "*", process.getErrorStream(), wantSTDERR ? res : null);

            // start gobbling and write our commands to the shell
            STDOUT.start();
            STDERR.start();
            for (String write : commands) {
                Debug.logCommand(String.format("[%s+] %s", shellUpper, write));
                STDIN.write((write + "\n").getBytes("UTF-8"));
                STDIN.flush();
            }
            STDIN.write("exit\n".getBytes("UTF-8"));
            STDIN.flush();

            // wait for our process to finish, while we gobble away in the background
            process.waitFor();

            // make sure our threads are done gobbling, our streams are closed, and the process is 
            // destroyed - while the latter two shouldn't be needed in theory, and may even produce 
            // warnings, in "normal" Java they are required for guaranteed cleanup of resources, so
            // lets be safe and do this on Android as well
            try {
                STDIN.close();
            } catch (IOException e) {
            }
            STDOUT.join();
            STDERR.join();
            process.destroy();

            // in case of su, 255 usually indicates access denied
            if (SU.isSU(shell) && (process.exitValue() == 255)) {
                res = null;
            }
        } catch (IOException e) {
            // shell probably not found
            res = null;
        } catch (InterruptedException e) {
            // this should really be re-thrown
            res = null;
        }

        Debug.logCommand(String.format("[%s%%] END", shell.toUpperCase(Locale.ENGLISH)));
        return res;
    }

    protected static String[] availableTestCommands = new String[]{
            "echo -BOC-",
            "id"
    };

    /**
     * See if the shell is alive, and if so, check the UID
     *
     * @param ret          Standard output from running availableTestCommands
     * @param checkForRoot true if we are expecting this to shell  be running as root
     * @return true on success, false on error
     */
    protected static boolean parseAvailableResult(List<String> ret, boolean checkForRoot) {
        if (ret == null) return false;

        // this is only one of many ways this can be done
        boolean echo_seen = false;

        for (String line : ret) {
            if (line.contains("uid=")) {
                // id command is working, let's see if we are actually root
                return !checkForRoot || line.contains("uid=0");
            } else if (line.contains("-BOC-")) {
                // if we end up here, at least the su command starts some kind of shell,
                // let's hope it has root privileges - no way to know without additional
                // native binaries
                echo_seen = true;
            }
        }

        return echo_seen;
    }

}

