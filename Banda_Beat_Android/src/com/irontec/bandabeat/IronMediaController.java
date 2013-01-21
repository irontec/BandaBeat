package com.irontec.bandabeat;

import java.util.Formatter;
import java.util.Locale;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.irontec.bandabeat.R;
import com.irontec.bandabeat.db.Track;
import com.nostra13.universalimageloader.core.ImageLoader;

public class IronMediaController extends LinearLayout {

	private View mRoot;
	private Context mContext;
	public ImageButton mPlayButton, mBackButton, mNextButton;
	// Daba un memory leak al extresar la aplicaci—n. La ponemos est‡tica para
	// utilizar siempre el mismo
	public static ImageView bigImage;
	private ProgressBar mProgress;
	private TextView mEndTime, mCurrentTime, mSongName, mAlbumName;
	private IIronMediaController mPlayer;
	private StringBuilder mFormatBuilder;
	private Formatter mFormatter;
	private boolean mDragging;
	public boolean stop;

	public static final int SHOW_PROGRESS = 2;

	public IronMediaController(Context context) {
		super(context);
		mContext = context;
	}

	public IronMediaController(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		makeControllerView();
	}

	public void setPlayButtonBackground() {
		if (mPlayer.isPlaying()) {
			mPlayButton.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.boton_pause_selector));
		} else {
			mPlayButton.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.boton_play_selector));
		}
	}

	private void initControllerViewBig(View v) {

		bigImage = (ImageView) findViewById(R.id.player_controller_image);

		mSongName = (TextView) v.findViewById(R.id.player_controller_songname);
		mAlbumName = (TextView) v
				.findViewById(R.id.player_controller_albumname);

		mPlayButton = (ImageButton) v.findViewById(R.id.player_controller_play);
		mPlayButton.setOnClickListener(mPlayListener);

		mBackButton = (ImageButton) v.findViewById(R.id.player_controller_back);
		mBackButton.setOnClickListener(mBackListener);

		mNextButton = (ImageButton) v.findViewById(R.id.player_controller_next);
		mNextButton.setOnClickListener(mNextListener);

		/*
		 * mHideButton = (Button) v.findViewById(R.id.player_controller_hide);
		 * mHideButton.setOnClickListener(mHideListener);
		 */

		mProgress = (ProgressBar) v
				.findViewById(R.id.player_controller_progress);

		if (mProgress != null) {
			if (mProgress instanceof SeekBar) {
				SeekBar seeker = (SeekBar) mProgress;
				seeker.setOnSeekBarChangeListener(mSeekListener);
				seeker.setMax(1000);
			}
		}

		mEndTime = (TextView) findViewById(R.id.player_controller_endtime);

		mCurrentTime = (TextView) findViewById(R.id.player_controller_currenttime);

		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

		mDragging = false;

	}

	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Log.d("MSG", "Mesaje RECIBIDO");
			int pos;
			switch (msg.what) {
			case SHOW_PROGRESS:
				pos = setProgress();
				setPlayButtonBackground();
				if (!mDragging && !stop)
					postDelayed(ru, 1000);

				break;
			}
		}

	};

	public Runnable ru = new Runnable() {

		@Override
		public void run() {
			Message msg = new Message();
			msg.what = SHOW_PROGRESS;
			Log.d("MSG", "Mesaje enviado");
			mHandler.sendMessage(msg);
		}
	};

	protected View makeControllerView() {
		LayoutInflater inflate = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRoot = inflate.inflate(R.layout.player_controller, this);

		initControllerViewBig(mRoot);

		return mRoot;
	}

	public void setMediaPlayer(IIronMediaController player) {
		mPlayer = player;
	}

	public void setEnabledInterface(boolean enabled) {
		mPlayButton.setEnabled(enabled);
		mBackButton.setEnabled(enabled);
		mNextButton.setEnabled(enabled);
		mProgress.setEnabled(enabled);
	}

	public void setEndInterface() {
		mPlayButton.setEnabled(true);
		mBackButton.setEnabled(true);
		mNextButton.setEnabled(false);
		mProgress.setEnabled(true);
	}

	private String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	public void setTrackInformation() {
		Track t = mPlayer.getCurrentTrack();
		if (t != null) {
			mSongName.setText(t.getTitulo());

			String group = t.getGrupo();
			String album = t.getAlbum();

			StringBuffer tmp = new StringBuffer();

			if (group != null) {
				tmp.append(group);
				if (album != null) {
					tmp.append(" - ");
					tmp.append(album);
				}
			} else {
				if (album != null) {
					tmp.append(album);
				}
			}

			mAlbumName.setText(tmp.toString());

			ImageLoader.getInstance().displayImage(t.getImageProfile(),
					bigImage, MainActivity.imgDisplayOptions);
		}
	}

	private int setProgress() {
		if (mPlayer == null || mDragging || !mPlayer.isPlaying()) {
			return 0;
		}
		int position = mPlayer.getCurrentPosition();
		int duration = mPlayer.getDuration();
		if (mProgress != null) {
			if (duration > 0) {
				// use long to avoid overflow
				long pos = 1000L * position / duration;
				mProgress.setProgress((int) pos);
			}
			// int percent = mPlayer.getBufferPercentage();
			// mProgress.setSecondaryProgress(percent * 10);
		}

		if (mEndTime != null)
			mEndTime.setText(stringForTime(duration));
		if (mCurrentTime != null)
			mCurrentTime.setText(stringForTime(position));

		return position;
	}

	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		long duration;

		@Override
		public void onStartTrackingTouch(SeekBar bar) {
			duration = mPlayer.getDuration();
		}

		@Override
		public void onProgressChanged(SeekBar bar, int progress,
				boolean fromtouch) {
			if (fromtouch) {
				mDragging = true;
				duration = mPlayer.getDuration();
				long newposition = (duration * progress) / 1000L;
				mPlayer.seekTo((int) newposition);
				if (mCurrentTime != null)
					mCurrentTime.setText(stringForTime((int) newposition));
			}

		}

		@Override
		public void onStopTrackingTouch(SeekBar bar) {
			mDragging = false;
			mHandler.post(ru);
		}
	};

	private View.OnClickListener mPlayListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			boolean isPlaying = mPlayer.playOrPause();

			if (isPlaying) {
				mPlayButton.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.boton_pause_selector));
			} else {
				mPlayButton.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.boton_play_selector));
			}
		}
	};

	private View.OnClickListener mBackListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mPlayer.previous();
		}
	};

	private View.OnClickListener mNextListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mPlayer.forward();
		}
	};

	public interface IIronMediaController {
		boolean playOrPause();

		void previous();

		void forward();

		int getCurrentPosition();

		int getDuration();

		void seekTo(int position);

		int getBufferPercentage();

		Track getCurrentTrack();

		boolean isPlaying();

	};

}
