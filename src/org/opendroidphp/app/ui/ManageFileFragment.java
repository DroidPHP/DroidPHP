package org.opendroidphp.app.ui;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;

import org.opendroidphp.R;
import org.opendroidphp.app.Constants;
import org.opendroidphp.app.common.parser.ServerParser;
import org.opendroidphp.app.listeners.OnInflationListener;
import org.opendroidphp.app.tasks.CommandTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageFileFragment extends SherlockFragment implements View.OnClickListener {

    public final static String EXTRA_CONF_FILE = "org.opendroidphp.CONF_FILE";
    protected static OnInflationListener sInflateCallback;

    private static boolean enableCreate = true;
    private static String defaultConfFile;

    private EditText etHostName;
    private EditText etHostUri;
    private EditText etHostPort;

    private ServerParser parser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_host, container, false);
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
    public void onClick(View view) {
        // new CreateOrUpdateConfigurationTask().execute();
        Bundle bundle = new Bundle();
        bundle.putString("host_name", etHostName.getText().toString());
        bundle.putString("host_port", etHostPort.getText().toString());
        bundle.putString("host_root", etHostUri.getText().toString());
        new UpdateTask(bundle).execute();
    }

    protected void prepareView(View view) {

        etHostName = (EditText) view.findViewById(R.id.host_name);
        etHostPort = (EditText) view.findViewById(R.id.host_port);
        etHostUri = (EditText) view.findViewById(R.id.host_location);
        Button btnCreate = (Button) view.findViewById(R.id.btn_create_host);
        if (getArguments() != null && !getArguments().isEmpty()) {
            enableCreate = false;
            defaultConfFile = getArguments().getString(EXTRA_CONF_FILE);
        }
        btnCreate.setOnClickListener(this);
    }


    private class UpdateTask extends CommandTask {
        private Bundle bundle;

        public UpdateTask(Bundle bundle) {
            this.bundle = bundle;
        }

        @Override
        protected String doInBackground(String... cmdArgs) {

            final String hostName = bundle.getString("host_name");
            final String hostPort = bundle.getString("host_port");
            final String hostRoot = bundle.getString("host_root");

            if (enableCreate) {
                List<String> command = Collections.unmodifiableList(new ArrayList<String>() {
                    {
                        add(CommandTask.CHANGE_PERMISSION.concat(Constants.INTERNAL_LOCATION + "/scripts/create_host.sh"));
                        add(String.format("%s/scripts/create_host.sh %s %s %s", Constants.INTERNAL_LOCATION, hostName, hostPort, hostRoot));
                    }
                });
                addCommand(command);
            }
            return super.doInBackground(cmdArgs);
        }

        @Override
        protected void onProgressUpdate(String... queryRes) {
            super.onProgressUpdate(queryRes);
            sInflateCallback.setOnFragmentReceived(new FileFragment());
        }
    }
}