package org.opendroidphp.app.tasks;


import android.content.Context;

import org.apache.commons.io.FileUtils;
import org.opendroidphp.R;
import org.opendroidphp.app.Constants;
import org.opendroidphp.app.common.parser.ConfigParser;
import org.opendroidphp.app.common.parser.Finder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class FileFinderTask extends ProgressDialogTask<Void, HashMap<Integer, Finder>, Void> {

    private File file;
    private String[] extension;
    private FileEvent fileEvent;

    public FileFinderTask() {

    }

    public FileFinderTask(Context context) {
        super(context);
    }

    public FileFinderTask(Context context, String title, String message) {
        super(context, title, message);
    }

    public FileFinderTask(Context context, int titleResId, int messageResId) {
        super(context, context.getString(titleResId), context.getString(messageResId));
    }

    public static FileFinderTask createFor(final Context c, final FileEvent event) {
        FileFinderTask task = new FileFinderTask(c, R.string.finder_title, R.string.finder_message);
        task.addFile(new File(Constants.PROJECT_LOCATION + "/hosts"));
        task.addExtensions(new String[]{"conf"});
        task.addEvent(event);
        return task;
    }

    @Override
    protected Void doInBackground(Void... list) {
        Collection<File> listFiles = FileUtils.listFiles(file, extension, true);
        int i = 0;
        HashMap<Integer, Finder> map = new HashMap<Integer, Finder>();
        for (Iterator<File> iterator = listFiles.iterator(); iterator.hasNext(); i++) {
            File file = iterator.next();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                Finder finder = new Finder(ConfigParser.factory(reader).getKeyValue());
                finder.setKeyValue("file_name", file.getName());
                finder.setKeyValue("file_location", file.getPath());
                map.put(i, finder);
            } catch (FileNotFoundException e) {
                fileEvent.onError(e);
            }
        }
        publishProgress(map);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void s) {
        super.onPostExecute(s);
        dismissProgress();
    }

    @Override
    protected void onProgressUpdate(HashMap<Integer, Finder>... queryMap) {
        super.onProgressUpdate(queryMap);
        if (fileEvent != null) fileEvent.onReceived(queryMap[0]);
    }

    public FileFinderTask addFile(final File file) {
        this.file = file;
        return this;
    }

    public FileFinderTask addEvent(final FileEvent fileEvent) {
        this.fileEvent = fileEvent;
        return this;
    }

    public FileFinderTask addExtensions(final String[] listExtension) {
        this.extension = listExtension;
        return this;
    }

    public interface FileEvent {

        public void onReceived(HashMap<Integer, Finder> file);

        public void onError(Exception e);
    }
}