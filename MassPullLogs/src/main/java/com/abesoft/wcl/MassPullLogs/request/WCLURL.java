package com.abesoft.wcl.MassPullLogs.request;

public enum WCLURL {

	AUTH_REQUEST("https://www.warcraftlogs.com/oauth/token"),

	QUERY_REQEST("https://www.warcraftlogs.com/api/v2/client");

	private String url;

	private WCLURL(String url) {
		this.url = url;
	}

	public String getURL() {
		return url;
	}
}
