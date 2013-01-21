package com.irontec.bandabeat.helper;

import org.alexd.jsonrpc.JSONRPCClient;
import org.alexd.jsonrpc.JSONRPCParams.Versions;

public class Data {
	
	private static Data INSTANCE = new Data();
	
	//Informaci—n de usuario
	private int userId;
	private String username;
	private String token;
	private String generalToken;
	private boolean logged;
	
	//Informaci—n de la mœsica que est‡ sonando
	
	
	public JSONRPCClient client;
    
    private Data() {
    	client = JSONRPCClient.create(
				"http://m.bandabeat.com/api/json",
				Versions.VERSION_2);

		client.setConnectionTimeout(2000);
		client.setSoTimeout(2000);
    }
    
 
    public static Data getInstance() {
        return INSTANCE;
    }

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getGeneralToken() {
		return generalToken;
	}

	public void setGeneralToken(String generalToken) {
		this.generalToken = generalToken;
	}

	public boolean isLogged() {
		return logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}
}
