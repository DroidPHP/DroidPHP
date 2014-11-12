package org.opendroidphp.app.tasks;


import android.content.Context;

import org.opendroidphp.R;
import org.opendroidphp.app.AppController;
import org.opendroidphp.app.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class CommandTask extends ProgressDialogTask<String, String, String> {

    public final static String CHANGE_PERMISSION = "/system/bin/chmod 755 ";
    public final static String COMMAND_EXECUTED = "command_executed";

    private List<String> mCommandList;
    private boolean enableSU;
    private int command_executed;

    public CommandTask() {

    }

    public CommandTask(Context context) {
        super(context);
    }

    public CommandTask(Context context, String title, String message) {
        super(context, title, message);
    }

    public CommandTask(Context context, int titleResId, int messageResId) {
        super(context, context.getString(titleResId), context.getString(messageResId));
    }

    public static CommandTask createForConnect(final Context c, final String execName, final String bindPort) {
        List<String> command = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(CHANGE_PERMISSION.concat(Constants.INTERNAL_LOCATION + "/scripts/server-sh.sh"));
                add(String.format("%s/scripts/server-sh.sh %s %s", Constants.INTERNAL_LOCATION, execName, bindPort));
            }
        });
        CommandTask task = new CommandTask(c, R.string.server_loading, R.string.turning_on_server);
        task.addCommand(command);
        return task;
    }

    public static CommandTask createForDisconnect(Context c) {
        List<String> command = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(Constants.INTERNAL_LOCATION + "/scripts/shutdown-sh.sh");
            }
        });
        CommandTask task = new CommandTask(c, R.string.server_loading, R.string.turning_off_server);
        task.addCommand(command);
        return task;
    }

    public static CommandTask createForUninstall(final Context c) {
        List<String> command = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(String.format("rm -R %s", Constants.INTERNAL_LOCATION.concat("/")));
            }
        });
        CommandTask task = new CommandTask(c, R.string.uninstalling_core_apps, R.string.core_uninstalling);
        task.addCommand(command);
        task.setNotification(R.string.core_apps_uninstalled);
        return task;
    }

    @Override
    protected String doInBackground(String... cmdArgs) {
        checkFilesystem();
        String command[] = mCommandList.toArray(new String[mCommandList.size()]);
        String shell = enableSU ? "su" : "sh";
        List<String> res = Shell.run(shell, command, null, true);
        for (String queryRes : res)
            publishProgress(queryRes);
        publishProgress(COMMAND_EXECUTED);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        dismissProgress();
    }

    @Override
    protected void onProgressUpdate(String... queryRes) {
        super.onProgressUpdate(queryRes);
        setMessage(queryRes[0]);
        if (queryRes[0].equals(COMMAND_EXECUTED)) {
            AppController.toast(getContext(), getContext().getString(command_executed));
        }
    }

    public CommandTask addCommand(List<String> commandList) {
        this.mCommandList = commandList;
        return this;
    }

    public boolean isEnabledSU() {
        return enableSU;
    }

    public CommandTask enableSU(final boolean enableSU) {
        this.enableSU = enableSU;
        return this;
    }

    public CommandTask toggleEnableSU() {
        return enableSU(!enableSU);
    }

    protected CommandTask setNotification(int resId) {
        command_executed = resId;
        return this;
    }

    protected void checkFilesystem() {
        List<String> listFiles = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add("/logs");
                add("/conf");
                add("/conf/nginx/logs");
                add("/hosts/nginx");
                add("/hosts/lighttpd");
                add("/sessions");
                add("/packages");
                add("/htdocs");
                add("/tmp");
            }
        });

        for (String filePath : listFiles) {
            File f = new File(Constants.PROJECT_LOCATION.concat(filePath));
            if (!f.exists()) f.mkdirs();
        }
    }

}