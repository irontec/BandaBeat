package com.irontec.bandabeat.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class Login {

	public static final String PREFS_NAME = "BandaBeat";

	public static void saveLoginPreferences(Context c, int userId,
			String token, String username) {
		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("userId", userId);
		editor.putString("token", token);
		editor.putString("username", username);
		editor.putBoolean("logged", true);

		editor.commit();
	}
	
	public static void loadLoginPreferences(Context c) {
		
		Data data = Data.getInstance();
		
		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
	
		data.setUserId(settings.getInt("userId", -1));
		data.setToken(settings.getString("token", null));
		data.setUsername(settings.getString("username", null));
		data.setLogged(settings.getBoolean("logged", false));
		data.setGeneralToken(settings.getString("generalToken", null));
	
	}

	public static void saveGeneralToken(Context c, String token) {
		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("generalToken", token);
		editor.commit();
	}

	public static void logout(Context c) {
		SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("userId", -1);
		editor.putString("token", null);
		editor.putString("username", null);
		editor.putBoolean("logged", false);
		editor.putString("generalToken", null);

		editor.commit();	
	}
}
