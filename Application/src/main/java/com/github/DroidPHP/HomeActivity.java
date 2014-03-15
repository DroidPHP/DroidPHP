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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

/**
 * Activity to Home Screen
 */

@android.annotation.TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class HomeActivity extends SherlockActivity {

	// private final String TAG = "com.github.com.DroidPHP";
	final static int PROJECT_CODE = 143;
	final static int DEV_CODE = PROJECT_CODE + 1;
	final static int ABOUT_US_CODE = DEV_CODE + 1;
	final static int SETTING_CODE = ABOUT_US_CODE + 1;
	final static int SHELL_CODE = SETTING_CODE + 1;
	public static HashMap<String, String> server;
	private SharedPreferences prefs;

	private final Context mContext = HomeActivity.this;

	/**
	 * TextView for notification
	 */
	private static TextView tv_install_exec;

	/**
	 * Buttons for managing server state
	 */
	private static Button btn_runServer;
	private static Button btn_stopServer;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		// startService(new Intent(mContext, ServerService.class));
		ServerUtils.StrictModePermitAll();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		ServerUtils.setHttpDocsUri(prefs.getString("k_docs_dir", "htdocs"));
		ServerUtils.setServerPort(prefs.getString("k_server_port", "8080"));

		ServerUtils.setContext(mContext);

		btn_runServer = (Button) findViewById(R.id.RunTime_Http);
		btn_stopServer = (Button) findViewById(R.id.RunTime_Http_Kill);

		btn_runServer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ServerUtils.runServer();
				btn_runServer.setEnabled(false);
				btn_runServer.setText(R.string.server_online);
				String msg = "Unable to Start Server";
				try {
					if (isServerRunning()) {
						msg = "Server successfully Started";

					}
				} catch (IOException e) {

				}
				android.widget.Toast.makeText(mContext, msg,
						android.widget.Toast.LENGTH_LONG).show();

				Intent i = new Intent(mContext, ServerService.class);

				i.putExtra(ServerService.EXTRA_PORT,
						prefs.getString("k_server_port", "8080"));

				startService(i);

			}
		});

		btn_stopServer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btn_runServer.setEnabled(true);
				ServerUtils.stopServer();

			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();

		if (!ServerUtils.checkIfInstalled()) {
			tv_install_exec = (TextView) findViewById(R.id.tv_bin);
			tv_install_exec.setVisibility(1);
			tv_install_exec.setText(R.string.install_bin);
			new InstallerAsync().execute();
		}
		TextView tvs = (TextView) findViewById(R.id.server_msg);
		String str = "<strong>Document Root: " + ServerUtils.getHttpDirectory()
				+ "</strong><br/>" + "<strong>URL : http://localhost:"
				+ prefs.getString("k_server_port", "8080") + "</strong><br/>"
				+ "<strong>mySQL User :"
				+ prefs.getString("k_mysql_username", "root")
				+ "</strong><br/>" + "<strong>mySQL Password :"
				+ prefs.getString("k_mysql_password", "Leave it Blank")
				+ "</strong>";
		tvs.setText(android.text.Html.fromHtml(str));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, SHELL_CODE, 0, "Shell").setIcon(R.drawable.ic_compose)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		SubMenu sub = menu.addSubMenu(getString(R.string.abs_help));
		/**
		 * Set Icon for Submenu
		 */
		sub.setIcon(R.drawable.ic_launcher_settings);
		/**
		 * Build navigation for submenu
		 */
		sub.add(0, PROJECT_CODE, 0, getString(R.string.abs_project));
		// sub.add(0, DEV_CODE, 0, getString(R.string.abs_dev));
		sub.add(0, ABOUT_US_CODE, 0, getString(R.string.abs_about));
		sub.add(0, SETTING_CODE, 0, getString(R.string.abs_settings));
		sub.getItem().setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
			return false;
		}

		if (item.getItemId() == DEV_CODE) {

		} else if (item.getItemId() == ABOUT_US_CODE) {

			startActivity(new Intent(mContext, AboutActivity.class));

		} else if (item.getItemId() == PROJECT_CODE) {
			Intent mProjectIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://github.com/DroidPHP/DroidPHP"));
			startActivity(mProjectIntent);

		} else if (item.getItemId() == SETTING_CODE) {
			startActivity(new Intent(mContext, PreferenceActivity.class));
		} else if (item.getItemId() == SHELL_CODE) {
			startActivity(new Intent(mContext, ShellActivity.class));
		}

		return true;
	}

	final protected boolean isServerRunning() throws IOException {
		InputStream is;
		java.io.BufferedReader bf;
		boolean isRunning = false;
		try {
			is = Runtime.getRuntime().exec("ps").getInputStream();
			bf = new java.io.BufferedReader(new java.io.InputStreamReader(is));

			String r;
			while ((r = bf.readLine()) != null) {
				if (r.contains("lighttpd")) {
					isRunning = true;
					break;
				}

			}
			is.close();
			bf.close();

		} catch (IOException e) {
			e.printStackTrace();

		}
		return isRunning;

	}

	private class InstallerAsync extends
			android.os.AsyncTask<Void, String, Void> {
		String loc;

		@Override
		protected Void doInBackground(Void... arg0) {

			loc = ServerUtils.getAppDirectory() + "/";
			try {

				dirChecker("");
				ZipInputStream zin = new ZipInputStream(getAssets().open(
						"data.zip"));
				ZipEntry ze = null;

				while ((ze = zin.getNextEntry()) != null) {

					if (ze.isDirectory()) {
						dirChecker(ze.getName());
					} else {
						FileOutputStream fout = new FileOutputStream(loc
								+ ze.getName());

						publishProgress("Extracting : " + ze.getName());

						byte[] buffer = new byte[4096 * 10];
						int length = 0;
						while ((length = zin.read(buffer)) != -1) {

							fout.write(buffer, 0, length);

						}

						zin.closeEntry();
						fout.close();
					}

				}
				publishProgress("ok");

				zin.close();

			} catch (Exception e) {
                e.printStackTrace();
				publishProgress("error");
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);

			String text = "Error";
			tv_install_exec.setVisibility(1);
			if (values[0] == "error")
				text = getString(R.string.bin_error);
			if (values[0] == "ok")
				text = getString(R.string.bin_installed);
			else
				text = values[0];

			tv_install_exec.setText(text);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

		private void dirChecker(String dir) {
			File f = new File(loc + dir);

			if (!f.isDirectory()) {
				f.mkdirs();
			}
		}

	}

}
