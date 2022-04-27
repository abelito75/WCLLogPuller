package com.abesoft.wcl.MassPullLogs.request.auth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.message.BasicNameValuePair;

import com.abesoft.wcl.MassPullLogs.request.AbstractRequest;
import com.abesoft.wcl.MassPullLogs.request.WCLURL;

public class AuthRequest extends AbstractRequest {
	
	public AuthRequest(String id, String secret) throws AuthenticationException, IOException {
		super(WCLURL.AUTH_REQUEST.getURL());
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("grant_type", "client_credentials"));
		addEntity(params);
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(id, secret);
		super.addHeader(new BasicScheme().authenticate(creds, post, null));
	}

}
