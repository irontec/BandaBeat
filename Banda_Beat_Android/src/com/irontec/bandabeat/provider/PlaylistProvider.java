package com.irontec.bandabeat.provider;

import com.irontec.bandabeat.db.BandaBeatOpenHelper;

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
import android.text.TextUtils;

public class PlaylistProvider extends ContentProvider {

	public static final String PROVIDER_NAME = "com.irontec.bandabeat.provider.Playlist";

	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ PROVIDER_NAME + "/playlists");

	public static final String idplaylist = "idPlaylist";
	public static final String name = "name";
	public static final String songCount = "songCount";
	public static final String token = "token";
	public static final String songToken = "songToken";
	public static final String dirty = "dirty";

	private static final int PLAYLISTS = 1;
	private static final int PLAYLISTS_ID = 2;

	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "playlists", PLAYLISTS);
		uriMatcher.addURI(PROVIDER_NAME, "playlists/#", PLAYLISTS_ID);
	}

	private SQLiteDatabase bandaBeatBB;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;

		switch (uriMatcher.match(uri)) {
		case PLAYLISTS:
			count = bandaBeatBB.delete("Playlist", selection, selectionArgs);
			break;
		case PLAYLISTS_ID:
			String id = uri.getPathSegments().get(1);
			count = bandaBeatBB.delete("Playlist", idplaylist
					+ " = "
					+ id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case PLAYLISTS:
			return "vnd.android.cursor.dir/com.irontec.bandabeat.provider.Playlist";
		case PLAYLISTS_ID:
			return "vnd.android.cursor.item/com.irontec.bandabeat.provider.Playlist";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowID = bandaBeatBB.insert("Playlist", "", values);

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
		sqlBuilder.setTables("Playlist");

		if (uriMatcher.match(uri) == PLAYLISTS_ID)
			sqlBuilder.appendWhere(idplaylist + " = "
					+ uri.getPathSegments().get(1));

		if (sortOrder == null || sortOrder == "")
			sortOrder = name;

		Cursor c = sqlBuilder.query(bandaBeatBB, projection, selection,
				selectionArgs, null, null, sortOrder);

		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case PLAYLISTS:
			count = bandaBeatBB.update("Playlist", values, selection,
					selectionArgs);
			break;
		case PLAYLISTS_ID:
			count = bandaBeatBB.update("Playlist", values,
					idplaylist
							+ " = "
							+ uri.getPathSegments().get(1)
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
