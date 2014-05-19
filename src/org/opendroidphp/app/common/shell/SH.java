package org.opendroidphp.app.common.shell;

import java.util.List;

/**
 * This class provides utility functions to easily execute commands using SH
 */
public class SH {

    /**
     * Runs command and return output
     *
     * @param command The command to run
     * @return Output of the command, or null in case of an error
     */
    public static List<String> run(String command) {
        return Shell.run("sh", new String[]{command}, null, false);
    }

    /**
     * Runs command and return output
     *
     * @param command The command to run
     * @param env     The Environment variable
     * @return Output of the command, or null in case of an error
     */
    public static List<String> run(String command, String[] env) {
        return Shell.run("sh", new String[]{command}, env, false);
    }

    /**
     * Runs commands and return output
     *
     * @param commands The commands to run
     * @return Output of the commands, or null in case of an error
     */
    public static List<String> run(List<String> commands) {
        return Shell.run("sh", commands.toArray(new String[commands.size()]), null, false);
    }

    /**
     * Runs commands and return output
     *
     * @param commands The commands to run
     * @param env      The Environment variables
     * @return Output of the commands, or null in case of an error
     */
    public static List<String> run(List<String> commands, String[] env) {
        return Shell.run("sh", commands.toArray(new String[commands.size()]), env, false);
    }

    /**
     * Runs commands and return output
     *
     * @param commands The commands to run
     * @return Output of the commands, or null in case of an error
     */
    public static List<String> run(String[] commands) {
        return Shell.run("sh", commands, null, false);
    }
}
