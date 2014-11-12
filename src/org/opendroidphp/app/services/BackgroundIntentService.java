package org.opendroidphp.app.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.opendroidphp.app.Constants;

import eu.chainfire.libsuperuser.Shell;

public class BackgroundIntentService extends IntentService {
    public static final String ACTION_BOOT_COMPLETE = "boot_complete";
    public static final String ACTION_PACKAGE_REMOVED = "package_removed";

    public BackgroundIntentService() {
        super("BackgroundIntentService");
    }

    public static void performAction(Context context, String action) {
        performAction(context, action, null);
    }

    public static void performAction(Context context, String action, Bundle extras) {
        if ((context == null) || (action == null) || action.equals("")) return;

        Intent svc = new Intent(context, BackgroundIntentService.class);
        svc.setAction(action);
        if (extras != null) svc.putExtras(extras);
        context.startService(svc);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if ((action == null) || (action.equals(""))) return;
        if (action.equals(ACTION_BOOT_COMPLETE)) {
            onBootComplete();
        }
        if (action.equals(ACTION_PACKAGE_REMOVED)) {
            onPackageRemoved();
        }
        // you can define more options here... pass parameters through the "extra" values
    }

    protected void onBootComplete() {
        //Intent intent = new Intent(getApplicationContext(), ServerService.class);
        //getApplicationContext().startService(intent);
    }

    protected void onPackageRemoved() {
        //remove every thing under components directory
        Shell.SH.run(String.format("rm -R %s", Constants.INTERNAL_LOCATION + "/components"));
    }
}
