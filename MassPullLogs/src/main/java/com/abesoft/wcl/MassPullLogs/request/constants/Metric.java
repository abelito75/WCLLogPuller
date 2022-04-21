package com.abesoft.wcl.MassPullLogs.request.constants;

public enum Metric implements AbstractConstant {

	DPS("dps"),

	HPS("hps");

	private String Metric;

	private Metric(String metric) {
		this.Metric = metric;
	}

	public String getName() {
		return Metric;
	}
}