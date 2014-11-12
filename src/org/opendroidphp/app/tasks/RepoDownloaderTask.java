package org.opendroidphp.app.tasks;

import android.content.Context;
import android.os.PowerManager;

import org.opendroidphp.app.Constants;
import org.opendroidphp.app.common.utils.FilenameUtils;
import org.opendroidphp.app.model.Repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RepoDownloaderTask extends ProgressDialogTask<String, String, String> {

    public static final String DOWNLOAD_COMPLETED = "org.opendroidphp.package.DOWNLOADED";
    public static final String DOWNLOAD_ERROR = "org.opendroidphp.package.ERROR";
    public static final String DOWNLOAD_CANCEL = "org.opendroidphp.package.CANCEL";
    public static final String DOWNLOAD_EXIST = "org.opendroidphp.package.EXIST";
    private Repository repository;
    private DownloadListener listener;
    private PowerManager.WakeLock mWakeLock;

    public RepoDownloaderTask(Context context) {
        super(context);
    }

    public RepoDownloaderTask(Context context, String title, String message, Repository repository) {
        super(context, title, message);
        this.repository = repository;
    }

    public RepoDownloaderTask addDownloadListener(DownloadListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if ((repository == null) && (listener == null)) {
            throw new NullPointerException("Repository and DownloadListener cannot be null");
        }
        PowerManager pm = (PowerManager) getContext().
                getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
    }

    @Override
    protected String doInBackground(String... repositoryUrl) {

        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(repository.getUrl());
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
            File repo = new File(Constants.PROJECT_LOCATION + "/packages");

            if (!repo.exists()) repo.mkdirs();

            String repoFilename = repo.getPath() + "/" + FilenameUtils.getFilename(repository.getUrl());

            File fileName = new File(repoFilename);
            if (fileName.exists())
                return DOWNLOAD_EXIST;

            output = new FileOutputStream(repoFilename);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button and also delete unfinished repository
                if (isCancelled()) {
                    input.close();
                    fileName.delete();
                    return DOWNLOAD_CANCEL;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((total * 100 / fileLength) + "%");
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
        return DOWNLOAD_COMPLETED;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        super.onProgressUpdate(progress);
        setMessage(String.format("Downloading [%s] completed", progress[0]));
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        dismissProgress();
        if (result.equals(DOWNLOAD_COMPLETED) || result.equals(DOWNLOAD_EXIST))
            listener.onDownloaded();
        else if (result.equals(DOWNLOAD_ERROR))
            listener.onDownloadError();
        else listener.onDownloadError(result);
    }

    public interface DownloadListener {
        public void onDownloaded();

        public void onDownloadError(String error);

        public void onDownloadError();
    }
}
