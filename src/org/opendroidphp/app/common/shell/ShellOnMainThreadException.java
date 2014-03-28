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

/**
 * Exception class used to crash application when shell commands are executed
 * from the main thread, and we are in debug mode.
 */
@SuppressWarnings("serial")
public class ShellOnMainThreadException extends RuntimeException {
    public static final String EXCEPTION_COMMAND = "Application attempted to run a shell command from the main thread";
    public static final String EXCEPTION_NOT_IDLE = "Application attempted to wait for a non-idle shell to close on the main thread";
    public static final String EXCEPTION_WAIT_IDLE = "Application attempted to wait for a shell to become idle on the main thread";

    public ShellOnMainThreadException(String message) {
        super(message);
    }
}