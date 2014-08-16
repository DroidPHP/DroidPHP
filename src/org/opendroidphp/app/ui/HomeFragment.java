package org.opendroidphp.app.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.actionbarsherlock.app.SherlockFragment;

import org.opendroidphp.R;
import org.opendroidphp.app.AppController;
import org.opendroidphp.app.Constants;
import org.opendroidphp.app.common.tasks.DestroyServer;
import org.opendroidphp.app.common.utils.FileUtils;
import org.opendroidphp.app.fragments.dialogs.AskForInstallDialogFragment;
import org.opendroidphp.app.fragments.dialogs.OnEventListener;
import org.opendroidphp.app.services.ServerService;

import java.util.List;

import de.ankri.views.Switch;
import eu.chainfire.libsuperuser.Shell;

@android.annotation.TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class HomeFragment extends SherlockFragment {

    Switch serverSwitch;

    private SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        prepareView(rootView);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
        new AsyncSystemRequirement().execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        new ConnectionListenerTask().execute();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (preferences.getBoolean("enable_server_on_app_startup", false)) {
            getSherlockActivity().
                    startService(new Intent(getSherlockActivity(), ServerService.class));
        }
        new ConnectionListenerTask().execute();
    }

    protected void prepareView(View view) {

        serverSwitch = (Switch) view.findViewById(R.id.switch_lighttpd_php);
        serverSwitch.setEnabled(true);
        serverSwitch.setOnCheckedChangeListener(new ServerListener());
    }

    protected void displayInstallDialog() {

        AskForInstallDialogFragment dialog = new AskForInstallDialogFragment();
        dialog.setOnEventListener(new OnEventListener() {
            @Override
            public void onSuccess() {
                AppController.toast(getSherlockActivity(), getString(R.string.core_apps_installed));
                getSherlockActivity().
                        startService(new Intent(getSherlockActivity(), ServerService.class));
                new ConnectionListenerTask().execute();
            }

            @Override
            public void onFailure() {
                AppController.toast(getSherlockActivity(), getString(R.string.core_apps_not_installed));
            }
        });
        dialog.show(getFragmentManager(), dialog.getClass().getSimpleName());

    }

    protected class AsyncSystemRequirement extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if (!FileUtils.checkIfExecutableExists()) {
                displayInstallDialog();
                return null;
            }
            return null;
        }
    }

    protected class ServerListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isEnable) {

            if (isEnable) {
                getSherlockActivity().
                        startService(new Intent(getSherlockActivity(), ServerService.class));

            } else {
                String baseShell = (!preferences.getBoolean("run_as_root", false)) ? "sh" : "su";

                Runnable destroyServer = new DestroyServer().
                        setShell(baseShell);
                Thread thread = new Thread(destroyServer);
                thread.start();

                NotificationManager notify = (NotificationManager) getSherlockActivity().
                        getSystemService(Context.NOTIFICATION_SERVICE);
                notify.cancel(143);
            }
        }
    }

    private class ConnectionListenerTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            String FIND_PROCESS = String.format(
                    "%s ps | %s grep \"components\"",
                    Constants.BUSYBOX_SBIN_LOCATION,
                    Constants.BUSYBOX_SBIN_LOCATION);

            List<String> rc = Shell.SH.run(FIND_PROCESS);

            boolean serverListing;
            boolean phpListing;
            boolean mysqlListing;

            String shellOutput = "";
            for (String buf : rc.toArray(new String[rc.size()])) {
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