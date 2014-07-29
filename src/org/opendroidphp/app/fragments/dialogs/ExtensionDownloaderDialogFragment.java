package org.opendroidphp.app.fragments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

import org.opendroidphp.R;
import org.opendroidphp.app.Constants;
import org.opendroidphp.app.common.utils.Extension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExtensionDownloaderDialogFragment extends SherlockDialogFragment {

    protected static OnEventListener listener;
    protected static Extension mExtension;
    private Dialog mDialog;
    private TextView titleView;
    private TextView messageView;
    private DownloadTask mTask;

    public void setExtension(Extension extension) {
        mExtension = extension;
    }

    public void setOnInstallListener(OnEventListener onInstallListener) {
        listener = onInstallListener;
    }

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

        mDialog = new Dialog(getSherlockActivity(), R.style.Theme_DroidPHP_Dialog);
        mDialog.setContentView(inflater.inflate(R.layout.dialog_progress_holo, null));

        //Title View
        titleView = (TextView) mDialog.findViewById(R.id.title);
        titleView.setText("Downloading ...");
        //Message View
        messageView = (TextView) mDialog.findViewById(R.id.message);
        messageView.setText("Please wait while we are downloading..");

        mTask = new DownloadTask(getSherlockActivity());
        mTask.execute(mExtension.getDownloadUrl());
        return mDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mTask != null) {
            mTask.cancel(false);
        }
        super.onDismiss(dialog);
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... repoUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(repoUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                File repo = new File(Constants.PROJECT_LOCATION + "/repo");
                if (!repo.exists()) {
                    repo.mkdirs();
                }
                File fileName = new File(Constants.PROJECT_LOCATION + "/repo/" + mExtension.getFileName());
                if (fileName.exists()) {
                    return null;
                }
                output = new FileOutputStream(Constants.PROJECT_LOCATION + "/repo/" + mExtension.getFileName());

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            messageView.setText(String.format("Downloading [%s] completed", progress[0] + "%"));
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mDialog.cancel();

            //String message = null;
            if (result != null) {
                // message = "Download error: " + result;
                if (listener != null) listener.onFailure();
            } else {
                if (listener != null) listener.onSuccess();
            }
            // IcsToast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
}