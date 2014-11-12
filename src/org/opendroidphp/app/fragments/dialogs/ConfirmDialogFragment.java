package org.opendroidphp.app.fragments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

import org.opendroidphp.R;


public class ConfirmDialogFragment extends SherlockDialogFragment {

    private static Context sContext;
    private Dialog dialog;
    private DialogClickEvent event;
    private TextView tv_title;
    private TextView tv_message;
    private String title;
    private String message;

    public static ConfirmDialogFragment create(Context c, final String title, final String message) {
        sContext = c;
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        fragment.setTitle(title);
        fragment.setMessage(message);
        return fragment;
    }

    public static ConfirmDialogFragment create(Context c, final String title, final String message, final DialogClickEvent event) {
        sContext = c;
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        fragment.setTitle(title);
        fragment.setMessage(message);
        fragment.addClickEventListener(event);
        return fragment;
    }

    public static ConfirmDialogFragment create(Context c, final int titleResId, final int messageResId) {
        sContext = c;
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        fragment.setTitle(titleResId);
        fragment.setMessage(messageResId);
        //fragment.addClickEventListener(event);
        return fragment;
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
                dismiss();
                if (event != null) event.onConfirm();
            }
        });

        return dialog;
    }

    public ConfirmDialogFragment addClickEventListener(DialogClickEvent event) {
        this.event = event;
        return this;
    }

    public ConfirmDialogFragment setTitle(final String title) {
        this.title = title;
        return this;
    }

    public ConfirmDialogFragment setMessage(final String message) {
        this.message = message;
        return this;
    }

    public ConfirmDialogFragment setTitle(final int titleResId) {
        this.title = sContext.getString(titleResId);
        return this;
    }

    public ConfirmDialogFragment setMessage(final int messageResId) {
        this.message = sContext.getString(messageResId);
        return this;
    }

    public interface DialogClickEvent {
        public void onConfirm();

        public void onConfirmExit();
    }
}
