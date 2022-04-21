package com.abesoft.wcl.MassPullLogs.request.constants;

/**
 * Simple little enum to hold class/spec names so
 * 
 * @author Abelito75
 */
public enum ClassSpec {

	DRUID_RESTORATION("Druid", "Restoration", "#FF7C0A"),

	MONK_MISTWEAVER("Monk", "Mistweaver", "#00FF98"),

	MONK_WINDWALKER("Monk", "Windwalker", "#00FF98"),

	PALADIN_HOLY("Paladin", "Holy", "#F48CBA"),

	PRIEST_HOLY("Priest", "Holy", "#FFFFFF"),

	PRIEST_DISCIPLINE("Priest", "Discipline", "#FFFFFF"),

	SHAMAN_RESTORATION("Shaman", "Restoration", "#0070DD");

	/**
	 * The name of the class
	 */
	private final String className;

	/**
	 * The name of the spec
	 */
	private final String spec;

	/**
	 * Color of class
	 */
	private final String color;

	private ClassSpec(String className, String specs, String color) {
		this.className = className;
		this.spec = specs;
		this.color = color;
	}

	/**
	 * Will return just the class name
	 * 
	 * @return Class Name IE Monk
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Will return the spec name
	 * 
	 * @return Spec name IE Mistweaver
	 */
	public String getSpec() {
		return spec;
	}

	/**
	 * Will return the classes color
	 * 
	 * @return Class color as a hex string
	 */
	public String getColor() {
		return color;
	}

	/**
	 * Will return the spec name then space then class name
	 * 
	 * @return IE Mistweaver Monk
	 */
	public String getSpecClass() {
		return spec + " " + className;
	}

	/**
	 * Will return the class name then a space then spec name
	 * 
	 * @return IE Monk Mistweaver
	 */
	public String getClassSpec() {
		return className + " " + spec;
	}
}