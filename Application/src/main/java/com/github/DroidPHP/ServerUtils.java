/**
 * This file is part of DroidPHP
 *
 * (c) 2013 Shushant Kumar
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.github.DroidPHP;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public final class ServerUtils {

	final static String TAG = "com.github.com.DroidPHP.ServerUtils";
	/**
	 * Holds the Current active instance of MYSQL Monitor
	 */
	private static Process proc;
	/**
	 * Hold the context of Current Activity
	 */
	static Context mContext;
	private static java.io.OutputStream stdin;
	private static java.io.InputStream stdout;
	private static String serverPort;
	private static String httpdUri;

	final public static void setContext(Context mContext) {
		ServerUtils.mContext = mContext;

	}

	/*
	 * Internal path to a directory assigned to the package for its persistent
	 * data.
	 */

	final public static String getAppDirectory() {

		return mContext.getApplicationInfo().dataDir;

	}

	/*
	 * External path to a directory assigned to the WWW
	 */

	final public static String getHttpDirectory() {

		return android.os.Environment.getExternalStorageDirectory().getPath()
				+ "/" + httpdUri;

	}

	/**
	 * Instead of killing process by its PID you can use this method to kill
	 * process by specifying its name
	 * 
	 * @param mProcessName
	 *            Name Of Process that you want to kill
	 * @return boolean
	 */

	final public static Boolean killProcessByName(String mProcessName) {

		return execCommand(getAppDirectory() + "/killall " + mProcessName);
	}

	/**
	 * This method is responsible for invoking
	 * {@link com.github.DroidPHP.ServerUtils.killProcessByName} which kills all the running
	 * instances of <strong>PHP</strong>, <strong>LIGHTTPD</strong>,
	 * <strong>MYSQLD</strong>, <strong>MYSQL Monitor </strong>
	 * 
	 * @return
	 */
	final public static void setServerPort(String port) {
		serverPort = port;

	}

	final public static void setHttpDocsUri(String Uri) {

		httpdUri = Uri;

	}

	final public static void stopServer() {

		/**
		 * 
		 * Kill the Running Instances of all called process name. PHP is
		 * automatically kill when instance <strong>LIGHTTPD</strong> is
		 * destroyed. Anyway if it is unable to kill <strong>PHP</strong> lets
		 * kill by invoking <b>killall</b> command
		 */
		killProcessByName("lighttpd");
		/**
		 * see above doc why i called PHP here
		 */
		killProcessByName("php");
		killProcessByName("mysqld");
		killProcessByName("mysql-monitor");

	}

	final public static void runServer() {
		restoreOrCreateServerData();
		restoreConfiguration("lighttpd.conf");
		restoreConfiguration("php.ini");
		restoreConfiguration("mysql.ini");
		setPermission();

		String[] serverCmd = { getAppDirectory() + "/lighttpd", "-f",
				getHttpDirectory() + "/conf/lighttpd.conf", "-D"

		};
		String[] mySQLCmd = { getAppDirectory() + "/mysqld",
				"--defaults-file=" + getHttpDirectory() + "/conf/mysql.ini",
				// "--pid-file=" + getHttpDirectory() + "/proc/mysqld.pid",
				"--user=root",
				"--language=" + getAppDirectory() + "/share/mysql/english" };
		try {
			(new ProcessBuilder(serverCmd)).start();
			Log.i(TAG, "LIGHTTPD is successfully running");
		} catch (Exception e) {
			Log.e(TAG, "Unable to start LIGHTTPD", e);
		}

		try {
			(new ProcessBuilder(mySQLCmd)).start();
			Log.i(TAG, "MYSQLD is successfully running");
		} catch (Exception e) {
			Log.e(TAG, "Unable to start MYSQLD", e);
		}

		return;

	}

	/**
	 * 
	 * @param mCommand
	 *            hold the command which will be executed by invoking {@link
	 *            Runtime.getRuntime.exec(...)}
	 * @return boolean
	 * @throws java.io.IOException
	 *             if it unable to execute the command
	 */

	final public static boolean execCommand(String mCommand) {

		/*
		 * Create a new Instance of Runtime
		 */
		Runtime r = Runtime.getRuntime();
		try {
			/**
			 * Executes the command
			 */
			r.exec(mCommand);

		} catch (IOException e) {

			Log.e(TAG, "execCommand", e);

			r = null;

			return false;
		}

		return true;

	}

	final static private void setPermission() {
		try {

			execCommand("/system/bin/chmod 777 " + getAppDirectory()
					+ "/lighttpd");
			execCommand("/system/bin/chmod 777 " + getAppDirectory()
					+ "/php-cgi");
			execCommand("/system/bin/chmod 777 " + getAppDirectory()
					+ "/mysqld");
			execCommand("/system/bin/chmod 777 " + getAppDirectory()
					+ "/mysql-monitor");
			execCommand("/system/bin/chmod 777 " + getAppDirectory()
					+ "/killall");
			execCommand("/system/bin/chmod 755 " + getAppDirectory() + "/tmp");

		} catch (Exception e) {
			Log.e(TAG, "setPermission", e);
		}

	}

	final private static void restoreConfiguration(String fileName) {

		File isConf = new File(getHttpDirectory() + "/conf/" + fileName);
		if (!isConf.exists()) {

			try {

				String mString;

				java.io.InputStream mStream = mContext.getAssets().open(
						fileName, AssetManager.ACCESS_BUFFER);

				java.io.BufferedWriter outputStream = new java.io.BufferedWriter(
						new java.io.FileWriter(getHttpDirectory() + "/tmp/"
								+ fileName));

				int c;
				while ((c = mStream.read()) != -1) {
					outputStream.write(c);
				}
				outputStream.close();
				mStream.close();

				mString = org.apache.commons.io.FileUtils.readFileToString(
						new File(getHttpDirectory() + "/tmp/" + fileName),
						"UTF-8");

				mString = mString.replace("%app_dir%", getAppDirectory());
				mString = mString.replace("%http_dir%", getHttpDirectory());
				mString = mString.replace("%port%", serverPort);
				org.apache.commons.io.FileUtils.writeStringToFile(new File(
						getHttpDirectory() + "/conf/" + fileName), mString,
						"UTF-8");
			} catch (Exception e) {
				Log.e(TAG, "Unable to copy " + fileName + " from assets", e);

			}
		}

	}

	final private static void restoreOrCreateServerData() {

		File mFile = new File(getHttpDirectory() + "/conf/");
		if (!mFile.exists())
			mFile.mkdirs();

		mFile = new File(getHttpDirectory() + "/www/");

		if (!mFile.exists())
			mFile.mkdir();

		mFile = new File(getHttpDirectory() + "/logs/");

		if (!mFile.exists())
			mFile.mkdir();
		mFile = new File(getHttpDirectory() + "/tmp/");

		if (!mFile.exists())
			mFile.mkdir();

		mFile = null;

	}

	public static boolean checkIfInstalled() {

		File mPhp = new File(getAppDirectory() + "/php-cgi");
		File mMySql = new File(getAppDirectory() + "/mysqld");
		File mLighttpd = new File(getAppDirectory() + "/lighttpd");
		File mMySqlMon = new File(getAppDirectory() + "/mysql-monitor");

		if (mPhp.exists() && mMySql.exists() && mLighttpd.exists()
				&& mMySqlMon.exists()) {

            Log.d(TAG, "Installed");

			return true;

		}

		return false;

	}

	/**
	 * StrictMode.ThreadPolicy was introduced science API level 9 and its
	 * default implementation has been change since API Level 11 and do not
	 * allow network operation on UI Thread and lots more reason Hence, its
	 * better to perform These Stuff outside UI Thread you may do it in
	 * <strong>AsyncTask</strong> And this method allow to permit all
	 * restriction made by system itself.
	 */
	@android.annotation.TargetApi(android.os.Build.VERSION_CODES.GINGERBREAD)
	public static void StrictModePermitAll() {
		android.os.StrictMode
				.setThreadPolicy((new android.os.StrictMode.ThreadPolicy.Builder())
						.permitAll().build());
	}

	/**
	 * 
	 * @param mUsername
	 * @param mPassword
	 */

	public static void startMYSQLMointor(String mUsername, String mPassword) {
		String[] query = new String[] { getAppDirectory() + "/mysql-monitor",
				"-h", "127.0.0.1", "-T", "-f", "-r", "-t", "-E",
				"--disable-pager", "-n", "--user=" + mUsername,
				"--password=" + mPassword, "--default-character-set=utf8", "-L" };
		try {

			ProcessBuilder pb = (new ProcessBuilder(query));
			pb.redirectErrorStream(true);
			proc = pb.start();
			stdin = proc.getOutputStream();
			stdout = proc.getInputStream();

		} catch (IOException e) {

			Log.e(TAG, "MSQL Monitor", e);
			/**
			 * I have commented <string>proc.destroy</strong> because this is
			 * usually cause bug
			 */
			// proc.destroy();
		}

	}

	/**
	 * @see com.github.DroidPHP.mySQLShell().ShellAsync()
	 * @param mSQLShellCmd
	 * @return
	 * @throws java.io.IOException
	 */

	public static String executeSQLShellCMD(String mSQLShellCmd)
			throws IOException {

		try {
			/**
			 * \r\n lets the code to be executed and getBytes converts chars in
			 * bytes Array
			 */
			stdin.write((mSQLShellCmd + "\r\n").getBytes());
			stdin.flush();
		} catch (Exception e) {
			// stdin.close();
			Log.e(TAG, "ERROR on Executing: mysql << " + mSQLShellCmd, e);

		}
		return readExecutedSQLShellCMD();

	}

	/**
	 * This Method is not in use any more because its causing bug after end of
	 * iteration
	 * 
	 * @see com.github.DroidPHP.mySQLShell().ShellAsync()
	 * @return
	 * @throws java.io.IOException
	 */

	public static String readExecutedSQLShellCMD() throws IOException {
		/*
		 * Returns an input stream that is connected to the standard output
		 * stream (stdout) of the native process represented by this object
		 */
		java.io.BufferedReader buffr = new java.io.BufferedReader(
				new java.io.InputStreamReader(stdout));
		String sb = new String();
		try {
			while (true) {
				String READ = buffr.readLine();

				if (READ == null) {
					break;
				}
				sb += READ + "\n";

			}

			Log.e(TAG, "Result = " + sb.toString());
		} catch (IOException e) {

			stdout.close();
			Log.e(TAG, "Unable to read mysql stream", e);

		}

		return sb.toString();

	}
}
