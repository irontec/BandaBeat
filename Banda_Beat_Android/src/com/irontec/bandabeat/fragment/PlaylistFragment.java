package com.irontec.bandabeat.fragment;

import java.util.zip.Inflater;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.irontec.bandabeat.MainActivity;
import com.irontec.bandabeat.PlayerActivity;
import com.irontec.bandabeat.PublicPlaylistActivity;
import com.irontec.bandabeat.R;
import com.irontec.bandabeat.provider.PlaylistProvider;

public class PlaylistFragment extends SherlockListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private PlaylistCursorAdapter mAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAdapter = new PlaylistCursorAdapter(getActivity(), null);
		setListAdapter(mAdapter);
		setListShown(false);
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getTracker().trackView("Playlist");
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		MainActivity a = (MainActivity) getActivity();
		
		if (position == 0) {
			startActivityForResult(new Intent(getActivity(),
					PublicPlaylistActivity.class), 2);
			a.exit = false;
			//a.replacePublicPlaylistFragment();
		} else {
			Cursor c = (Cursor) l.getItemAtPosition(position);
			int count = c.getInt(3);

			if (count == 0) {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						getActivity());
				alert.setTitle("Abisua");
				alert.setMessage(getString(R.string.no_songs));
				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {

							}
						});
				alert.show();
			} else {
				a.replacePlaylistFragment(c.getInt(4), c.getInt(1));
			}
		}
	}

	static final String[] PLAYLIST_SUMMARY_PROJECTION = new String[] { "_id",
			PlaylistProvider.idplaylist, PlaylistProvider.name,
			PlaylistProvider.songCount, PlaylistProvider.dirty, };

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri baseUri;

		baseUri = PlaylistProvider.CONTENT_URI;

		return new CursorLoader(getActivity(), baseUri,
				PLAYLIST_SUMMARY_PROJECTION, null, null, "idPlaylist ASC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
		setListShown(true);
		setEmptyText(getString(R.string.playlist_empty));
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	private class PlaylistCursorAdapter extends CursorAdapter {

		private Context mContext;
		private LayoutInflater mInflater;
		public TextView title;
		public TextView songCount;

		@SuppressWarnings("deprecation")
		public PlaylistCursorAdapter(Context context, Cursor c) {
			super(context, c);
			mContext = context;
			mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		// „apa de œltima hora. Hay que utilizar el patr—n ViewHolder
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (!mDataValid) {
				throw new IllegalStateException(
						"this should only be called when the cursor is valid");
			}
			if (!mCursor.moveToPosition(position)) {
				throw new IllegalStateException(
						"couldn't move cursor to position " + position);
			}

			View v;

			if (mCursor.getInt(1) != -1) {
				v = mInflater.inflate(R.layout.playlist_row, null, true);
				bindView(v, mContext, mCursor);
			} else {
				v = mInflater.inflate(R.layout.playlist_bb_row, null, true);
			}
			return v;
		}

		@Override
		public void bindView(View v, Context context, Cursor cursor) {

			String name = cursor.getString(2);
			if (name == null) {
				name = "Izenik gabe";
			}

			songCount = (TextView) v.findViewById(R.id.playlist_songCount);
			title = (TextView) v.findViewById(R.id.playlist_title);

			title.setText(name);
			songCount.setText(cursor.getString(3) + " abesti");

		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			return null;
		}
	}
}
