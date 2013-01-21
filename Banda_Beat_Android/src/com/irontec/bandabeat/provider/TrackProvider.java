package com.irontec.bandabeat.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import com.irontec.bandabeat.db.BandaBeatOpenHelper;

public class TrackProvider extends ContentProvider {
	
	public static final String PROVIDER_NAME = "com.irontec.bandabeat.provider.Track";

	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ PROVIDER_NAME + "/tracks");

	public static final String ID_TRACK = "idTrack";
	public static final String ID_PLAYLIST = "idPlaylist";
	public static final String GRUPO = "grupo";
	public static final String TITULO = "titulo";
	public static final String ALBUM = "album";
	public static final String TOKEN = "token";
	public static final String URL = "url";
	public static final String IMAGE_MINI = "imageMini";
	public static final String IMAGE_THUMB = "imageThumb";
	public static final String IMAGE_PROFILE = "imageProfile";
	public static final String IMAGE_BIG = "imageBig";
	public static final String IMAGE_IPHONE = "imageiPhone";
	public static final String FAVORITE = "favorite";
	public static final String DURATION = "duration";
	public static final String ORDER = "trackOrder";

	private static final int TRACKS = 1;

	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "Tracks", TRACKS);
	}

	private SQLiteDatabase bandaBeatBB;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		count = bandaBeatBB.delete("Track", selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
			case TRACKS:
				return "vnd.android.cursor.dir/com.irontec.bandabeat.provider.Track";
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowID = bandaBeatBB.insert("Track", "", values);

		if (rowID > 0) {
			Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		Context context = getContext();
		BandaBeatOpenHelper dbHelper = new BandaBeatOpenHelper(context);
		bandaBeatBB = dbHelper.getWritableDatabase();
		return (bandaBeatBB == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables("Track");

		if (sortOrder == null || sortOrder == "")
			sortOrder = TITULO;

		Cursor c = sqlBuilder.query(bandaBeatBB, projection, selection,
				selectionArgs, null, null, sortOrder);

		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
		count = bandaBeatBB.update("Track", values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
