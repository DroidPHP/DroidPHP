package org.opendroidphp.app.tasks;


import android.content.Context;

import org.opendroidphp.R;
import org.opendroidphp.app.AppController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class RepoInstallerTask extends ProgressDialogTask<String, String, String> {

    public static final String INSTALL_DONE = "org.opendroidphp.repository.INSTALLED";
    public static final String INSTALL_ERROR = "org.opendroidphp.repository.INSTALL_ERROR";

    private String repositoryName;
    private String repositoryPath;

    public RepoInstallerTask(Context context) {
        super(context);
    }

    public RepoInstallerTask(Context context, String title, String message) {
        super(context, title, message);
    }

    @Override
    protected String doInBackground(String... file) {
        repositoryName = file[0];
        repositoryPath = file[1];
        boolean isSuccess = true;

        ZipInputStream zipInputStream = null;
        createDirectory("");
        try {
            if (repositoryName == null || repositoryName.equals("")) {
                zipInputStream = new ZipInputStream(getContext().getAssets().open("data.zip"));
            } else if (new File(repositoryName).exists()) {
                zipInputStream = new ZipInputStream(new FileInputStream(repositoryName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ZipEntry zipEntry;
        try {
            FileOutputStream fout;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.isDirectory()) {
                    createDirectory(zipEntry.getName());
                } else {
                    fout = new FileOutputStream(repositoryPath + "/" + zipEntry.getName());
                    publishProgress(zipEntry.getName());
                    byte[] buffer = new byte[4096 * 10];
                    int length;
                    while ((length = zipInputStream.read(buffer)) != -1) {
                        fout.write(buffer, 0, length);
                    }
                    zipInputStream.closeEntry();
                    fout.close();
                }
            }
            zipInputStream.close();
        } catch (Exception e) {
            isSuccess = false;
            e.printStackTrace();
        }
        publishProgress(isSuccess ? INSTALL_DONE : INSTALL_ERROR);
        dismissProgress();
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        setMessage(values[0]);
        if (values[0].equals(INSTALL_DONE) || values[0].equals(INSTALL_ERROR)) {
            int resId = values[0].equals(INSTALL_DONE) ? R.string.core_apps_installed :
                    R.string.core_apps_not_installed;
            AppController.toast(getContext(), getContext().getString(resId));
        }
    }

    /**
     * Responsible for creating directory inside application's data directory
     *
     * @param dirName Directory to create during extracting
     */
    protected void createDirectory(String dirName) {
        File file = new File(repositoryPath + dirName);
        if (!file.isDirectory()) file.mkdirs();
    }
}
