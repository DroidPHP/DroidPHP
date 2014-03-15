/**
 * This file is part of DroidPHP
 *
 * (c) 2013 Shushant Kumar
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.github.DroidPHP;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.DroidPHP.Utils.ShellOutputFormatter;

public class ShellActivity extends SherlockActivity {

	protected static final String TAG = "com.github.DroidPHP.mySQLShell";
	private Context mContext = ShellActivity.this;
	private EditText et_shell;
	private TextView shellOutput;
	private static Process proc;
	private static java.io.InputStream stdout;
	private static java.io.OutputStream stdin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shell);
		ServerUtils.StrictModePermitAll();

		((Button) findViewById(R.id.runCmd))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						String cmd = et_shell.getText().toString();
						try {
							/**
							 * \r\n lets the code to be executed and getBytes
							 * converts chars in bytes Array
							 */
							stdin.write((cmd + "\r\n").getBytes());
							stdin.flush();
						} catch (Exception e) {
							// stdin.close();
							Log.e(TAG, "ERROR on Executing: mysql << " + cmd, e);

						}
						shellOutput.setText("");
						try {
							new ShellAsync().execute();
						} catch (Exception e) {

						}

					}
				});

	}

	@Override
	protected void onStart() {

		super.onStart();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		/**
		 * Retrieve the saved MYSQL user name and password from
		 * SharedPreferences
		 */
		String mUsername = prefs.getString("k_mysql_username", "root");
		String mPassword = prefs.getString("k_mysql_password", "");
		startMYSQLMointor(mUsername, mPassword);

		et_shell = (EditText) findViewById(R.id.et_shell_cmd);
		shellOutput = (TextView) findViewById(R.id.res_shell);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Home").setIcon(R.drawable.ic_refresh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
			return false;
		}

		if (item.getItemId() == 1) {
			/**
			 * Kill Process before starting new activity
			 */
			ServerUtils.killProcessByName("mysql-monitor");
			startActivity(new Intent(mContext, HomeActivity.class));

		}

		return true;
	}

	private static void startMYSQLMointor(String mUsername, String mPassword) {
		String[] query = new String[] {
				ServerUtils.getAppDirectory() + "/mysql-monitor", "-h",
				"127.0.0.1", "-T", "-f", "-r", "-t", "-E", "--disable-pager",
				"-n", "--user=" + mUsername, "--password=" + mPassword,
				"--default-character-set=utf8", "-L" };
		try {

			ProcessBuilder pb = (new ProcessBuilder(query));
			/**
			 * Set to false to avoid crashing application clash
			 */
			pb.redirectErrorStream(false);
			proc = pb.start();
			stdin = proc.getOutputStream();
			stdout = proc.getInputStream();

		} catch (IOException e) {

			Log.e(TAG, "MSQL Monitor", e);
			/**
			 * I have commented <string>proc.destroy</strong> because this is
			 * usually causing bug
			 */
			// proc.destroy();
		}

	}

	private class ShellAsync extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			java.io.BufferedReader buffr = new java.io.BufferedReader(
					new java.io.InputStreamReader(stdout));

			try {
				while (true) {
					String READ = ShellOutputFormatter.toHTML(buffr.readLine());

					if (READ == null) {
						break;
					}
					publishProgress(READ);

				}
				buffr.close();
			} catch (IOException e) {

				Log.e(TAG, "Unable to read mysql stream", e);

			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			/**
			 * Use append to show previous output
			 */
			shellOutput.setVisibility(1);

			shellOutput.append(android.text.Html.fromHtml(values[0]));
		}

	}

}
