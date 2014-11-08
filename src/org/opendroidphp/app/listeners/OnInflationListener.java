package org.opendroidphp.app.listeners;


import android.support.v4.app.Fragment;

public interface OnInflationListener {

    public void setOnViewIdReceived(int idView);

    public void setOnFragmentReceived(Fragment fragment);
}
