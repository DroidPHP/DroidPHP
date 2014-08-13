package org.opendroidphp.app.ui;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.inject.Inject;

import org.opendroidphp.R;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import roboguice.RoboGuice;
import roboguice.inject.InjectView;

public class MySqlFragment extends SherlockFragment {

    protected static Shell.Interactive interactive;
    @InjectView(R.id.run_cmd)
    Button mRunCommand;
    @InjectView(R.id.shell_result)
    TextView mResult;
    @Inject
    Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_mysql, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);

        mRunCommand.setOnClickListener(new ExecuteQuery());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectViewMembers(this);
    }

    private final class ExecuteQuery implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
            String username = preferences.getString("mysql_username", "root");
            String password = preferences.getString("mysql_password", "");

            String baseShell = String.format("%s -h 127.0.0.1 -T -f -r -t -E --disable-pager -n --user=%s --password=%s --default-character-set=utf8 -L",
                    username,
                    password);

            interactive = new Shell.Builder().
                    setShell(baseShell).
                    setWantSTDERR(true).
                    setWatchdogTimeout(5).
                    setMinimalLogging(true).
                    open(new Shell.OnCommandResultListener() {
                        @Override
                        public void onCommandResult(int commandCode, final int exitCode, final List<String> output) {

                            if (exitCode != Shell.OnCommandResultListener.SHELL_RUNNING) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (output != null) {
                                            for (String error : output) {
                                                mResult.append(error);
                                            }
                                        }
                                    }
                                });

                            }

                        }
                    });
        }
    }
}