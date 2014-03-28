/*
 * *
 *  * This file is part of DroidPHP
 *  *
 *  * (c) 2014 Shushant Kumar
 *  *
 *  * For the full copyright and license information, please view the LICENSE
 *  * file that was distributed with this source code.
 *
 */
package org.opendroidphp.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.opendroidphp.app.common.components.BaseExecutor;
import org.opendroidphp.app.common.components.LighttpdExecutor;
import org.opendroidphp.app.common.components.MySqlExecutor;
import org.opendroidphp.app.common.components.PHPExecutor;
import org.opendroidphp.app.common.inject.InjectView;
import org.opendroidphp.app.common.inject.Injector;
import org.opendroidphp.app.common.utils.FileUtils;
import org.opendroidphp.app.fragments.dialogs.AboutDialogFragment;
import org.opendroidphp.app.fragments.dialogs.NotifyInstalltionDialogFragment;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import de.ankri.views.Switch;

/**
 * Activity to Home Screen
 */

@android.annotation.TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class HomeActivity extends SherlockFragmentActivity {


    static {
        //BaseExecutor should be registered before other
        ComponentExecutorPool.registerExecutor(BaseExecutor.class);

        ComponentExecutorPool.registerExecutor(LighttpdExecutor.class);
        ComponentExecutorPool.registerExecutor(PHPExecutor.class);
        ComponentExecutorPool.registerExecutor(MySqlExecutor.class);

    }

    private final Activity context = this;

    @InjectView(R.id.update_msg)
    private TextView mUpdateMessage;

    @InjectView(R.id.switch_lighttpd_php)
    private Switch manageServer;

//    @InjectView(R.id.message_container)
//    private LinearLayout messageLayout;

    private AtomicBoolean isInstalled = new AtomicBoolean(false);


    /**
     * Events Listeners
     */
    private CompoundButton.OnCheckedChangeListener manageServerEventListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isEnable) {
            Runnable runnable = null;

            if (isEnable) {

                //server enabled
                runnable = new Runnable() {
                    @Override
                    public void run() {

                        try {

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

                            SystemClock.sleep(150);

                            //FIXME: this is not executed after installer is closed
                            if (FileUtils.checkIfExecutableExists()) {
                                //let me guss installation is completed lets execute the all registered executors

                                ComponentExecutorPool.connectAll();
                            }


                        } catch (Exception e) {

                        }

                    }
                };

            } else {

                //server disable
                runnable = new Runnable() {

                    @Override
                    public void run() {

                        try {

                            ComponentExecutorPool.destroyAll();
                        } catch (Exception e) {

                        }
                    }
                };
            }

            new Thread(runnable).start();

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Injector.get(this).inject();
        manageServer.setEnabled(true);
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

        //check server status by looking lighttpd.pid file

        new Thread(new Runnable() {

            @Override
            public void run() {

                if ((new File(Constants.INTERNAL_LOCATION + "/tmp/lighttpd.pid").exists())) {
                    manageServer.post(new Runnable() {
                        @Override
                        public void run() {
                            manageServer.setChecked(true);
                        }
                    });


                } else {
                    manageServer.post(new Runnable() {
                        @Override
                        public void run() {
                            manageServer.setChecked(false);
                        }
                    });
                }

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

        final Runnable enableRunnable = new Runnable() {
            @Override
            public void run() {
                //so required file exist so enable the server feature
                //  manageServer.setEnabled(true);
            }
        };

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

