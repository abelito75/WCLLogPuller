package com.abesoft.wcl.MassPullLogs.request.constants;

public enum Difficulty implements AbstractConstant {

	LFR(1, "LFR"),

	NORMAL(3, "Normal"),

	HEROIC(4, "Heroic"),

	MYTHIC(5, "Mythic");

	private int wclNumber;
	private String name;

	private Difficulty(int wclNumber, String name) {
		this.wclNumber = wclNumber;
		this.name = name;
	}

	public int getWclNumber() {
		return wclNumber;
	}

	public String getName() {
		return name;
	}

}
