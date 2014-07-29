package org.opendroidphp.app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.internal.widget.IcsToast;

import org.opendroidphp.R;
import org.opendroidphp.app.common.utils.Extension;
import org.opendroidphp.app.common.utils.JSONParser;
import org.opendroidphp.app.fragments.dialogs.ExtensionDownloaderDialogFragment;
import org.opendroidphp.app.fragments.dialogs.OnEventListener;
import org.opendroidphp.app.fragments.dialogs.ZipExtractDialogFragment;

import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;


public class ExtensionActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extension);
        Toast.makeText(this, "Loading extension ...", Toast.LENGTH_LONG).show();

        ListView extensionListView = (ListView) findViewById(R.id.extension_list);

        JSONParser jsonParser = new JSONParser();
        List<Extension> extensions = jsonParser.populate(Constants.REPOSITORY_URL);

        ExtensionAdapter extensionAdapter = new ExtensionAdapter(this, extensions);
        extensionListView.setAdapter(extensionAdapter);

        extensionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Extension extension = (Extension) adapterView.getItemAtPosition(i);
                //Toast.makeText(ExtensionActivity.this, extension.getName(), Toast.LENGTH_LONG).show();
                ExtensionDownloaderDialogFragment downloaderDialogFragment = new ExtensionDownloaderDialogFragment();
                downloaderDialogFragment.setOnInstallListener(new OnEventListener() {
                    @Override
                    public void onSuccess() {

                        if (!extension.getFileName().endsWith(".zip")) {
                            executeShellScripts(extension, true);


                        } else {
                            ZipExtractDialogFragment dialogFragment = new ZipExtractDialogFragment();
                            String repoUri = Constants.PROJECT_LOCATION + "/repo/" + extension.getFileName();

                            dialogFragment.setRepository(repoUri, extension.getInstallPath());
                            dialogFragment.setOnInstallListener(new OnEventListener() {
                                @Override
                                public void onSuccess() {
                                    IcsToast.makeText(ExtensionActivity.this, "Repository " + extension.getName() + " installed", Toast.LENGTH_LONG).show();
                                    executeShellScripts(extension, false);
                                }

                                @Override
                                public void onFailure() {

                                }
                            });
                            dialogFragment.show(getSupportFragmentManager(), "do_install");
                        }
                    }

                    @Override
                    public void onFailure() {

                    }
                });

                downloaderDialogFragment.setExtension(extension);
                downloaderDialogFragment.show(getSupportFragmentManager(), "do_install");
            }
        });

    }

    private void executeShellScripts(Extension extension, boolean isNative) {

        String shell = extension.getShellScript();

        final List<String> run = new ArrayList<String>();
        if (shell.contains("\n")) {
            for (String script : shell.split("\n")) {
                run.add(script);
            }
        } else {
            run.add(shell);
        }
        if (isNative) {
            run.add(String.format("%s cp %s %s",
                            Constants.BUSYBOX_SBIN_LOCATION,
                            Constants.PROJECT_LOCATION + "/repo/" + extension.getFileName(),
                            extension.getInstallPath())
            );
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                Shell.SH.run(run);
            }
        }).start();
    }

    public class ExtensionAdapter extends ArrayAdapter<Extension> {

        private List<Extension> itemList;
        private Context context;

        public ExtensionAdapter(Context ctx, List<Extension> itemList) {
            super(ctx, android.R.layout.simple_list_item_1, itemList);
            this.itemList = itemList;
            this.context = ctx;
        }

        public int getCount() {
            if (itemList != null)
                return itemList.size();
            return 0;
        }

        public Extension getItem(int position) {
            if (itemList != null)
                return itemList.get(position);
            return null;
        }

        public long getItemId(int position) {
            if (itemList != null)
                return itemList.get(position).hashCode();
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.extensions_list, null);
            }

            Extension c = itemList.get(position);
            TextView text = (TextView) v.findViewById(R.id.extension_name);
            text.setText(c.getName());

            TextView text1 = (TextView) v.findViewById(R.id.extension_summery);
            text1.setText(c.getSummery());

            return v;
        }

        public List<Extension> getItemList() {
            return itemList;
        }

        public void setItemList(List<Extension> itemList) {
            this.itemList = itemList;
        }
    }
}
