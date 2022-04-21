package com.abesoft.wcl.MassPullLogs.request.constants;

public enum Soulbind implements AbstractConstant {

	NIYA(1, Covenant.NIGHT_FAE, "Niya"),

	DREAMWEAVER(2, Covenant.NIGHT_FAE, "Dreamweaver"),

	GENERAL_DRAVEN(3, Covenant.VENTHYR, "General Draven"),

	PLAGUE_DEVISER_MARILETH(4, Covenant.NECROLORD, "Plauge Deviser Marileth"),

	EMENI(5, Covenant.NECROLORD, "Emeni"),

	KORAYN(6, Covenant.NIGHT_FAE, "Korayn"),

	PELAGOS(7, Covenant.KYRIAN, "Pelagos"),

	NADJIA_THE_MISTBLADE(8, Covenant.VENTHYR, "Nadjia the Mistblade"),

	THEOTAR_THE_MAD_DUKE(9, Covenant.VENTHYR, "Theotar the Mad Duke"),

	BONESMITH_HEIRMIR(10, Covenant.NECROLORD, "Bonesmith Heirmir"),

	KLEIA(13, Covenant.KYRIAN, "Kleia"),

	FORGELITE_PRIME_MIKANIKOS(18, Covenant.KYRIAN, "Forgelite Prime Mikanikos");

	private int id;
	private Covenant sourceCovenant;
	private String name;

	private Soulbind(int id, Covenant sourceCovenant, String name) {
		this.id = id;
		this.sourceCovenant = sourceCovenant;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public Covenant getSourceCovenant() {
		return sourceCovenant;
	}

	public String getName() {
		return name;
	}

	public Soulbind[] getSoulbindForCovenant(Covenant covenant) {
		Soulbind[] binds = new Soulbind[3];

		int current = 0;

		for (Soulbind bind : Soulbind.values()) {
			if (current == 3) {
				break;
			}

			if (bind.getSourceCovenant() == covenant) {
				binds[current] = bind;
				current++;
			}
		}

		return binds;
	}

}
