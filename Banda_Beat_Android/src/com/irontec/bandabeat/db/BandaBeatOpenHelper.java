package com.irontec.bandabeat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BandaBeatOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "BandaBeat.db";
	private static final int DATABASE_VERSION = 2;

	private static final String PLAYLIST_TABLE_CREATE = "CREATE TABLE Playlist ( _id INTEGER PRIMARY KEY AUTOINCREMENT, idPlaylist INTEGER, name TEXT, songCount INTEGER, songToken TEXT, token TEXT, dirty INTEGER );";
	private static final String TRACK_TABLE_CREATE = "CREATE TABLE Track ( _id INTEGER PRIMARY KEY AUTOINCREMENT, idTrack INTEGER, album TEXT, grupo TEXT, duration TEXT, imageBig TEXT, imageMini TEXT, imageThumb TEXT, imageProfile TEXT, imageiPhone TEXT, titulo TEXT, token TEXT, url TEXT, idPlaylist INTEGER, favorite INTEGER, trackOrder INTEGER );";

	public BandaBeatOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TRACK_TABLE_CREATE);
		db.execSQL(PLAYLIST_TABLE_CREATE);
		//Para añadir listas públicas
		db.execSQL("INSERT INTO Playlist (idPlaylist) VALUES (-1);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(BandaBeatOpenHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS Playlist");
		db.execSQL("DROP TABLE IF EXISTS Track");
		onCreate(db);
	}

}