package org.opendroidphp.app.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.opendroidphp.R;
import org.opendroidphp.app.Constants;
import org.opendroidphp.app.common.utils.FilenameUtils;
import org.opendroidphp.app.model.Repository;
import org.opendroidphp.app.tasks.RepoDownloaderTask;
import org.opendroidphp.app.tasks.RepoInstallerTask;

import java.util.ArrayList;

public class RepositoryListAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private ArrayList<Repository> repositories;

    public RepositoryListAdapter(Context context, ArrayList<Repository> repositories) {
        this.context = context;
        this.repositories = repositories;
    }

    @Override
    public int getCount() {
        return repositories.size();
    }

    @Override
    public Repository getItem(int position) {
        return repositories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.repository_list_item, null);
        }

        Repository repo = repositories.get(position);
        TextView repositoryName = (TextView) convertView.findViewById(R.id.txt_repository_name);
        repositoryName.setText(repo.getName());

        TextView tv_filesize = (TextView) convertView.findViewById(R.id.txt_repository_size);
        tv_filesize.setText(String.format("SIZE : %s", Formatter.formatFileSize(context, repo.getSize())));

        TextView tv_copyto = (TextView) convertView.findViewById(R.id.txt_repository_copyto);
        tv_copyto.setText(String.format("INSTALL : %s", repo.getCopyPath()));

        convertView.findViewById(R.id.repository_ll).setOnClickListener(
                new DownloadEventListener(position));

        return convertView;
    }

    @Override
    public void onClick(View view) {

    }

    private class DownloadEventListener implements View.OnClickListener {
        private int repoId;

        public DownloadEventListener(int repoId) {
            this.repoId = repoId;
        }

        @Override
        public void onClick(View view) {
            Repository repository = getItem(repoId);
            if (!repository.getUrl().contains("zip")) {
                return;
            }
            String repoFilename = Constants.PROJECT_LOCATION + "/packages/" + FilenameUtils.
                    getFilename(repository.getUrl());
            String repoPath = repository.getCopyPath() + "/";
            RepoDownloaderTask task = new RepoDownloaderTask(context, "Downloading ...",
                    "Please wait while we are downloading..", repository);
            task.addDownloadListener(new DownloadResultListener(repoFilename, repoPath));
            task.execute();
        }
    }

    private class DownloadResultListener implements RepoDownloaderTask.DownloadListener {
        private String repoFilename;
        private String repoPath;

        public DownloadResultListener(String repoFilename, String repoPath) {
            this.repoFilename = repoFilename;
            this.repoPath = repoPath;
        }

        @Override
        public void onDownloaded() {
            new RepoInstallerTask(context).execute(repoFilename, repoPath);
        }

        @Override
        public void onDownloadError(String error) {

        }

        @Override
        public void onDownloadError() {

        }
    }
}

