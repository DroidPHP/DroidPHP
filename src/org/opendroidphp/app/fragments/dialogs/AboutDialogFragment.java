package org.opendroidphp.app.fragments.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

import org.opendroidphp.app.R;

/**
 * Created by shushant on 3/19/14.
 */
public class AboutDialogFragment extends SherlockDialogFragment {


    private Dialog mDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getSherlockActivity().getLayoutInflater();

        mDialog = new Dialog(getSherlockActivity(), R.style.Theme_DroidPHP_Dialog);
        mDialog.setContentView(inflater.inflate(R.layout.dialog_holo, null));

        //Title View
        TextView titleView = (TextView) mDialog.findViewById(R.id.title);
        titleView.setText(R.string.core_apps);

        //Message View
        TextView messageView = (TextView) mDialog.findViewById(R.id.message);
        messageView.setText(R.string.about_app);

        //Buttons and events listener
        Button negativeButton = (Button) mDialog.findViewById(R.id.negative);
        negativeButton.setText(R.string.close);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.cancel();
            }
        });

        Button positiveButton = (Button) mDialog.findViewById(R.id.positive);

        positiveButton.setText((CharSequence) "GitHub");
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDialog.cancel();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/droidphp"));
                intent = Intent.createChooser(intent, "Choose browser");

                if (intent != null) {
                    startActivity(intent);
                }
            }
        });

        return mDialog;
    }
}