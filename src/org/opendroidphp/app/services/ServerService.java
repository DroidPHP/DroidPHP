package org.opendroidphp.app.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.actionbarsherlock.internal.widget.IcsToast;

import org.opendroidphp.app.R;
import org.opendroidphp.app.common.shell.SH;
import org.opendroidphp.app.common.tasks.ConnectServer;

public class ServerService extends Service {

    public static final String EXTRA_PORT = "EXTRA_PORT";
    private final IBinder mBinder = new ServerBinder();
    private PowerManager.WakeLock wakeLock = null;
    // private WifiManager.WifiLock wifiLock = null;

    private SharedPreferences preferences;

    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        initialize();

        return (START_NOT_STICKY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyService();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ServerBinder extends Binder {

        ServerService getService() {
            return ServerService.this;
        }
    }

    protected void initialize() {

        NotificationManager noti = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification notification = new Notification(R.drawable.ic_launcher, "Connected to Droidphp", System.currentTimeMillis());

        Context context = getApplicationContext();

        CharSequence contentTitle = "Droid";
        CharSequence contentText = "Web Service started";

        Intent notificationIntent = new Intent();
        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText,
                contentIntent);

        noti.notify(143, notification);

        if (preferences.getBoolean("enable_screen_on", false)) {

            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "DPScreenLock");
            wakeLock.acquire();

        }
        if (preferences.getBoolean("enable_lock_wifi", false)) {
            //
        }
        new Thread(new ConnectServer()).start();
        //(new ServerListener()).start();


    }

    protected void destroyService() {

        NotificationManager noti = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        noti.cancel(143);

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }


    class ServerListener extends Thread {

        @Override
        public void run() {

            while (true) {

                String res = SH.run("ps").get(0);
                if (!res.contains("php") && !res.contains("lighttpd") && !res.contains("mysqld")) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            IcsToast.makeText(
                                    getApplicationContext(),
                                    getString(R.string.core_apps_not_installed), Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                    destroyService();
                    break;
                }
            }
        }
    }
}