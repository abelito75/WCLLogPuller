package com.abesoft.wcl.MassPullLogs.request.monitor;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import com.abesoft.wcl.MassPullLogs.request.AbstractRequest;
import com.abesoft.wcl.MassPullLogs.request.WCLURL;
import com.abesoft.wcl.MassPullLogs.request.auth.AuthToken;

public class RateLimitRequest extends AbstractRequest {

	public RateLimitRequest() {
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
			setAuth();
		} catch (AuthenticationException | UnsupportedEncodingException e) {

		}
		return super.fireRequest();
	}

}
