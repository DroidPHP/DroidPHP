package org.opendroidphp.app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

import org.opendroidphp.R;
import org.opendroidphp.app.adapter.FileListAdapter;
import org.opendroidphp.app.common.parser.Finder;
import org.opendroidphp.app.fragments.dialogs.EditorDialogFragment;
import org.opendroidphp.app.listeners.OnInflationListener;
import org.opendroidphp.app.tasks.FileFinderTask;

import java.io.File;
import java.util.HashMap;

public class FileFragment extends SherlockFragment implements AdapterView.OnItemClickListener, View.OnClickListener {


    private static OnInflationListener sInflateCallback;
    protected HashMap<Integer, Finder> fileMap;
    private ListView mVirtualView;
    private FileListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_file, container, false);
        final FileFinderTask task = FileFinderTask.createFor(getSherlockActivity(), new FileFinderTask.FileEvent() {

            @Override
            public void onReceived(HashMap<Integer, Finder> file) {
                fileMap = file;
                prepareView(rootView);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
        task.execute();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            sInflateCallback = (OnInflationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement OnInflateViewListener");
        }
    }

    @Override
    public void onClick(View view) {
        if (view instanceof Button) {
            sInflateCallback.setOnFragmentReceived(new ManageFileFragment());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String filePath = fileMap.get(i).find("file_location");
        File file = new File(filePath);
        DialogFragment fragment = EditorDialogFragment.create(file);
        fragment.show(getFragmentManager(), getClass().getSimpleName());
    }

    protected void prepareView(View view) {
        mVirtualView = (ListView) view.findViewById(R.id.file_list);
        mAdapter = new FileListAdapter(getSherlockActivity(), fileMap);
        mVirtualView.setAdapter(mAdapter);
        mVirtualView.setOnItemClickListener(this);
//        Button btnCreate = (Button) view.findViewById(R.id.btn_create_host);
//        btnCreate.setOnClickListener(this);
    }
}