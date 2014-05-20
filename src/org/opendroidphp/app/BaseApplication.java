package org.opendroidphp.app;

import android.app.Application;

import java.io.File;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }


    public boolean hasRootPermission() {

        if (!new File("/system/bin/su").exists()) {
            return false;
        }

        if (!new File("/system/xbin/su").exists()) {
            return false;
        }
        return true;
    }
}
