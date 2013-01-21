package com.irontec.bandabeat;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.bandabeat.service.PlayerService.PlayerState;


public class PublicTrackActivity extends SherlockFragmentActivity {

	public boolean exit;
	public int idPlaylist;
	private String playlistTitle;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       
       idPlaylist = getIntent().getIntExtra("idPlaylist", 1);
       playlistTitle = getIntent().getStringExtra("playlistTitle");
       
       ActionBar actionBar = getSupportActionBar();

		BitmapDrawable background = new BitmapDrawable(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.actionbar_background_img));
		background.setTileModeX(android.graphics.Shader.TileMode.REPEAT);
		
		actionBar.setBackgroundDrawable(background);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(playlistTitle);
       
       setContentView(R.layout.track_public_activity);
	}
	
	@Override
	protected void onResume() {
		super.onResume();	
		exit = true;
		if (PlayerActivity.mService != null && PlayerActivity.mService.mState == PlayerState.PLAY) {
			PlayerActivity.mService.cancelNotification();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (PlayerActivity.mService.mState == PlayerState.PLAY && exit) {
			PlayerActivity.mService.enterForeground();
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
	
	@Override
	public void onBackPressed() {
		exit = false;
		super.onBackPressed();
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
}
