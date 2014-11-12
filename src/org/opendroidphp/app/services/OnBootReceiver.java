package org.opendroidphp.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences.getBoolean("enable_server_on_boot", false)) {
                Intent i = new Intent(context, ServerService.class);
                context.startService(i);
            }
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            BackgroundIntentService.performAction(context, BackgroundIntentService.ACTION_PACKAGE_REMOVED);
        }
    }
}