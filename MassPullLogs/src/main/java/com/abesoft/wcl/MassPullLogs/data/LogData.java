package com.abesoft.wcl.MassPullLogs.data;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This defines a singular log It contains hardcoded fields that are required to
 * do most wcl requests It also contains an map that allows someone to store
 * extra data such as results This map should get printed to output everytime
 * 
 * @author Abe
 *
 */
public class LogData {

	private Map<String, Field> dataHolder;

	public LogData() {
		dataHolder = new LinkedHashMap<>();
		for (DefaultField field : DefaultField.values()) {
			dataHolder.put(field.getOutputName(), field.getField());
		}
	}

	public void outputWCLLink() {
		Field field = dataHolder.get(DefaultField.REPORT_CODE.getOutputName());
		String reportCode = field.getValue().toString();
		field.setOutputField(false);

		field = dataHolder.get(DefaultField.FIGHT_ID.getOutputName());
		String fightID = field.getValue().toString();
		field.setOutputField(false);

		String wclLink = "https://www.warcraftlogs.com/reports/" + reportCode + "/#fight=" + fightID;
		field = new Field(wclLink, true, "WCL Link");
		dataHolder.put("wclLink", field);
	}

	public Field getField(String fieldToGet) {
		return dataHolder.get(fieldToGet);
	}

	public Object getValue(String fieldToGet) {
		Field field = getField(fieldToGet);
		return field == null ? null : field.getValue();
	}

	public void setFieldValue(String fieldToSet, Object value) {
		Field field = getField(fieldToSet);
		if (field == null) {
			field = new Field();
			dataHolder.put(fieldToSet, field);
		}
		field.setValue(value);
	}

	public void setFieldValue(String fieldToSet, Object value, boolean output) {
		Field field = getField(fieldToSet);
		if (field == null) {
			field = new Field(value, output);
			dataHolder.put(fieldToSet, field);
		}
		field.setValue(value);
	}

	public void setFieldValue(String fieldToSet, Object value, boolean output, String outputName) {
		Field field = getField(fieldToSet);
		if (field == null) {
			field = new Field(value, output, outputName);
			dataHolder.put(fieldToSet, field);
		}
		field.setValue(value);
	}

	public void enableAllFields() {
		for (Entry<String, Field> e : dataHolder.entrySet()) {
			e.getValue().setOutputField(true);
		}
	}

	public void disableAllFields() {
		for (Entry<String, Field> e : dataHolder.entrySet()) {
			e.getValue().setOutputField(false);
		}
	}

	public void removeField(String fieldToRemove) {
		dataHolder.remove(fieldToRemove);
	}

	public String getBossName() {
		return getField(DefaultField.BOSS_NAME.getOutputName()).getValue().toString();
	}

	public String getPlayerName() {
		return getField(DefaultField.PLAYER_NAME.getOutputName()).getValue().toString();
	}

	public String getPlayerClass() {
		return getField(DefaultField.PLAYER_CLASS.getOutputName()).getValue().toString();
	}

	public String getPlayerSpec() {
		return getField(DefaultField.PLAYER_SPEC.getOutputName()).getValue().toString();
	}

	public String getReportCode() {
		return getField(DefaultField.REPORT_CODE.getOutputName()).getValue().toString();
	}

	public String getFightID() {
		return getField(DefaultField.FIGHT_ID.getOutputName()).getValue().toString();
	}

	public String getSourceID() {
		return getField(DefaultField.SOURCE_ID.getOutputName()).getValue().toString();
	}

	public void setBossName(Object value) {
		setFieldValue(DefaultField.BOSS_NAME.getOutputName(), value);
	}

	public void setPlayerName(Object value) {
		setFieldValue(DefaultField.PLAYER_NAME.getOutputName(), value);
	}

	public void setPlayerClass(Object value) {
		setFieldValue(DefaultField.PLAYER_CLASS.getOutputName(), value);
	}

	public void setPlayerSpec(Object value) {
		setFieldValue(DefaultField.PLAYER_SPEC.getOutputName(), value);
	}

	public void setReportCode(Object value) {
		setFieldValue(DefaultField.REPORT_CODE.getOutputName(), value);
	}

	public void setFightID(Object value) {
		setFieldValue(DefaultField.FIGHT_ID.getOutputName(), value);
	}

	public void setSourceID(Object value) {
		setFieldValue(DefaultField.SOURCE_ID.getOutputName(), value);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, JsonNode> getTables() {	
		return (Map<String, JsonNode>)getField("Tables").getValue();
	}

	/**
	 * Returns the data as a unmodifiable map
	 * 
	 * @return a map
	 */
	public Map<String, Field> getData() {
		return Collections.unmodifiableMap(dataHolder);
	}

}
