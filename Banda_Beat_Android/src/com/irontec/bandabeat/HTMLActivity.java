package com.irontec.bandabeat;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.webkit.WebView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.bandabeat.service.PlayerService.PlayerState;

public class HTMLActivity extends SherlockActivity {
	
	private boolean exit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String fileName = getIntent().getStringExtra("fileName");
		
		WebView webView = new WebView(this);
		
		webView.loadUrl("file:///android_asset/"+fileName);
		
		ActionBar actionBar = getSupportActionBar();

		BitmapDrawable background = new BitmapDrawable(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.actionbar_background_img));
		background.setTileModeX(android.graphics.Shader.TileMode.REPEAT);

		actionBar.setBackgroundDrawable(background);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		exit = true;
		
		EasyTracker.getTracker().trackEvent("Help", "Help", fileName, (long) 1);
		
		setContentView(webView);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case android.R.id.home:
			exit = false;
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {
		exit = false;
		super.onBackPressed();
	}

	protected void onPause() {
		super.onPause();
		if (PlayerActivity.mService != null) {
			if (PlayerActivity.mService.mState == PlayerState.PLAY && exit) {
				PlayerActivity.mService.enterForeground();
			}
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}
}
