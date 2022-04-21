package com.abesoft.wcl.MassPullLogs.request.constants;

public enum Covenant implements AbstractConstant {

	KYRIAN(1, "Kyrian"),

	NECROLORD(4, "Necrolord"),

	NIGHT_FAE(3, "Night Fae"),

	VENTHYR(2, "Venthyr");

	private int id;
	private String name;

	private Covenant(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
