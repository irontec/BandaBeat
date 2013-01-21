package com.irontec.bandabeat.fragment;

import java.util.ArrayList;
import org.alexd.jsonrpc.JSONRPCClient;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.actionbarsherlock.app.SherlockListFragment;
import com.irontec.bandabeat.MainActivity;
import com.irontec.bandabeat.PlayerActivity;
import com.irontec.bandabeat.PublicTrackActivity;
import com.irontec.bandabeat.R;
import com.irontec.bandabeat.db.Playlist;
import com.irontec.bandabeat.db.Track;
import com.irontec.bandabeat.fragment.TrackFragment.TrackCursorAdapter;
import com.irontec.bandabeat.helper.Data;
import com.irontec.bandabeat.provider.TrackProvider;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PublicTrackFragment extends SherlockListFragment implements
		LoaderCallbacks<ArrayList<Track>> {

	private int idPlaylist;
	private ArrayList<Track> mTrackList;

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		idPlaylist = ((PublicTrackActivity) getActivity()).idPlaylist;
	}

	@Override
	public void onResume() {
		super.onResume();
		getLoaderManager().initLoader(0, null, this).forceLoad();
	}

	@Override
	public Loader<ArrayList<Track>> onCreateLoader(int arg0, Bundle arg1) {
		return new TrackLoader(getActivity(), idPlaylist);
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<Track>> loader,
			ArrayList<Track> data) {
		mTrackList = data;
		setListAdapter(new TrackAdapter(data));
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<Track>> arg0) {
		setListAdapter(null);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		PublicTrackActivity a = (PublicTrackActivity) getActivity();
		a.exit = false;
	
		PlayerActivity.mService.maxTrackCount = mTrackList.size();
		PlayerActivity.mService.nextTrackPosition = position;
		PlayerActivity.mService.trackList = mTrackList;

		PlayerActivity.mService.playNextSong();
		
		/*Intent i = new Intent("com.irontec.action.PLAYER");
		startActivity(i);*/
		
		getActivity().setResult(2);
		getActivity().finish();

	}

	private class TrackAdapter extends BaseAdapter {

		private ArrayList<Track> list;
		private LayoutInflater mInflater;
		private ImageLoader imageLoader;

		public TrackAdapter(ArrayList<Track> data) {
			this.list = data;
			mInflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			imageLoader = ImageLoader.getInstance();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {

			TrackViewHolder holder;

			if (v == null) {
				holder = new TrackViewHolder();
				v = mInflater.inflate(R.layout.track_public_row, null, true);
				holder.image = (ImageView) v.findViewById(R.id.track_image);
				holder.album = (TextView) v.findViewById(R.id.track_album);
				holder.title = (TextView) v.findViewById(R.id.track_title);
				holder.duration = (TextView) v
						.findViewById(R.id.track_duration);
				v.setTag(holder);
			} else {
				holder = (TrackViewHolder) v.getTag();
			}

			Track t = list.get(position);

			holder.title.setText(t.getTitulo());
			holder.album.setText(t.getAlbum());
			holder.duration.setText(t.getDuration());
			
			imageLoader.displayImage(t.getImageProfile(), holder.image,
					MainActivity.imgDisplayOptions);

			return v;
		}
	}

	static class TrackViewHolder {
		private ImageView image;
		private TextView title;
		private TextView album;
		private TextView duration;
	}

}

class TrackLoader extends AsyncTaskLoader<ArrayList<Track>> {

	private int idPlaylist;

	public TrackLoader(Context c, int idPlaylist) {
		super(c);
		this.idPlaylist = idPlaylist;
	}

	@Override
	public ArrayList<Track> loadInBackground() {

		Data applicationData = Data.getInstance();
		JSONRPCClient client = applicationData.client;

		ArrayList<Track> list = new ArrayList<Track>();

		try {

			Object[] parameters = { idPlaylist, "", "" };
			JSONArray data = client.callJSONArray("getPlaylistSongs",
					parameters);

			for (int i = 0; i < data.length(); i++) {

				Track track = new Track();
				JSONObject playlistJSON = data.getJSONObject(i);

				track.setIdTrack(playlistJSON.getInt("idTrack"));
				track.setGrupo(playlistJSON.getString("grupo"));
				track.setTitulo(playlistJSON.getString("titulo"));
				track.setAlbum(playlistJSON.getString("album"));
				track.setUrl(playlistJSON.getString("url"));
				track.setImageProfile(playlistJSON.getString("imageProfile"));
				track.setImageBig(playlistJSON.getString("imageBig"));
				track.setDuration(playlistJSON.getString("duration"));

				list.add(track);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
}