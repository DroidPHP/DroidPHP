package org.opendroidphp.app.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.opendroidphp.R;
import org.opendroidphp.app.AppController;
import org.opendroidphp.app.listeners.OnInflationListener;


public class ConsoleActivity extends SherlockFragmentActivity implements OnInflationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            inflateFragment(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            case R.id.web_admin:
                startActivity(new Intent(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://localhost:8080"))));
                return true;
            case R.id.sql_admin:
                startActivity(new Intent(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://localhost:10000"))));
                return true;
            case R.id.settings:
                startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setOnViewIdReceived(int idView) {
        inflateFragment(idView);
    }

    @Override
    public void setOnFragmentReceived(Fragment fragment) {
        inflateFragment(fragment);
    }

    protected Fragment getFragmentById(final int fragmentCode) {

        switch (fragmentCode) {
            case 0:
                return new HomeFragment();
            case R.id.ll_mysql_shell:
                return new QueryFragment();
            case R.id.ll_package:
                return new PackageFragment();
            case R.id.ll_vhost:
                return new FileFragment();
//            case R.id.ll_update:
//                return null;
            case R.id.ll_about:
                return new AboutFragment();
            default:
                return null;
        }
    }

    protected void inflateFragment(int fragmentId) {
        inflateFragment(getFragmentById(fragmentId));
    }

    protected void inflateFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.
                    beginTransaction().
                    replace(R.id.frame_container, fragment).
                    setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out).
                    addToBackStack(getClass().getCanonicalName()).
                    commit();
            fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    if (getSupportFragmentManager().getBackStackEntryCount() == 0) finish();
                }
            });
        } else {
            AppController.toast(this, "Unable to inflate fragment");
        }
    }
}
