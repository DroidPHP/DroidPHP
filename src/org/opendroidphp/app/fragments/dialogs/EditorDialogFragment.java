package org.opendroidphp.app.fragments.dialogs;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;

import org.apache.commons.io.FileUtils;
import org.opendroidphp.R;

import java.io.File;
import java.io.IOException;


public class EditorDialogFragment extends SherlockDialogFragment {

    private Dialog dialog;
    private DialogClickEvent event;

    private EditText etSource;

    private String source;
    private File file;

    public static EditorDialogFragment create(final String source) {
        EditorDialogFragment fragment = new EditorDialogFragment();
        fragment.setSource(source);
        return fragment;
    }

    public static EditorDialogFragment create(final File file) {
        EditorDialogFragment fragment = new EditorDialogFragment();
        fragment.setFile(file);
        return fragment;
    }

    public static EditorDialogFragment create(final String source, final DialogClickEvent event) {
        EditorDialogFragment fragment = new EditorDialogFragment();
        fragment.setSource(source);
        fragment.addClickEventListener(event);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getSherlockActivity());
//        if ((source == null) || (file == null)) {
//            throw new NullPointerException("source or file cannot be empty");
//        }
        dialog = new Dialog(getSherlockActivity(), R.style.Theme_DroidPHP_Dialog);
        dialog.setContentView(inflater.inflate(R.layout.dialog_editor, null));
        etSource = (EditText) dialog.findViewById(R.id.content);

        if (source != null)
            etSource.setText(source);
        else new FileLoaderTask().execute();
        //Buttons and events listener
        Button btnNotConfirm = (Button) dialog.findViewById(R.id.negative);
        btnNotConfirm.setText(R.string.confirm_exit);

        Button btnConfirm = (Button) dialog.findViewById(R.id.positive);
        btnConfirm.setText(R.string.confirm);

        btnNotConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (event != null) event.onConfirmExit();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileSaveTask task = new FileSaveTask();
                task.execute(etSource.getText().toString());
                dismiss();
                if (event != null) event.onConfirm();
            }
        });

        return dialog;
    }

    protected EditorDialogFragment setFile(final File file) {
        this.file = file;
        return this;
    }

    public String getSource() {
        return source;
    }

    public EditorDialogFragment setSource(final String source) {
        this.source = source;
        return this;
    }

    public EditorDialogFragment addClickEventListener(DialogClickEvent event) {
        this.event = event;
        return this;
    }

    public interface DialogClickEvent {
        public void onConfirm();

        public void onConfirmExit();
    }

    private class FileLoaderTask extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String source = FileUtils.readFileToString(file, "UTF-8");
                setSource(source);
                publishProgress(source);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... sourceCode) {
            super.onProgressUpdate(sourceCode);
            if (etSource != null)
                etSource.setText(sourceCode[0]);
        }
    }

    private class FileSaveTask extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... sourceCode) {
            try {
                FileUtils.writeStringToFile(file, sourceCode[0], "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
