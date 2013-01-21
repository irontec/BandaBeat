package com.irontec.bandabeat.db;

public class Track {
	
	private long idTrack;
	private String album;
	private String grupo;
	private String imageMini;
	private String imageThumb;
	private String imageBig;
	private String imageProfile;
	private String titulo;
	private String url;
	private String token;
	private String duration;
	
	
	public long getIdTrack() {
		return idTrack;
	}
	public void setIdTrack(long idTrack) {
		this.idTrack = idTrack;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getGrupo() {
		return grupo;
	}
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	public String getImageMini() {
		return imageMini;
	}
	public void setImageMini(String imageMini) {
		this.imageMini = imageMini;
	}
	public String getImageThumb() {
		return imageThumb;
	}
	public void setImageThumb(String imageThumb) {
		this.imageThumb = imageThumb;
	}
	public String getImageBig() {
		return imageBig;
	}
	public void setImageBig(String imageBig) {
		this.imageBig = imageBig;
	}
	public String getImageProfile() {
		return imageProfile;
	}
	public void setImageProfile(String imageProfile) {
		this.imageProfile = imageProfile;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}	
}
