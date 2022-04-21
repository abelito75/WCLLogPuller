package com.abesoft.wcl.MassPullLogs.request.constants;

public enum DataType implements AbstractConstant {

	SUMMARY("Summary"),

	BUFFS("Buffs"),

	CASTS("Casts"),

	DAMAGE_DONE("DamageDone"),

	DAMAGE_TAKEN("DamageTaken"),

	DEATHS("Deaths"),

	DEBUFFS("Debuffs"),

	DISPELS("Dispels"),

	HEALING("Healing"),

	INTERRUPTS("Interrupts"),

	RESOURCES("Resources"),

	SUMMONS("Summons"),

	SURVIVABILITY("Survivability"),

	THREAT("Threat");

	private String name;

	private DataType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
