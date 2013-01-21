package com.irontec.bandabeat.fragment;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.bandabeat.IronMediaController;
import com.irontec.bandabeat.PlayerActivity;
import com.irontec.bandabeat.R;
import com.irontec.bandabeat.IronMediaController.IIronMediaController;
import com.irontec.bandabeat.db.Track;
import com.irontec.bandabeat.service.PlayerService.IPlayer;
import com.irontec.bandabeat.service.PlayerService.PlayerState;

public class PlayerFragment extends SherlockFragment implements
		IIronMediaController, IPlayer {

	private IronMediaController mMediaController;
	private MediaPlayer mMediaPlayer;
	private boolean gAnalyticsDataSend;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mMediaPlayer = PlayerActivity.mMediaPlayer;
	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getTracker().trackView("Player");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.player, container, false);
		mMediaController = (IronMediaController) v
				.findViewById(R.id.mediaControllerBig);
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		mMediaController.setMediaPlayer(this);
		PlayerActivity.mService.mPlayer = this;
		if (isPlaying() || PlayerActivity.mService.mState == PlayerState.PAUSE) {
			mMediaController.stop = false;
			mMediaController.setEnabledInterface(true);
			mMediaController.setTrackInformation();
			mMediaController.mHandler
					.sendEmptyMessage(IronMediaController.SHOW_PROGRESS);
		} else {
			mMediaController.setEnabledInterface(false);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mMediaController.stop = true;
		mMediaController.mHandler.removeCallbacks(mMediaController.ru);
	}

	// IIronMediaController

	@Override
	public boolean playOrPause() {
		if (mMediaPlayer.isPlaying()) {
			PlayerActivity.mService.pause();
			return false;
		} else {
			mMediaPlayer.start();
			return true;
		}
	}

	@Override
	public void previous() {
		PlayerActivity.mService.previous();
	}

	@Override
	public void forward() {
		PlayerActivity.mService.forward();
	}

	@Override
	public boolean isPlaying() {
		if (mMediaPlayer != null)
			return mMediaPlayer.isPlaying();
		return false;
	}

	@Override
	public int getCurrentPosition() {
		int currentPosition = mMediaPlayer.getCurrentPosition();

		if (!gAnalyticsDataSend && (currentPosition / 1000) > 60) {
			gAnalyticsDataSend = true;
			EasyTracker.getTracker().trackEvent("Music", "Listen",
					PlayerActivity.mService.currentTrack.getTitulo(), (long) 1);
		}

		return currentPosition;
	}

	@Override
	public int getDuration() {
		return mMediaPlayer.getDuration();
	}

	@Override
	public void seekTo(int position) {
		mMediaPlayer.seekTo(position);
	}

	@Override
	public int getBufferPercentage() {
		return (mMediaPlayer.getCurrentPosition() * 1000)
				/ mMediaPlayer.getDuration();
	}

	@Override
	public Track getCurrentTrack() {
		return PlayerActivity.mService.currentTrack;
	}

	// IPlayer
	@Override
	public void prepared() {
		gAnalyticsDataSend = false;
		mMediaController.setEnabledInterface(true);
		mMediaController.setTrackInformation();
		mMediaController.mHandler
				.sendEmptyMessage(IronMediaController.SHOW_PROGRESS);
	}

	@Override
	public void block() {
		mMediaController.setEnabledInterface(false);
		mMediaController.mHandler.removeCallbacks(mMediaController.ru);
	}

	@Override
	public void end() {
		mMediaController.setEndInterface();
	}
}
