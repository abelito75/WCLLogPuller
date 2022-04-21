package com.abesoft.wcl.MassPullLogs.request.constants;

public enum ViewType implements AbstractConstant {

	DEFAULT("Default"),

	ABILITY("Ability"),

	SOURCE("Source"),

	TARGET("Target");

	private String name;

	private ViewType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
