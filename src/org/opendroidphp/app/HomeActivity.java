package org.opendroidphp.app;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.internal.widget.IcsToast;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.opendroidphp.R;
import org.opendroidphp.app.common.tasks.DestroyServer;
import org.opendroidphp.app.common.utils.FileUtils;
import org.opendroidphp.app.fragments.dialogs.AboutDialogFragment;
import org.opendroidphp.app.fragments.dialogs.AskForInstallDialogFragment;
import org.opendroidphp.app.fragments.dialogs.OnEventListener;
import org.opendroidphp.app.services.ServerService;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.ankri.views.Switch;
import eu.chainfire.libsuperuser.Shell;

@android.annotation.TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class HomeActivity extends SherlockFragmentActivity {

    private Switch serverSwitch;
    private AtomicBoolean isInstalled = new AtomicBoolean(false);
    private SharedPreferences preferences;

    private CompoundButton.OnCheckedChangeListener changeServerState = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isEnable) {

            if (isEnable) {
                startService(new Intent(HomeActivity.this, ServerService.class));
            } else {

                String baseShell = (!preferences.getBoolean("run_as_root", false)) ? "sh" : "su";

                Runnable destroyServer = new DestroyServer()
                        .setShell(baseShell);
                Thread thread = new Thread(destroyServer);
                thread.start();

                NotificationManager notify = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notify.cancel(143);
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        serverSwitch = (Switch) findViewById(R.id.switch_lighttpd_php);
        serverSwitch.setEnabled(true);

        checkIfCoreInstalled();

        serverSwitch.setOnCheckedChangeListener(changeServerState);

        ((Button) findViewById(R.id.link)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/droidphp"));
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
        new ConnectionListenerTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        String basePort = preferences.getString("server_port", "8080");

        switch (item.getItemId()) {

            case R.id.web_admin:
                Intent intent;
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.
                        format("http://localhost:%s", basePort)));
                intent = Intent.createChooser(intent, "Choose browser");
                startActivity(intent);
                return true;
            case R.id.sql_admin:
                startActivity(new Intent(this, SQLShellActivity.class));
                return true;
            case R.id.extension:
                startActivity(new Intent(this, ExtensionActivity.class));
                return true;
            case R.id.settings:

                Intent prefIntent = new Intent(this, Preferences.class);
                startActivity(prefIntent);
                return true;

            case R.id.about:

                AboutDialogFragment aboutDialogFragment = new AboutDialogFragment();
                aboutDialogFragment.show(getSupportFragmentManager(), "about");
                return false;
        }

        return super.onMenuItemSelected(featureId, item);

    }

    protected void checkIfCoreInstalled() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (!FileUtils.checkIfExecutableExists()) {

                    AskForInstallDialogFragment dialog = new AskForInstallDialogFragment();

                    dialog.setOnEventListener(new OnEventListener() {

                        @Override
                        public void onSuccess() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    IcsToast.makeText(
                                            HomeActivity.this, getString(R.string.core_apps_installed), Toast.LENGTH_LONG)
                                            .show();
                                }
                            });

                            startService(new Intent(HomeActivity.this, ServerService.class));
                            new ConnectionListenerTask().execute();
                        }

                        @Override
                        public void onFailure() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    IcsToast
                                            .makeText(HomeActivity.this, getString(R.string.install_failed), Toast.LENGTH_LONG)
                                            .show();
                                }
                            });
                        }
                    });

                    dialog.show(getSupportFragmentManager(), "install");

                    if (FileUtils.checkIfExecutableExists()) {
                        isInstalled.set(true);
                    }
                }
            }
        }).start();
    }


    private class ConnectionListenerTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            String FIND_PROCESS = String.format(
                    "%s ps | %s grep \"components\"",
                    Constants.BUSYBOX_SBIN_LOCATION,
                    Constants.BUSYBOX_SBIN_LOCATION);

            List<String> rc = Shell.SH.run(FIND_PROCESS);

            boolean serverListing = false;
            boolean phpListing = false;
            boolean mysqlListing = false;

            String shellOutput = "";
            for (String buf : rc.toArray(new String[]{})) {
                shellOutput += buf;
            }

            serverListing = (shellOutput.contains("lighttpd") || shellOutput.contains("nginx"));
            phpListing = shellOutput.contains("php-cgi");
            mysqlListing = shellOutput.contains("mysqld");

            if (serverListing && phpListing && mysqlListing) {
                publishProgress("OK");
            } else {
                publishProgress("ERROR");
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {

            if (values[0].equals("OK")) {
                serverSwitch.setChecked(true);
            }
            if (values[0].equals("ERROR")) {
                serverSwitch.setChecked(false);
            }
        }
    }
}