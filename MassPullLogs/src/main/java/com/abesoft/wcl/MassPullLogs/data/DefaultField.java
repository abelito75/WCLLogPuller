package com.abesoft.wcl.MassPullLogs.data;

public enum DefaultField {

	BOSS_NAME(new Field("", true, "Boss Name")),

	PLAYER_NAME(new Field("", true, "Player Name")),

	PLAYER_CLASS(new Field("", true, "Player Class")),

	PLAYER_SPEC(new Field("", true, "Player Spec")),

	REPORT_CODE(new Field("", true, "Report Code")),

	FIGHT_ID(new Field("", true, "FightID")),

	SOURCE_ID(new Field("", true, "SouceID"));

	private Field field;

	private DefaultField(Field field) {
		this.field = field;
	}

	public Field getField() {
		return new Field(field);
	}

	public Object getFieldValue() {
		return field.getValue();
	}

	public boolean getOutputField() {
		return field.isOutputField();
	}

	public String getOutputName() {
		return field.getOutputName();
	}

}
