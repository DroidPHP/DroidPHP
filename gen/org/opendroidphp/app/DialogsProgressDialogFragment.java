package org.opendroidphp.app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

import java.util.concurrent.TimeUnit;

public class DialogsProgressDialogFragment extends SherlockDialogFragment {


    private final class InstallerTask extends AsyncTask<Void, Integer, Void> {
        private int mInitalPosition = 0;


        @Override
        protected Void doInBackground(Void... params) {
            for (int i = mInitalPosition; i <= 100; i++) {
                if (isCancelled()) {
                    return null;
                }
                publishProgress(i);
                try {
                    TimeUnit.MILLISECONDS.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            mLastPosition = 0;
            if (getDialog() != null && getDialog().isShowing()) {
                dismiss();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (mDialog != null) {
                //mDialog.setProgress(mLastPosition = values[0]);
                messageView.setText("installtion" + values[0]);


            }
        }
    }

    private Dialog mDialog;
    private TextView titleView;
    private TextView messageView;

    private static final String KEY_INITIAL_POSITION = "initial_position";
    private int mLastPosition = 0;
    private InstallerTask mTask;

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mTask != null) {
            mTask.cancel(false);
        }
        super.onCancel(dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getSherlockActivity().getLayoutInflater();

        mDialog = new Dialog(getSherlockActivity(), R.style.Theme_LoopInfinite_DroidPHP_Dialog);
        mDialog.setContentView(inflater.inflate(R.layout.dialog_progress_holo, null));

        //Title View
        titleView = (TextView) mDialog.findViewById(R.id.title);
        titleView.setText(R.string.core_apps);

        //Message View
        messageView = (TextView) mDialog.findViewById(R.id.message);
        messageView.setText(R.string.installing_core_apps);

        return mDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mTask != null) {
            mTask.cancel(false);
        }
        super.onDismiss(dialog);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INITIAL_POSITION, mLastPosition);
    }
}