package com.irontec.bandabeat;

import org.alexd.jsonrpc.JSONRPCClient;
import org.alexd.jsonrpc.JSONRPCException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.bandabeat.R;
import com.irontec.bandabeat.fragment.FavoriteFragment;
import com.irontec.bandabeat.fragment.PlayerFragment;
import com.irontec.bandabeat.fragment.PlaylistFragment;
import com.irontec.bandabeat.fragment.PublicPlaylistFragment;
import com.irontec.bandabeat.fragment.TrackFragment;
import com.irontec.bandabeat.helper.Data;
import com.irontec.bandabeat.helper.Login;
import com.irontec.bandabeat.provider.PlaylistProvider;
import com.irontec.bandabeat.provider.TrackProvider;
import com.irontec.bandabeat.service.PlayerService.PlayerState;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends PlayerActivity {

	private ActionBar actionBar;
	private Tab playerTab, playlistTab, favoriteTab;
	private static boolean trackFragmentShowing = false;

	public static DisplayImageOptions imgDisplayOptions = new DisplayImageOptions.Builder()
	// .showStubImage(R.drawable.stub_image)
			.cacheInMemory().cacheOnDisc()
			// .imageScaleType(ImageScaleType.EXACT)
			.build();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_activity);
		
		EasyTracker.getInstance().activityStart(this);

		actionBar = getSupportActionBar();

		BitmapDrawable background = new BitmapDrawable(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.actionbar_background_img));
		background.setTileModeX(android.graphics.Shader.TileMode.REPEAT);
		actionBar.setBackgroundDrawable(background);

		loadTabs();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).enableLogging().memoryCacheSize(41943040)
				.discCacheSize(104857600).threadPoolSize(10).build();

		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);

		String intentAction = getIntent().getAction();

		if (intentAction != null
				&& intentAction.equals("com.irontec.action.PLAYER"))
			changeToPlayerTab();
	}
	
	public void changeToPlayerTab() {
		actionBar.selectTab(playerTab);
	}

	@Override
	protected void onResume() {
		super.onResume();
		new CheckUpdatesTask().execute(0);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mService.mState == PlayerState.PLAY && exit) {
			mService.enterForeground();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.profile:
			exit = false;
			startActivityForResult(new Intent(getApplicationContext(),
					ProfileActivity.class), 1);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			startActivity(new Intent(getApplicationContext(),
					LoginActivity.class));
			finish();
		} else if(resultCode == 2) {
			actionBar.selectTab(playerTab);
		}	
	}

	public void replacePlaylistFragment(int dirty, int idPlaylist) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		TrackFragment t = new TrackFragment(0, idPlaylist);
		ft.replace(R.id.fragment_container, t);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.addToBackStack(null);
		ft.commit();
		trackFragmentShowing = true;
	}
	

	protected void loadTabs() {
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		playlistTab = actionBar
				.newTab()
				.setText("Zerrendak")
				.setTabListener(
						new TabListListener<PlaylistFragment>(this,
								"Zerrendak", PlaylistFragment.class));
		actionBar.addTab(playlistTab);

		favoriteTab = actionBar
				.newTab()
				.setText("Gustokoenak")
				.setTabListener(
						new TabListListener<FavoriteFragment>(this,
								"Gustokoenak", FavoriteFragment.class));

		actionBar.addTab(favoriteTab);

		playerTab = actionBar
				.newTab()
				.setText("Player")
				.setTabListener(
						new TabListener<PlayerFragment>(this, "Player",
								PlayerFragment.class));
		actionBar.addTab(playerTab);

	}
	
	@Override
	public void onBackPressed() {
		if (trackFragmentShowing) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			PlaylistFragment f = new PlaylistFragment();
			ft.replace(R.id.fragment_container, f);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.commit();
			trackFragmentShowing = false;
		}
	}

	public static class TabListListener<T extends SherlockListFragment>
			implements ActionBar.TabListener {

		private SherlockListFragment mFragment;
		private final SherlockFragmentActivity mActivity;
		private final String mTag;
		private final Class<T> mClass;

		public TabListListener(SherlockFragmentActivity activity, String tag,
				Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
		}

		public TabListListener(SherlockFragmentActivity activity, String tag,
				Class<T> clz, SherlockListFragment frag) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
			mFragment = frag;
		}

		/* The following are each of the ActionBar.TabListener callbacks */

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// Check if the fragment is already initialized
			if (mFragment == null) {
				// If not, instantiate and add it to the activity
				mFragment = (SherlockListFragment) Fragment.instantiate(
						mActivity, mClass.getName());

			}
			
			ft.replace(R.id.fragment_container, mFragment, mTag);
			if (!mTag.equals("Zerrendak"))
				trackFragmentShowing = false;
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			
			 
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			if (mTag.equals("Zerrendak")) {
				mFragment = (SherlockListFragment) Fragment.instantiate(
						mActivity, mClass.getName());
				ft.replace(R.id.fragment_container, mFragment, mTag);
			}
		}
	}

	public static class TabListener<T extends SherlockFragment> implements
			ActionBar.TabListener {

		private SherlockFragment mFragment;
		private final SherlockFragmentActivity mActivity;
		private final String mTag;
		private final Class<T> mClass;

		public TabListener(SherlockFragmentActivity activity, String tag,
				Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
		}

		public TabListener(SherlockFragmentActivity activity, String tag,
				Class<T> clz, SherlockFragment frag) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
			mFragment = frag;
		}

		/* The following are each of the ActionBar.TabListener callbacks */

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// Check if the fragment is already initialized
			if (mFragment == null) {
				// If not, instantiate and add it to the activity
				mFragment = (SherlockFragment) Fragment.instantiate(mActivity,
						mClass.getName());

			}
			
			ft.replace(R.id.fragment_container, mFragment, mTag);
			trackFragmentShowing = false;
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			
	
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			//ft.replace(R.id.fragment_container, mFragment, mTag);
		}
	}

	private class CheckUpdatesTask extends AsyncTask<Integer, Boolean, Integer> {

		ProgressDialog dialog;
		String error;
		JSONRPCClient client;
		Data applicationData;

		@Override
		protected Integer doInBackground(Integer... params) {

			int response = 1;

			Login.loadLoginPreferences(getApplicationContext());

			applicationData = Data.getInstance();
			client = applicationData.client;

			try {
				Object[] parameters = { applicationData.getUserId(),
						applicationData.getToken() };
				String data = client.callString("getGeneralToken", parameters);

				if (!data.equals(applicationData.getGeneralToken())) {
					publishProgress(true);
					response = updatePlaylists();
				}

				Login.saveGeneralToken(getApplicationContext(), data);

			} catch (JSONRPCException e) {
				response = -2;
			}

			return response;
		}

		private int updatePlaylists() {

			final String[] PLAYLIST_SUMMARY_PROJECTION = new String[] { "_id",
					PlaylistProvider.idplaylist, PlaylistProvider.token, };

			ContentResolver resolver = getContentResolver();
			ContentValues values = new ContentValues();

			Cursor previousPlaylists = resolver.query(
					PlaylistProvider.CONTENT_URI, PLAYLIST_SUMMARY_PROJECTION,
					null, null, null);

			try {
				Object[] parameters = { applicationData.getUserId(),
						applicationData.getToken() };
				JSONArray data = client.callJSONArray("getUserPlaylists",
						parameters);

				resolver.delete(PlaylistProvider.CONTENT_URI,
						"idPlaylist != -1", null);

				for (int i = 0; i < data.length(); i++) {
					JSONObject playlistJSON = data.getJSONObject(i);

					int idPlaylist = playlistJSON.getInt("idPlaylist");
					String songToken = playlistJSON.getString("songToken");

					values.put(PlaylistProvider.idplaylist, idPlaylist);
					values.put(PlaylistProvider.name,
							playlistJSON.getString("name"));
					values.put(PlaylistProvider.token,
							playlistJSON.getString("token"));
					values.put(PlaylistProvider.songCount,
							playlistJSON.getString("songCount"));
					values.put(PlaylistProvider.songToken, songToken);
					values.put(PlaylistProvider.dirty, 0);

					while (previousPlaylists.moveToNext()) {
						int cursorIdPlaylist = previousPlaylists.getInt(1);
						String cursorSongToken = previousPlaylists.getString(2);

						if (cursorIdPlaylist == idPlaylist) {
							if (!cursorSongToken.equals(songToken))
								values.put(PlaylistProvider.dirty, 1);
							break;
						}

					}

					resolver.insert(PlaylistProvider.CONTENT_URI, values);
					values.clear();
				}

				previousPlaylists.close();

			} catch (JSONRPCException e) {
				return -3;
			} catch (JSONException e) {
				return -2;
			}

			return 0;
		}

		@Override
		protected void onProgressUpdate(Boolean... progress) {
			dialog = ProgressDialog.show(MainActivity.this,
					getString(R.string.actualizando),
					getString(R.string.espere));
		}

		@Override
		protected void onPostExecute(Integer result) {

			if (result == 0) {
				dialog.dismiss();
			} else if (result == -1) {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						MainActivity.this);
				alert.setTitle("Error");
				alert.setMessage(error);
				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								exit = false;
								Login.logout(getApplicationContext());
								ContentResolver resolver = getContentResolver();
								resolver.delete(PlaylistProvider.CONTENT_URI, "idPlaylist != -1", null);
								resolver.delete(TrackProvider.CONTENT_URI, null, null);
								PlayerActivity.mService.cancelNotification();
								PlayerActivity.mMediaPlayer.reset();
								finish();
							}
						});
				alert.show();
			} else if (result == -3) {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						MainActivity.this);
				alert.setTitle("Error");
				alert.setMessage(getString(R.string.error));
				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								exit = false;
								Login.logout(getApplicationContext());
								ContentResolver resolver = getContentResolver();
								resolver.delete(PlaylistProvider.CONTENT_URI,
										null, null);
								resolver.delete(TrackProvider.CONTENT_URI,
										null, null);
								startActivity(new Intent(
										getApplicationContext(),
										LoginActivity.class));
								mService.cancelNotification();
								mMediaPlayer.reset();
							}
						});
				alert.show();
			}

		}

	}
}
