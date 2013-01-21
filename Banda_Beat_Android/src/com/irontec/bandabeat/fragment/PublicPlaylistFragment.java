package com.irontec.bandabeat.fragment;

import java.util.ArrayList;
import org.alexd.jsonrpc.JSONRPCClient;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.irontec.bandabeat.PublicPlaylistActivity;
import com.irontec.bandabeat.PublicTrackActivity;
import com.irontec.bandabeat.R;
import com.irontec.bandabeat.db.Playlist;
import com.irontec.bandabeat.helper.Data;

public class PublicPlaylistFragment extends SherlockListFragment implements
		LoaderCallbacks<ArrayList<Playlist>> {

	@Override
	public void onResume() {
		super.onResume();
		getLoaderManager().initLoader(0, null, this).forceLoad();
	}

	@Override
	public Loader<ArrayList<Playlist>> onCreateLoader(int arg0, Bundle arg1) {
		return new PlaylistLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<Playlist>> loader,
			ArrayList<Playlist> data) {
		setListAdapter(new PlaylistAdapter(data));
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<Playlist>> arg0) {
		setListAdapter(null);
	}
	
    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        PublicPlaylistActivity a = (PublicPlaylistActivity) getActivity();
        PlaylistAdapter adapter = (PlaylistAdapter) getListAdapter();
        Playlist playlist = (Playlist) adapter.getItem(position);
        Intent i = new Intent(getActivity(), PublicTrackActivity.class);
        i.putExtra("playlistTitle", playlist.getName());
        i.putExtra("idPlaylist", playlist.getIdPlaylist());
        a.exit = false;
        a.setResult(2);
        startActivityForResult(i, 2);
    }
    

	private class PlaylistAdapter extends BaseAdapter {

		private ArrayList<Playlist> list;
		private LayoutInflater mInflater;

		public PlaylistAdapter(ArrayList<Playlist> data) {
			this.list = data;
			mInflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
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
			PlaylistViewHolder holder;

			if (v == null) {
				holder = new PlaylistViewHolder();
				v = mInflater.inflate(R.layout.playlist_row, null, true);
				holder.songCount = (TextView) v
						.findViewById(R.id.playlist_songCount);
				holder.title = (TextView) v.findViewById(R.id.playlist_title);
				v.setTag(holder);
			} else {
				holder = (PlaylistViewHolder) v.getTag();
			}

			Playlist p = list.get(position);

			holder.title.setText(p.getName());
			holder.songCount.setText(p.getSongCount() + " abesti");

			return v;
		}
	}

	static class PlaylistViewHolder {
		public TextView title;
		public TextView songCount;
	}

}

class PlaylistLoader extends AsyncTaskLoader<ArrayList<Playlist>> {

	public PlaylistLoader(Context c) {
		super(c);
	}

	@Override
	public ArrayList<Playlist> loadInBackground() {

		Data applicationData = Data.getInstance();
		JSONRPCClient client = applicationData.client;

		ArrayList<Playlist> list = new ArrayList<Playlist>();

		try {

			Object[] parameters = {};
			JSONArray data = client.callJSONArray("getPublicPlaylists",
					parameters);

			for (int i = 0; i < data.length(); i++) {

				Playlist playlist = new Playlist();
				JSONObject playlistJSON = data.getJSONObject(i);

				playlist.setIdPlaylist(playlistJSON.getInt("idPlaylist"));
				playlist.setName(playlistJSON.getString("name"));
				playlist.setSongCount(playlistJSON.getInt("songCount"));

				list.add(playlist);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
}