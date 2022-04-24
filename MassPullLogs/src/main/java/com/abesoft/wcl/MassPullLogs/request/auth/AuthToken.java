package com.abesoft.wcl.MassPullLogs.request.auth;

import java.io.IOException;
import java.util.Date;

import org.apache.http.auth.AuthenticationException;

import com.fasterxml.jackson.databind.JsonNode;

public class AuthToken {

	private static AuthToken token;

	public static AuthToken getToken() {
		if (token == null) {
			// synchronized block to remove overhead
			synchronized (AuthToken.class) {
				if (token == null) {
					// if instance is null, initialize
					token = new AuthToken();
				}

			}
		}
		return token;
	}

	private String accessToken;
	private Date expiresAt;

	private AuthToken() {
		setupToken();
	}

	public synchronized void setupToken() {
		AuthRequest request;
		try {
			request = new AuthRequest();
			request.fireRequest();
			JsonNode node = request.getJSON();
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
		return this != null;
	}

	public synchronized boolean isExpired() {
		return new Date().after(expiresAt);
	}

	public synchronized boolean isAuthenticatedAndNotExpired() {
		return isAuthenticated() && !isExpired();
	}
}
