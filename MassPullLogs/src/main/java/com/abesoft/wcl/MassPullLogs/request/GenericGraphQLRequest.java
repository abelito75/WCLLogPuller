package com.abesoft.wcl.MassPullLogs.request;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import com.abesoft.wcl.MassPullLogs.request.auth.AuthToken;
import com.abesoft.wcl.MassPullLogs.request.monitor.KeyMonitor;

public class GenericGraphQLRequest extends AbstractRequest {

	public GenericGraphQLRequest() {
		super(WCLURL.QUERY_REQEST.getURL());
	}

	public void setAuth() throws UnsupportedEncodingException, AuthenticationException {
		AuthToken token = AuthToken.getToken();

		if (token.isExpired()) {
			token.setupToken();
		}

		super.addHeader(new BasicHeader("Authorization", "Bearer " + token.getAccessToken()));
	}

	public void buildRequest(String query) throws UnsupportedEncodingException {
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("query", query));
		addEntity(params);
	}
	
	public boolean fireRequest() {
		try {
			KeyMonitor.getMonitor().requestFired();
			setAuth();
			return super.fireRequest();
		} catch (AuthenticationException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	}
}
