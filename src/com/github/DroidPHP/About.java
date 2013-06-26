/**
 * This file is part of DroidPHP
 *
 * (c) 2013 Shushant Kumar
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.github.DroidPHP;

import android.os.Bundle;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;

public class About extends SherlockActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutus);

		// Load partially transparent black background
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.ab_bg_black));

		WebView wView = (WebView) findViewById(R.id.about_us);
		wView.loadUrl("file:///android_asset/about.html");
	}
}