package com.irontec.bandabeat;

import java.util.zip.Inflater;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.bandabeat.helper.Data;
import com.irontec.bandabeat.helper.Login;
import com.irontec.bandabeat.provider.PlaylistProvider;
import com.irontec.bandabeat.provider.TrackProvider;
import com.irontec.bandabeat.service.PlayerService.PlayerState;

public class ProfileActivity extends SherlockActivity {

	private ListView mList;
	private ProfileAdapter mAdapter;
	private LayoutInflater mInflater;
	private boolean exit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.profile_activity);

		mList = (ListView) findViewById(R.id.profile_list);

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mAdapter = new ProfileAdapter();

		mList.setAdapter(mAdapter);

		ActionBar actionBar = getSupportActionBar();

		BitmapDrawable background = new BitmapDrawable(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.actionbar_background_img));
		background.setTileModeX(android.graphics.Shader.TileMode.REPEAT);

		actionBar.setBackgroundDrawable(background);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		exit = true;

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
	
	public void about(View v) {
		exit = false;
		Intent i = new Intent(this, HTMLActivity.class);
		i.putExtra("fileName", "about.html");
		startActivity(i);
	}

	public void logout(View v) {
		exit = false;
		Login.logout(getApplicationContext());
		ContentResolver resolver = getContentResolver();
		resolver.delete(PlaylistProvider.CONTENT_URI, "idPlaylist != -1", null);
		resolver.delete(TrackProvider.CONTENT_URI, null, null);
		PlayerActivity.mService.cancelNotification();
		PlayerActivity.mMediaPlayer.reset();
		setResult(1);
		finish();
	}
	
	public void help(View v) {
		exit = false;
		Intent i = new Intent(this, HTMLActivity.class);
		i.putExtra("fileName", "help.html");
		startActivity(i);
	}

	private class ProfileAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {

			ProfileHolder profileHolder;

			if (v == null) {
				profileHolder = new ProfileHolder();
				v = mInflater.inflate(R.layout.profile_row, null);
				profileHolder.mDescription = (TextView) v.findViewById(R.id.profile_description);
				profileHolder.mDetail = (TextView) v.findViewById(R.id.profile_detail);
				v.setTag(profileHolder);
			} else {
				profileHolder = (ProfileHolder) v.getTag();
			}

			switch (position) {
				case 0:
					profileHolder.mDescription.setText("Erabiltzailea");
					profileHolder.mDetail.setText(Data.getInstance().getUsername());
					break;
	
				case 1:
					profileHolder.mDescription.setText("Zerrendak");
					profileHolder.mDetail.setText(String.valueOf(getPlaylistCount()));
					break;
	
				default:
					break;
			}

			return v;
		}
		
		private int getPlaylistCount() {
			
			String[] projection = {PlaylistProvider.idplaylist};
			
			ContentResolver resolver = getContentResolver();
			Cursor c = resolver.query(PlaylistProvider.CONTENT_URI, projection, null, null, null);
			int num =c.getCount();
			c.close();
			return num;
		}

	}

	static class ProfileHolder {
		TextView mDescription;
		TextView mDetail;
	}

}
