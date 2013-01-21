package com.irontec.bandabeat.fragment;

import java.util.ArrayList;

import org.alexd.jsonrpc.JSONRPCClient;
import org.alexd.jsonrpc.JSONRPCException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.actionbarsherlock.app.SherlockListFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.bandabeat.LoginActivity;
import com.irontec.bandabeat.MainActivity;
import com.irontec.bandabeat.PlayerActivity;
import com.irontec.bandabeat.R;
import com.irontec.bandabeat.db.Track;
import com.irontec.bandabeat.helper.Data;
import com.irontec.bandabeat.helper.Login;
import com.irontec.bandabeat.provider.PlaylistProvider;
import com.irontec.bandabeat.provider.TrackProvider;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TrackFragment extends SherlockListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private int idPlaylist;
	private int dirty;
	private TrackCursorAdapter mAdapter;
	
	public TrackFragment() {

	}
	
	public TrackFragment(int dirty, int idPlaylist) {
		this.idPlaylist = idPlaylist;
		this.dirty = dirty;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mAdapter = new TrackCursorAdapter(getActivity(), null);
		setListAdapter(mAdapter);
		setListShown(false);
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getTracker().trackView("Track");
	}
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		new CheckUpdatesTask().execute(0);
	}

	@SuppressWarnings("static-access")
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		MainActivity a = (MainActivity) getActivity();
		a.exit = true;
		
		CursorAdapter adapter = (CursorAdapter) getListAdapter();
		Cursor c = adapter.getCursor();
		c.moveToFirst();
		
		ArrayList<Track> trackList = new ArrayList<Track>();

		do {
			Track t = new Track();
			t.setUrl(c.getString(3));
			t.setTitulo(c.getString(2));
			t.setAlbum(c.getString(8));
			t.setImageBig(c.getString(5));
			t.setIdTrack(c.getInt(1));
			t.setImageProfile(c.getString(9));
			trackList.add(t);
		} while (c.moveToNext());

		PlayerActivity.mService.maxTrackCount = trackList.size();
		PlayerActivity.mService.nextTrackPosition = position;
		PlayerActivity.mService.trackList = trackList;

		PlayerActivity.mService.playNextSong();
		
		a.changeToPlayerTab();
	}

	static final String[] TRACK_SUMMARY_PROJECTION = new String[] { "_id",
			TrackProvider.ID_TRACK, TrackProvider.TITULO, TrackProvider.URL,
			TrackProvider.ALBUM, TrackProvider.IMAGE_BIG,
			TrackProvider.FAVORITE, TrackProvider.DURATION, TrackProvider.GRUPO, TrackProvider.IMAGE_PROFILE };

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri baseUri;

		baseUri = TrackProvider.CONTENT_URI;

		return new CursorLoader(getActivity(), baseUri,
				TRACK_SUMMARY_PROJECTION, "idPlaylist = " + idPlaylist, null,
				TrackProvider.ORDER + " ASC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
		setListShown(true);
		setEmptyText(getString(R.string.track_empty));
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	public class TrackCursorAdapter extends CursorAdapter {

		private ImageLoader imageLoader;
		private ImageView image;
		private TextView title;
		private TextView album;
		private TextView duration;
		private ToggleButton fav;

		@SuppressWarnings("deprecation")
		public TrackCursorAdapter(Context context, Cursor c) {
			super(context, c);
			imageLoader = ImageLoader.getInstance();
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.track_row, null, true);
			
			return v;
		}

		@Override
		public void bindView(View v, Context context, Cursor c) {

			image = (ImageView) v.findViewById(R.id.track_image);
			album = (TextView) v.findViewById(R.id.track_album);
			title = (TextView) v.findViewById(R.id.track_title);
			duration = (TextView) v
					.findViewById(R.id.track_duration);
			fav = (ToggleButton) v.findViewById(R.id.track_fav);
			
			title.setText(c.getString(2));
			album.setText(c.getString(4));
			duration.setText(c.getString(7));
			
			int isFav = c.getInt(6);
			
			if (isFav == 1) {
				fav.setChecked(true);
			} else {
				fav.setChecked(false);
			}
			
			fav.setOnClickListener(new FavListener(c.getInt(1)));

			imageLoader.displayImage(c.getString(9), image,
					MainActivity.imgDisplayOptions);
		}
		
		private class FavListener implements OnClickListener {

			private int idTrack;

			public FavListener(int idTrack) {
				this.idTrack = idTrack;
			}

			@Override
			public void onClick(View v) {

				ToggleButton tog = (ToggleButton) v;

				ContentValues values = new ContentValues();

				if (tog.isChecked()) {
					values.put("favorite", 1);

				} else {
					values.put("favorite", 0);
				}

				getActivity().getContentResolver().update(
						TrackProvider.CONTENT_URI, values,
						"idTrack = " + String.valueOf(idTrack), null);

			}

		 }
	}
	
	private class CheckUpdatesTask extends AsyncTask<Integer, Boolean, Integer> {

		ProgressDialog dialog;
		JSONRPCClient client;
		Data applicationData;
		ContentResolver resolver = getActivity().getContentResolver();

		final String[] PLAYLIST_SUMMARY_PROJECTION = new String[] { "_id", };

		@Override
		protected Integer doInBackground(Integer... params) {

			int response = -1;

			applicationData = Data.getInstance();
			client = applicationData.client;

			Cursor c = resolver.query(TrackProvider.CONTENT_URI,
					PLAYLIST_SUMMARY_PROJECTION, "idPlaylist = " + idPlaylist,
					null, null);

			if (c.getCount() == 0 || dirty == 1) {
				publishProgress(true);
				response = updateTracks();
				ContentValues values = new ContentValues();
				values.put("dirty", 0);
				resolver.update(
						PlaylistProvider.CONTENT_URI, values,
						"idPlaylist = " + idPlaylist, null);
			}

			c.close();

			return response;
		}

		private int updateTracks() {

			ContentValues values = new ContentValues();

			try {
				Object[] parameters = { idPlaylist,
						applicationData.getUserId(), applicationData.getToken() };
				JSONArray data = client.callJSONArray("getPlaylistSongs",
						parameters);

				resolver.delete(TrackProvider.CONTENT_URI, "idPlaylist = "
						+ idPlaylist, null);

				for (int i = 0; i < data.length(); i++) {
					JSONObject playlistJSON = data.getJSONObject(i);

					values.put(TrackProvider.ID_TRACK,
							playlistJSON.getInt("idTrack"));
					values.put(TrackProvider.GRUPO,
							playlistJSON.getString("grupo"));
					values.put(TrackProvider.TITULO,
							playlistJSON.getString("titulo"));
					values.put(TrackProvider.ALBUM,
							playlistJSON.getString("album"));
					values.put(TrackProvider.TOKEN,
							playlistJSON.getString("token"));
					values.put(TrackProvider.URL, playlistJSON.getString("url"));
					values.put(TrackProvider.IMAGE_MINI,
							playlistJSON.getString("imageMini"));
					values.put(TrackProvider.IMAGE_THUMB,
							playlistJSON.getString("imageThumb"));
					values.put(TrackProvider.IMAGE_PROFILE,
							playlistJSON.getString("imageProfile"));
					values.put(TrackProvider.IMAGE_BIG,
							playlistJSON.getString("imageBig"));
					values.put(TrackProvider.IMAGE_IPHONE,
							playlistJSON.getString("imageiPhone"));
					values.put(TrackProvider.DURATION,
							playlistJSON.getString("duration"));
					values.put(TrackProvider.ID_PLAYLIST, idPlaylist);
					values.put(TrackProvider.FAVORITE, 0);
					values.put(TrackProvider.ORDER, i);
					resolver.insert(TrackProvider.CONTENT_URI, values);
					values.clear();
				}

			} catch (JSONRPCException e) {
				return 1;
			} catch (JSONException e) {
				return 1;
			}

			return 0;
		}

		@Override
		protected void onProgressUpdate(Boolean... progress) {
			dialog = ProgressDialog.show(getActivity(),
					getString(R.string.actualizando),
					getString(R.string.espere));
		}

		@Override
		protected void onPostExecute(Integer result) {

			if (result == 0) {
				dialog.dismiss();
				resolver.notifyChange(TrackProvider.CONTENT_URI, null);
			} else if (result == 1) {
				AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
				alert.setTitle("Error");
				alert.setMessage(getString(R.string.error));
				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Login.logout(getActivity());
								resolver.delete(PlaylistProvider.CONTENT_URI, null, null);
								resolver.delete(TrackProvider.CONTENT_URI, null, null);
								startActivity(new Intent(getActivity(),
										LoginActivity.class));
								PlayerActivity.mService.cancelNotification();
								PlayerActivity.mService.stopSelf();
							}
						});
				alert.show();
			}

		}

	}

}
