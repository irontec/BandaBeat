package com.irontec.bandabeat.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.irontec.bandabeat.LoginActivity;
import com.irontec.bandabeat.MainActivity;
import com.irontec.bandabeat.R;
import com.irontec.bandabeat.db.Track;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class PlayerService extends Service implements
		MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
		OnCompletionListener {

	public enum PlayerState {
		PLAY, STOP, PAUSE
	}

	public static final String ACTION_PLAY = "com.irontec.action.PLAY";
	private static final int NOTIFICATION_ID = 1234;

	private Intent mIntent;
	private MediaPlayer mMediaPlayer = null;
	public IPlayer mPlayer = null;
	public PlayerState mState;
	private boolean foreground = false;
	private final IBinder mBinder = new PlayerBinder();

	public Track currentTrack;
	public ArrayList<Track> trackList;
	public int nextTrackPosition;
	public int maxTrackCount;

	public class PlayerBinder extends Binder {
		public PlayerService getService() {
			return PlayerService.this;
		}

		public MediaPlayer getMediaPlayer() {
			/*
			 * if (mMediaPlayer == null) { initMediaPlayer(); }
			 */
			return mMediaPlayer;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mIntent = intent;
		//if (mIntent.getAction().equals(ACTION_PLAY)) {
			mState = PlayerState.STOP;
			initMediaPlayer();
		//}
		return START_STICKY;
	}

	private void initMediaPlayer() {
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnErrorListener(this);
		mMediaPlayer.setOnCompletionListener(this);
	}

	public MediaPlayer getMediPlayer() {
		/*
		 * if (mMediaPlayer == null) { initMediaPlayer(); }
		 */
		return mMediaPlayer;
	}

	@Override
	public boolean onError(MediaPlayer player, int what, int extra) {
		player.reset();
		return false;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	private Notification createNotification() {

		File file = ImageLoader.getInstance().getDiscCache()
				.get(currentTrack.getImageProfile());
		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(currentTrack.getTitulo())
				.setContentText(currentTrack.getAlbum()).setLargeIcon(bitmap);

		
		Intent resultIntent = new Intent(this, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(LoginActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		resultIntent.setAction("com.irontec.action.PLAYER");
		mBuilder.setContentIntent(resultPendingIntent);

		return mBuilder.build();
	}

	public void showNotification() {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotificationManager.notify(NOTIFICATION_ID, createNotification());
	}

	public void cancelNotification() {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotificationManager.cancel(NOTIFICATION_ID);
		stopForeground(true);
		foreground = false;
	}

	public void enterForeground() {
		foreground = true;
		startForeground(NOTIFICATION_ID, createNotification());
	}

	@Override
	public void onPrepared(MediaPlayer player) {
		nextTrackPosition++;
	
		mState = PlayerState.PLAY;
		mMediaPlayer.start();
		
		if (foreground)
			showNotification();
		
		if (mPlayer != null)
			mPlayer.prepared();
		
		if (nextTrackPosition == trackList.size()) {
			if (mPlayer != null)
				mPlayer.end();
		}
		
	}

	@Override
	public void onCompletion(MediaPlayer player) {
		if (mPlayer != null)
			mPlayer.block();
		//mMediaPlayer.stop();
		mMediaPlayer.reset();
		forward();
	}

	private void playSong() {

		try {
			mMediaPlayer.setDataSource(currentTrack.getUrl());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mMediaPlayer.prepareAsync();
	}

	public void playNextSong() {

		if (mPlayer != null)
			mPlayer.block();
		
		currentTrack = trackList.get(nextTrackPosition);
			
		/*if (mState == PlayerState.PLAY_PAUSE) {
			mMediaPlayer.reset();
			playSong();
		} else {
			playSong();
		}*/
			
		mMediaPlayer.reset();
		playSong();
	}

	public void forward() {
		if (nextTrackPosition < trackList.size()) {
			playNextSong();
		}
	}

	public void previous() {

		nextTrackPosition -= 2;
		if (nextTrackPosition <= 0) {
			nextTrackPosition = 0;
		}
		playNextSong();
	}

	public void pause() {
		mMediaPlayer.pause();
		mState = PlayerState.PAUSE;
		cancelNotification();
	}

	public interface IPlayer {
		public void prepared();
		public void end();
		public void block();

	}
}
