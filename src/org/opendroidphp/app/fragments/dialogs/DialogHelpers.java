package org.opendroidphp.app.fragments.dialogs;


import android.content.Context;
import android.support.v4.app.DialogFragment;

import org.opendroidphp.R;
import org.opendroidphp.app.Constants;
import org.opendroidphp.app.tasks.RepoInstallerTask;

public class DialogHelpers {

    public static DialogFragment factoryForInstall(Context context) {
        ConfirmDialogFragment dialog = new ConfirmDialogFragment().
                create(context, R.string.core_apps,
                        R.string.core_apps_not_installed).
                addClickEventListener(new DialogEventListener(context));
        dialog.setCancelable(false);
        //dialog.show(context, context.getClass().getSimpleName());
        return dialog;
    }

    private static class DialogEventListener implements ConfirmDialogFragment.DialogClickEvent {

        private Context context;

        public DialogEventListener(Context context) {
            this.context = context;
        }

        @Override
        public void onConfirm() {
            RepoInstallerTask task = new RepoInstallerTask(context);
            task.execute("", Constants.INTERNAL_LOCATION.concat("/"));
        }

        @Override
        public void onConfirmExit() {
            System.exit(0);
        }
    }
}
