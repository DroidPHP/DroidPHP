package org.opendroidphp.app.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opendroidphp.R;
import org.opendroidphp.app.AppController;
import org.opendroidphp.app.Constants;
import org.opendroidphp.app.adapter.ExtensionListAdapter;
import org.opendroidphp.app.fragments.dialogs.ExtensionDownloaderDialogFragment;
import org.opendroidphp.app.fragments.dialogs.OnEventListener;
import org.opendroidphp.app.fragments.dialogs.ZipExtractDialogFragment;
import org.opendroidphp.app.model.ExtensionItem;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;


public class ExtensionFragment extends SherlockFragment implements AdapterView.OnItemClickListener {

    ListView mExtensionList;

    private ArrayList<ExtensionItem> extensionItems = new ArrayList<ExtensionItem>();

    private ExtensionListAdapter extensionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_extension, container, false);
        prepareView(rootView);
        requestJsonRepository();
        return rootView;
    }


    private void requestJsonRepository() {
        JsonArrayRequest extensionReq = new JsonArrayRequest(Constants.REPOSITORY_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(getClass().getSimpleName(), response.toString());
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);

                                ExtensionItem extensionItem = new ExtensionItem().
                                        setName(obj.getString("repoName")).
                                        setFileName(obj.getString("fileName")).
                                        setSummery(obj.getString("repoDescription")).
                                        setShellScript(obj.getString("shellScript")).
                                        setInstallPath(obj.getString("installPath")).
                                        setDownloadUrl(obj.getString("downloadUrl"));

                                // adding to extension list
                                extensionItems.add(extensionItem);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        extensionAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(getClass().getSimpleName(), "Error: " + error.getMessage());
            }
        }
        );
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(extensionReq);
    }

    protected void prepareView(View view) {
        mExtensionList = (ListView) view.findViewById(R.id.extension_list);
        extensionAdapter = new ExtensionListAdapter(getSherlockActivity(), extensionItems);
        mExtensionList.setAdapter(extensionAdapter);
        mExtensionList.setOnItemClickListener(this);
    }

    private void executeShellScripts(ExtensionItem extension) {

        String shell = extension.getShellScript();

        final List<String> run = new ArrayList<String>();
        if (shell.contains("\n")) {
            Collections.addAll(run, shell.split("\n"));
        } else {
            run.add(shell);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Shell.SH.run(run);
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        final ExtensionItem extension = (ExtensionItem) adapterView.getItemAtPosition(i);
        ExtensionDownloaderDialogFragment downloaderDialogFragment = new ExtensionDownloaderDialogFragment();
        downloaderDialogFragment.setOnInstallListener(new OnEventListener() {
            @Override
            public void onSuccess() {

                if (extension.getFileName().endsWith(".zip")) {
                    ZipExtractDialogFragment dialogFragment = new ZipExtractDialogFragment();
                    String repoUri = Constants.PROJECT_LOCATION + "/repo/" + extension.getFileName();

                    dialogFragment.setRepository(repoUri, extension.getInstallPath());
                    dialogFragment.setOnInstallListener(new OnEventListener() {
                        @Override
                        public void onSuccess() {
                            AppController.
                                    toast(getSherlockActivity(), MessageFormat.
                                            format("Repository {0} installed", extension.getName()));
                            executeShellScripts(extension);
                        }

                        @Override
                        public void onFailure() {

                        }
                    });
                    dialogFragment.show(getFragmentManager(), getClass().getSimpleName());
                } else {
                }
            }

            @Override
            public void onFailure() {

            }
        });

        downloaderDialogFragment.setExtension(extension);
        downloaderDialogFragment.show(getFragmentManager(), getClass().getSimpleName());
    }
}
