package com.abesoft.wcl.MassPullLogs.data;

public class Field {

	private boolean outputField;
	private String outputName;
	private Object value;

	public Field(Field field) {
		this(field.getValue(), field.isOutputField(), field.getOutputName());
	}

	public Field() {
		this(null, false, null);
	}

	public Field(Object value) {
		this(value, false);
	}

	public Field(Object value, boolean outputField) {
		this(value, outputField, value.toString());
	}

	public Field(Object value, boolean outputField, String outputName) {
		this.value = value;
		this.outputField = outputField;
		this.outputName = outputName;
	}

	public boolean isOutputField() {
		return outputField;
	}

	public void setOutputField(boolean outputField) {
		this.outputField = outputField;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getOutputName() {
		return outputName;
	}

	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}
}
