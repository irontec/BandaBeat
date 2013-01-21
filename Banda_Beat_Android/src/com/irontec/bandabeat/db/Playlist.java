package com.irontec.bandabeat.db;

public class Playlist {
	
	private int idPlaylist;
	private String name;
	private int songCount;
	private String token;
	private String songToken;
	
	
	public int getIdPlaylist() {
		return idPlaylist;
	}
	public void setIdPlaylist(int idPlaylist) {
		this.idPlaylist = idPlaylist;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSongCount() {
		return songCount;
	}
	public void setSongCount(int songCount) {
		this.songCount = songCount;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getSongToken() {
		return songToken;
	}
	public void setSongToken(String songToken) {
		this.songToken = songToken;
	}
	
	
}
