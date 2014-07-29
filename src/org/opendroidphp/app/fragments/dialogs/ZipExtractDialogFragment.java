package org.opendroidphp.app.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

import org.opendroidphp.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractDialogFragment extends SherlockDialogFragment {


    protected static OnEventListener listener;


    private static String repoFilename;
    private static String repoExtract;
    private Dialog mDialog;
    private TextView titleView;
    private TextView messageView;
    private InstallerTask mTask;

    public void setRepository(String repoFilename, String repoExtract) {
        this.repoFilename = repoFilename;
        this.repoExtract = repoExtract;
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
        titleView.setText(R.string.core_apps);

        //Message View
        messageView = (TextView) mDialog.findViewById(R.id.message);
        messageView.setText(R.string.installing_core_apps);


        mTask = new InstallerTask();
        mTask.execute();

        return mDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mTask != null) {
            mTask.cancel(false);
        }
        super.onDismiss(dialog);
    }

    private final class InstallerTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Boolean isInstalled = true;

            ZipInputStream zipInputStream = null;
            createDirectory("");

            try {
                if (repoFilename.equals("") || repoFilename == null) {
                    zipInputStream = new ZipInputStream(
                            getSherlockActivity().getAssets().open("data.zip")
                    );
                } else if (new File(repoFilename).exists()) {
                    zipInputStream = new ZipInputStream(
                            new FileInputStream(repoFilename)
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ZipEntry zipEntry = null;

            try {

                while ((zipEntry = zipInputStream.getNextEntry()) != null) {

                    if (zipEntry.isDirectory()) {

                        createDirectory(zipEntry.getName());

                    } else {

                        FileOutputStream fout = null;

                        fout = new FileOutputStream(
                                repoExtract + zipEntry.getName()
                        );
                        publishProgress(zipEntry.getName());

                        byte[] buffer = new byte[4096 * 10];
                        int length = 0;

                        while ((length = zipInputStream.read(buffer)) != -1) {
                            fout.write(buffer, 0, length);
                        }

                        zipInputStream.closeEntry();
                        fout.close();
                    }


                }
                zipInputStream.close();
            } catch (Exception e) {
                isInstalled = false;

            }

            if (isInstalled) {
                publishProgress("DONE");
            } else {
                publishProgress("ERROR");
            }

            if (getDialog() != null && getDialog().isShowing()) {
                dismiss();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (mDialog != null) {

                messageView.setText("EXTRACTING: " + values[0]);

                if (values[0].equals("DONE")) {

                    if (listener != null) {
                        listener.onSuccess();
                    }
                }
                if (values[0].equals("ERROR")) {

                    if (listener != null) {
                        listener.onFailure();
                    }
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        /**
         * Responsible for creating directory inside application's data directory
         *
         * @param dirName
         */
        protected void createDirectory(String dirName) {
            File file = new File(repoExtract + dirName);
            if (!file.isDirectory()) file.mkdirs();
        }
    }
}