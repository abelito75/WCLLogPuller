package com.abesoft.wcl.MassPullLogs.request.constants;

public enum Boss implements AbstractConstant {

	VIGILANT_GUARDIAN(2512, "Vigilant Guardian"),

	DAUSEGNE_THE_FALLEN_ORACLE(2540, "Dausegne, the Fallen Oracle"),

	ARTIFICER_XYMOX(2553, "Artificer Xy'mox"),

	PROTOTYPE_PANTHEON(2544, "Prototype Pantheon"),

	SKOLEX_THE_INSATIABLE_RAVENER(2542, "Skolex, the Insatiable Ravener"),

	HALONDRUS_THE_RECLAIMER(2529, "Halondrus the Reclaimer"),

	LIHUVIM_PRINCIPAL_ARCHITECT(2539, "Lihuvim, Principal Architect"),

	ANDUIN_WRYNN(2546, "Anduin Wrynn"),

	LORDS_OF_DREAD(2543, "Lords of Dread"),

	RYGELON(2549, "Rygelon"),

	THE_JAILER(2537, "The Jailer");

	private int id;
	private String name;

	private Boss(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}
}
