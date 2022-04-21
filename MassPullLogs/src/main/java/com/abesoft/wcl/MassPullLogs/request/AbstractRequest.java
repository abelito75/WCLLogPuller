package com.abesoft.wcl.MassPullLogs.request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.Charsets;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractRequest {

	protected HttpPost post;
	private JsonNode reseponseJSON;

	public AbstractRequest(String url) {
		this.post = new HttpPost(url);
	}

	public void addEntity(Map<String, String> body) throws UnsupportedEncodingException {
		List<NameValuePair> changedBody = new ArrayList<>();
		body.entrySet().stream()
				.forEach(mapEntry -> changedBody.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue())));
		addEntity(changedBody);
	}

	public void addEntity(List<NameValuePair> body) throws UnsupportedEncodingException {
		post.setEntity(new UrlEncodedFormEntity(body, Charsets.UTF_8));
	}

	public void addHeader(Header header) {
		post.addHeader(header);
	}

	public boolean fireRequest() {
		try (CloseableHttpClient client = HttpClients.createDefault();
				CloseableHttpResponse response = client.execute(post)) {

			String responseString = EntityUtils.toString(response.getEntity());

			ObjectMapper mapper = new ObjectMapper();
			reseponseJSON = mapper.readTree(responseString);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public JsonNode getJSON() {
		return reseponseJSON;
	}

}
