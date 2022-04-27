package com.abesoft.wcl.MassPullLogs.request.auth;

import java.io.IOException;
import java.util.Date;

import org.apache.http.auth.AuthenticationException;

import com.fasterxml.jackson.databind.JsonNode;

public class AuthToken {

	private String accessToken;
	private Date expiresAt;

	public AuthToken(String id, String secret) {
		setupToken(id, secret);
	}

	public synchronized void setupToken(String id, String secret) {
		AuthRequest request;
		try {
			request = new AuthRequest(id, secret);
			request.fireRequest();
			JsonNode node = request.getJSON();
			
			if(node.get("access_token") == null) {
				return;
			}
			
			accessToken = node.get("access_token").asText();

			// We remove 30 seconds so we have a little wiggle room
			long expiresIn = node.get("expires_in").asLong() - (1000 * 30);
			expiresAt = new Date(new Date().getTime() + expiresIn);

		} catch (AuthenticationException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized String getAccessToken() {
		return accessToken;
	}

	public synchronized boolean isAuthenticated() {
		return accessToken != null;
	}

	public synchronized boolean isExpired() {
		return expiresAt != null && new Date().after(expiresAt);
	}

	public synchronized boolean isAuthenticatedAndNotExpired() {
		return isAuthenticated() && !isExpired();
	}
}
