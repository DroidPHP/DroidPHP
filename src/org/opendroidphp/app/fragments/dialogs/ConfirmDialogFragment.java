package org.opendroidphp.app.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

import org.opendroidphp.R;


public class ConfirmDialogFragment extends SherlockDialogFragment {

    private Dialog dialog;
    private DialogClickEvent event;

    private TextView tv_title;
    private TextView tv_message;

    private String title;
    private String message;

    public ConfirmDialogFragment create(String title, String message) {
        this.title = title;
        this.message = message;
        return this;
    }

    public ConfirmDialogFragment create(String title, String message, DialogClickEvent event) {
        create(title, message);
        addClickEventListener(event);
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getSherlockActivity());
        if ((title == null) || (message == null)) {
            throw new NullPointerException("title and message cannot be empty");
        }
        dialog = new Dialog(getSherlockActivity(), R.style.Theme_DroidPHP_Dialog);
        dialog.setContentView(inflater.inflate(R.layout.dialog_holo, null));
        //Title View
        tv_title = (TextView) dialog.findViewById(R.id.title);
        tv_title.setText(title);
        //Message View
        tv_message = (TextView) dialog.findViewById(R.id.message);
        tv_message.setText(message);
        //Buttons and events listener
        Button btnNotConfirm = (Button) dialog.findViewById(R.id.negative);
        btnNotConfirm.setText(R.string.confirm_exit);

        Button btnConfirm = (Button) dialog.findViewById(R.id.positive);
        btnConfirm.setText(R.string.confirm);

        if (event != null) {
            btnNotConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    event.onConfirmExit();
                }
            });
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    event.onConfirm();
                }
            });
        }
        return dialog;
    }

    public ConfirmDialogFragment addClickEventListener(DialogClickEvent event) {
        this.event = event;
        return this;
    }

    public interface DialogClickEvent {
        public void onConfirm();

        public void onConfirmExit();
    }
}
