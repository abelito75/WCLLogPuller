package com.abesoft.wcl.MassPullLogs.request;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

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

}
