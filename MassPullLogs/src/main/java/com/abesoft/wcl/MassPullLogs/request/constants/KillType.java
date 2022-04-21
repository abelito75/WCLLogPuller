package com.abesoft.wcl.MassPullLogs.request.constants;

public enum KillType implements AbstractConstant {

	ALL("All"),

	ENCOUNTERS("Encounters"),

	KILLS("Kills"),

	TRASH("Trash"),

	WIPES("Wipes");

	private String name;

	private KillType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
