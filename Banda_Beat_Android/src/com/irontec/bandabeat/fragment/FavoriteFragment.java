package com.irontec.bandabeat.fragment;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.irontec.bandabeat.MainActivity;
import com.irontec.bandabeat.PlayerActivity;
import com.irontec.bandabeat.R;
import com.irontec.bandabeat.db.Track;
import com.irontec.bandabeat.fragment.TrackFragment.TrackCursorAdapter;
import com.irontec.bandabeat.helper.Data;
import com.irontec.bandabeat.provider.TrackProvider;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.URLConnectionImageDownloader;

public class FavoriteFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	
	private TrackCursorAdapter mAdapter;
	private ActionMode mActionMode;
	private int deleteID;
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

	    // Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.context_menu, menu);
	        return true;
	    }

	    // Called each time the action mode is shown. Always called after onCreateActionMode, but
	    // may be called multiple times if the mode is invalidated.
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false; // Return false if nothing is done
	    }

	    // Called when the user selects a contextual menu item
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	        switch (item.getItemId()) {
	            case R.id.menu_delete:
	                ContentResolver resolver = getActivity().getContentResolver();
	                ContentValues values = new ContentValues();
	                values.put("favorite", 0);
	                resolver.update(TrackProvider.CONTENT_URI, values, "_id = " + deleteID, null);
	                resolver.notifyChange(TrackProvider.CONTENT_URI, null);
	                mode.finish(); // Action picked, so close the CAB
	                return true;
	            default:
	                return false;
	        }
	    }

	    // Called when the user exits the action mode
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	        mActionMode = null;
	    }
	};

       
	@Override 
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        mAdapter = new TrackCursorAdapter(getActivity(), null);
        setListAdapter(mAdapter);
        setListShown(false);
        getLoaderManager().initLoader(0, null, this);
        
        getListView().setOnItemLongClickListener (new OnItemLongClickListener() {
     
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View v,
					int position, long id) {
				
				Cursor c = (Cursor) getListView().getItemAtPosition(position);
				
				if (mActionMode != null) {
		            return false;
		        }
		        
		        deleteID = c.getInt(0);
		        
		        // Start the CAB using the ActionMode.Callback defined above
		        mActionMode = getSherlockActivity().startActionMode(mActionModeCallback);
		        v.setSelected(true);

				return true;
			}
        	});
    }
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	   	
    	MainActivity a = (MainActivity) getActivity();
		a.exit = true;
		
		Cursor c = ((TrackCursorAdapter) l.getAdapter()).getCursor();
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
		
		Intent i = new Intent("com.irontec.action.PLAYER");
		startActivity(i);
    			
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
        		TRACK_SUMMARY_PROJECTION, "favorite = 1", null, TrackProvider.TITULO + " COLLATE LOCALIZED ASC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		 mAdapter.swapCursor(data);
		 setListShown(true);
		 setEmptyText(getString(R.string.favorite_empty));
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		 mAdapter.swapCursor(null);
	}
	
	public class TrackCursorAdapter extends CursorAdapter {

		ImageLoader imageLoader;
		private ImageView image;
	    private TextView title;
        private TextView album;
        private TextView duration;
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity())
				        .memoryCacheExtraOptions(480, 800) // max width, max height
				        .threadPoolSize(3)
				        .threadPriority(Thread.NORM_PRIORITY - 1)
				        .denyCacheImageMultipleSizesInMemory()
				        .offOutOfMemoryHandling()
				        .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation
				        .imageDownloader(new URLConnectionImageDownloader(5 * 1000, 20 * 1000)) // connectTimeout (5 s), readTimeout (20 s)
				        .tasksProcessingOrder(QueueProcessingType.FIFO)
		
				        .build();
		
	    @SuppressWarnings("deprecation")
		public TrackCursorAdapter(Context context, Cursor c) {
	        super(context, c);
	        imageLoader = ImageLoader.getInstance();
	        imageLoader.init(config);
	    }

	    @Override
	    public View newView(Context context, Cursor cursor, ViewGroup parent) {
	        LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View rowView = inflater.inflate(R.layout.favorite_row, null, true);

	        image = (ImageView) rowView.findViewById(R.id.track_image);
	        album = (TextView) rowView.findViewById(R.id.track_album);
	        title = (TextView) rowView.findViewById(R.id.track_title);
	        duration = (TextView) rowView.findViewById(R.id.track_duration);
	        
	        return rowView;
	    }

	    @Override
	    public void bindView(View v, Context context, Cursor c) {
	 
	       title.setText(c.getString(2)); 
	       album.setText(c.getString(4));
	       duration.setText(c.getString(7));
	       imageLoader.displayImage(c.getString(5), image, MainActivity.imgDisplayOptions);
	       
	       v.setTag(c.getInt(0));
	       
	       //v.setOnLongClickListener(new DeleteListener(c.getInt(0)));
	       
	    }
	    
	    /*private class DeleteListener implements OnLongClickListener {

			private int idTrack;

			public DeleteListener(int idTrack) {
				this.idTrack = idTrack;
			}

			@Override
			 public boolean onLongClick(View view) {
		        if (mActionMode != null) {
		            return false;
		        }
		        
		        deleteID = idTrack;
		        
		        // Start the CAB using the ActionMode.Callback defined above
		        mActionMode = getSherlockActivity().startActionMode(mActionModeCallback);
		        view.setSelected(true);
		        return true;
		    }

		 }*/
	}
}
