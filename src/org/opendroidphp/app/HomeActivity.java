package org.opendroidphp.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.opendroidphp.app.common.tasks.DestroyServer;
import org.opendroidphp.app.common.utils.FileUtils;
import org.opendroidphp.app.fragments.dialogs.AboutDialogFragment;
import org.opendroidphp.app.fragments.dialogs.NotifyInstalltionDialogFragment;
import org.opendroidphp.app.services.ServerService;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import de.ankri.views.Switch;

/**
 * Activity to Home Screen
 */

@android.annotation.TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class HomeActivity extends SherlockFragmentActivity {

    private Switch manageServer;

    private AtomicBoolean isInstalled = new AtomicBoolean(false);
    private SharedPreferences preferences;


    /**
     * Events Listeners
     */
    private CompoundButton.OnCheckedChangeListener manageServerEventListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isEnable) {


            if (isEnable) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!FileUtils.checkIfExecutableExists()) {

                            boolean exitInfiniteLoop = false;
                            SherlockDialogFragment dialogFragment = new NotifyInstalltionDialogFragment();
                            dialogFragment.show(getSupportFragmentManager(), "install");
                            SystemClock.sleep(150);

                            //i love infinity :D
                            // dirty hack to avoid calling ComponentExecutorPool before dialog is dismissed
                            do {
                                if (!dialogFragment.getDialog().isShowing()) {
                                    exitInfiniteLoop = true;
                                    Log.d("DIALOG", "Exit from dialog");
                                }

                            } while (!exitInfiniteLoop);
                        }
                    }
                }).start();

                startService(new Intent(HomeActivity.this, ServerService.class));
            } else {
                new Thread(new DestroyServer()).start();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manageServer = (Switch) findViewById(R.id.switch_lighttpd_php);
        manageServer.setEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        checkIfCoreInstalled();

        manageServer.setOnCheckedChangeListener(manageServerEventListener);

        ((Button) findViewById(R.id.link)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/droidphp"));
                //intent = Intent.createChooser(intent, "Choose browser");
                startActivity(intent);


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (preferences.getBoolean("enable_server_on_app_startup", false)) {
            startService(new Intent(this, ServerService.class));
        }
        //check server status by looking lighttpd.pid file


        new Thread(new Runnable() {

            @Override
            public void run() {
                boolean pidExist = false;

                if ((new File(Constants.INTERNAL_LOCATION + "/tmp/lighttpd.pid").exists())) {
                    pidExist = true;
                }
                final boolean finalPidExist = pidExist;
                manageServer.post(new Runnable() {
                    @Override
                    public void run() {
                        manageServer.setChecked(finalPidExist);
                    }
                });

            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {

            case R.id.web_admin:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://localhost:8080"));
                intent = Intent.createChooser(intent, "Choose browser");

                if (intent != null) {
                    startActivity(intent);
                }
                return true;

            case R.id.settings:

                Intent prefIntent = new Intent(getBaseContext(), Preferences.class);
                startActivity(prefIntent);
                return true;

            case R.id.about:

                new AboutDialogFragment()
                        .show(getSupportFragmentManager(), "about");

                return false;
        }

        return super.onMenuItemSelected(featureId, item);

    }


    protected void checkIfCoreInstalled() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (!FileUtils.checkIfExecutableExists()) {

                    new NotifyInstalltionDialogFragment()
                            .show(getSupportFragmentManager(), "install");

                    //recheck if file exist
                    if (FileUtils.checkIfExecutableExists()) {
                        isInstalled.set(true);
//                         manageServer.post(enableRunnable);
                    }

                }

            }
        }).start();

    }


}

