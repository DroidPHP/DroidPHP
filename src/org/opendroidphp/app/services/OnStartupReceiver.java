package org.opendroidphp.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by shushant on 3/19/14.
 */
public class OnStartupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("BOOT", "completed");

        /*Toast.makeText(context,
                "Boot successfully", Toast.LENGTH_LONG)
                .show();*/

    }

}
