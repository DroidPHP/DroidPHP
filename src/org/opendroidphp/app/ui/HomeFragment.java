package org.opendroidphp.app.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.actionbarsherlock.app.SherlockFragment;

import org.opendroidphp.R;
import org.opendroidphp.app.Constants;
import org.opendroidphp.app.common.utils.FileUtils;
import org.opendroidphp.app.fragments.dialogs.DialogHelpers;
import org.opendroidphp.app.listeners.OnInflationListener;
import org.opendroidphp.app.services.ServerService;
import org.opendroidphp.app.tasks.CommandTask;

import java.util.List;

import de.ankri.views.Switch;
import eu.chainfire.libsuperuser.Shell;

@android.annotation.TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class HomeFragment extends SherlockFragment implements View.OnClickListener {

    private static OnInflationListener sInflateCallback;
    private Switch sEnableServer;
    private SharedPreferences preferences;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        prepareView(rootView);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            sInflateCallback = (OnInflationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement OnInflateViewListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getSherlockActivity();
        preferences = PreferenceManager.
                getDefaultSharedPreferences(getSherlockActivity());
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
        sEnableServer = (Switch) view.findViewById(R.id.sw_enable_server);
        sEnableServer.setEnabled(true);
        sEnableServer.setOnCheckedChangeListener(new ServerListener());
        view.findViewById(R.id.ll_mysql_shell).setOnClickListener(this);
        view.findViewById(R.id.ll_package).setOnClickListener(this);
        view.findViewById(R.id.ll_vhost).setOnClickListener(this);
        //view.findViewById(R.id.ll_update).setOnClickListener(this);
        view.findViewById(R.id.ll_about).setOnClickListener(this);
        view.findViewById(R.id.ll_uninstall).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ll_uninstall) {
            CommandTask task = CommandTask.createForUninstall(context);
            task.execute();
        } else
            sInflateCallback.setOnViewIdReceived(view.getId());
    }

    protected void confirmDialogForInstall() {
        DialogFragment fragment = DialogHelpers.factoryForInstall(context);
        fragment.show(getFragmentManager(), getClass().getSimpleName());
    }

    private class AsyncSystemRequirement extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if (!FileUtils.checkIfExecutableExists()) {
                confirmDialogForInstall();
                return null;
            }
            return null;
        }
    }

    protected class ServerListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isEnable) {

            boolean enableSU = preferences.getBoolean("run_as_root", false);
            String execName = preferences.getString("use_server_httpd", "lighttpd");
            String bindPort = preferences.getString("server_port", "8080");

            if (isEnable) {
                context.startService(new Intent(context, ServerService.class));
                CommandTask task = CommandTask.createForConnect(context, execName, bindPort);
                task.enableSU(enableSU);
                task.execute();
            } else {
                CommandTask task = CommandTask.createForDisconnect(context);
                task.enableSU(enableSU);
                task.execute();

                NotificationManager notify = (NotificationManager) context.
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
                sEnableServer.setChecked(true);
            }
            if (values[0].equals("ERROR")) {
                sEnableServer.setChecked(false);
            }
        }
    }
}