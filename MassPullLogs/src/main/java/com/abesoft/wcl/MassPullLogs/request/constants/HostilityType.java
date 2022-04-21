package com.abesoft.wcl.MassPullLogs.request.constants;

public enum HostilityType implements AbstractConstant {

	FRIENDLIES("Friendlies"),

	ENEMIES("Enemies");

	private String name;

	private HostilityType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
