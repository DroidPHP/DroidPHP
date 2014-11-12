package org.opendroidphp.app.tasks;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.widget.TextView;

import org.opendroidphp.R;


abstract public class ProgressDialogTask<ParameterT, ProgressT, ReturnT> extends AsyncTask<ParameterT, ProgressT, ReturnT> {

    protected Handler handler = new Handler(Looper.getMainLooper());

    private Dialog progress;
    private Context context;

    private TextView tv_title;
    private TextView tv_message;
    //private ProgressBar progressBar;

    private String title;
    private String message;

    public ProgressDialogTask() {

    }

    public ProgressDialogTask(Context context) {
        this.context = context;
        handler.post(new Runnable() {
            @Override
            public void run() {
                createDialog().show();
            }
        });
    }

    public ProgressDialogTask(Context context, String title, String message) {
        this(context);
        this.title = title;
        this.message = message;
    }

    public ProgressDialogTask(Context context, int titleResId, int messageResId) {
        this(context, context.getString(titleResId), context.getString(messageResId));
    }

    protected Dialog createDialog() {

        LayoutInflater inflater = LayoutInflater.from(context);

        progress = new Dialog(context, R.style.Theme_DroidPHP_Dialog);
        progress.setContentView(inflater.inflate(R.layout.dialog_progress_holo, null));

        title = title != null ? title : context.getString(R.string.core_apps);
        message = message != null ? message : context.getString(R.string.installing_core_apps);

        //Title View
        tv_title = (TextView) progress.findViewById(R.id.title);
        tv_title.setText(title);

        //Message View
        tv_message = (TextView) progress.findViewById(R.id.message);
        tv_message.setText(message);

        //progressBar = (ProgressBar) progress.findViewById(R.id.pb_progress);
        return progress;
    }

    public ProgressDialogTask setTitle(String title) {
        this.title = title;
        tv_title.setText(title);
        return this;
    }

    public ProgressDialogTask setMessage(String message) {
        this.message = message;
        tv_message.setText(message);
        return this;
    }

    /**
     * Dismiss the Dialog
     */
    protected void dismissProgress() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    public Context getContext() {
        return context;
    }

    protected Dialog getDialog() {
        return progress;
    }
}
