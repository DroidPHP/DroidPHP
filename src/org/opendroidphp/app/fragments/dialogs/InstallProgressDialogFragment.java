package org.opendroidphp.app.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.internal.widget.IcsToast;

import org.opendroidphp.app.Constants;
import org.opendroidphp.app.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class InstallProgressDialogFragment extends SherlockDialogFragment {

    private static String NATIVE_DIRECTORY;
    private static String EXTERNAL_REPOSITORY;

    private final class InstallerTask extends AsyncTask<Void, String, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            Boolean isInstalled = true;

            ZipInputStream zipInputStream = null;
            createDirectory("");

            try {
                //update from external repository (from `/mnt/sdcard/droidphp/repository/update.zip`)

                if (new File(EXTERNAL_REPOSITORY).exists()) {

                    zipInputStream = new ZipInputStream(
                            new FileInputStream(EXTERNAL_REPOSITORY)
                    );

                } else {
                    //use internal repository
                    zipInputStream = new ZipInputStream(
                            getSherlockActivity().getAssets().open("data.zip")
                    );
                }
            } catch (Exception e) {

            }
            ZipEntry zipEntry = null;

            try {

                while ((zipEntry = zipInputStream.getNextEntry()) != null) {

                    if (zipEntry.isDirectory()) {

                        createDirectory(zipEntry.getName());

                    } else {

                        FileOutputStream fout = null;

                        fout = new FileOutputStream(
                                NATIVE_DIRECTORY + zipEntry.getName()
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

                    IcsToast.makeText(
                            getSherlockActivity(), getString(R.string.core_apps_installed), Toast.LENGTH_LONG)
                            .show();

                }

                if (values[0].equals("ERROR")) {
                    IcsToast.makeText(
                            getSherlockActivity(), getString(R.string.install_failed), Toast.LENGTH_LONG)
                            .show();

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

            File file = new File(NATIVE_DIRECTORY + dirName);

            if (!file.isDirectory()) file.mkdirs();
        }

    }

    private Dialog mDialog;
    private TextView titleView;
    private TextView messageView;

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

        try {
            NATIVE_DIRECTORY = getSherlockActivity().getApplicationInfo().dataDir + "/";
        } catch (NullPointerException e) {

        }

        EXTERNAL_REPOSITORY = Environment.
                getExternalStorageDirectory().getPath() + "/" + Constants.UPDATE_FROM_EXTERNAL_REPOSITORY;

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
}