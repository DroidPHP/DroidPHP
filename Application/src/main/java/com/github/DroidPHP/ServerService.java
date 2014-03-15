/**
 * This file is part of DroidPHP
 *
 * (c) 2013 Shushant Kumar
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.github.DroidPHP;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

public class ServerService extends Service {
	public static final String EXTRA_PORT = "EXTRA_PORT";
	private boolean isRunning = false;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String port = intent.getStringExtra(EXTRA_PORT);

		run(port);

		return (START_NOT_STICKY);
	}

	@Override
	public void onDestroy() {
		stop();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return (null);
	}

	@SuppressWarnings("deprecation")
	private void run(String port) {
		if (!isRunning) {

			isRunning = true;

			Notification note = new Notification(R.drawable.ic_launcher,
					"Server is successfully running",
					System.currentTimeMillis());
			Intent i = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://localhost:" + port));

			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);

			PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

			note.setLatestEventInfo(this, "DroidPHP", "Open http://localhost:"
					+ port, pi);
			note.flags |= Notification.FLAG_NO_CLEAR;

			startForeground(1337, note);
		}
	}

	private void stop() {
		if (isRunning) {

			isRunning = false;
			stopForeground(true);
		}
	}
}