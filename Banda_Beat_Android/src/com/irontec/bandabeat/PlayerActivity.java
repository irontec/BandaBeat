package com.irontec.bandabeat;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.irontec.bandabeat.service.PlayerService;
import com.irontec.bandabeat.service.PlayerService.PlayerBinder;
import com.irontec.bandabeat.service.PlayerService.PlayerState;

public abstract class PlayerActivity extends SherlockFragmentActivity {

	public static boolean mBound = false;
	public static PlayerService mService;
	public static MediaPlayer mMediaPlayer;
	
	public boolean exit;

	public ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			PlayerBinder binder = (PlayerBinder) service;
			mService = binder.getService();
			mMediaPlayer = binder.getMediaPlayer();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = new Intent(this, PlayerService.class);
		intent.setAction(PlayerService.ACTION_PLAY);
		
		if (!isMyServiceRunning() || mService == null) {
			startService(intent);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		} else {
			//mMediaPlayer = mService.getMediPlayer();
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		exit = true;
		
		if (mService != null && mService.mState == PlayerState.PLAY) {
			mService.cancelNotification();
		}
	}

	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (PlayerService.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}
}
