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

import org.opendroidphp.R;

public class ServerService extends Service {

    private final IBinder mBinder = new ServerBinder();
    private PowerManager.WakeLock wakeLock = null;

    private SharedPreferences preferences;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
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

    protected void initialize() {

        Context context = getApplicationContext();
        NotificationManager noti = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.ic_launcher, "DroidPHP service started", System.currentTimeMillis());
        CharSequence contentTitle = "DroidPHP";
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
            /*  not implemented */
        }
    }

    protected void destroyService() {
        NotificationManager notify = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notify.cancel(143);
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    public class ServerBinder extends Binder {

        ServerService getService() {
            return ServerService.this;
        }
    }
}