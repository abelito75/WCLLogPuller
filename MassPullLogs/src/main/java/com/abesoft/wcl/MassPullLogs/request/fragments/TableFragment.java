package com.abesoft.wcl.MassPullLogs.request.fragments;

import java.util.ArrayList;
import java.util.List;

import com.abesoft.wcl.MassPullLogs.request.constants.Boss;
import com.abesoft.wcl.MassPullLogs.request.constants.ClassSpec;
import com.abesoft.wcl.MassPullLogs.request.constants.DataType;
import com.abesoft.wcl.MassPullLogs.request.constants.Difficulty;
import com.abesoft.wcl.MassPullLogs.request.constants.HostilityType;
import com.abesoft.wcl.MassPullLogs.request.constants.KillType;
import com.abesoft.wcl.MassPullLogs.request.constants.ViewType;

public class TableFragment extends AbstractFragment {

	// name for fragment so we can have multiple table requests in 1 request
	private String name;

	// possible metrics
	private float abilityID;
	private DataType dataType;
	private int death;
	private Difficulty difficulty;
	private Boss encounterID;
	private int endTime;
	private List<Integer> fightIDs;
	private String filterExpression;
	private HostilityType hostilityType;
	private KillType killType;
	private String sourceAurasAbsent;
	private String sourceAurasPresent;
	private ClassSpec sourceClass;
	private int sourceID;
	private int sourceInstanceID;
	private int startTime;
	private String targetAurasAbsent;
	private String targetAurasPresent;
	private ClassSpec targetClass;
	private int targetID;
	private int targetInstanceID;
	private boolean translate;
	private int viewOptions;
	private ViewType viewBy;
	private int wipeCutoff;

	// actual keys
	public TableFragment(String name) {
		this.name = name;
		this.abilityID = -1;
		this.dataType = null;
		this.death = -1;
		this.difficulty = null;
		this.encounterID = null;
		this.endTime = -1;
		this.fightIDs = null;
		this.filterExpression = "";
		this.hostilityType = null;
		this.killType = null;
		this.sourceAurasAbsent = null;
		this.sourceAurasPresent = null;
		this.sourceClass = null;
		this.sourceID = -1;
		this.sourceInstanceID = -1;
		this.startTime = -1;
		this.targetAurasAbsent = null;
		this.targetAurasPresent = null;
		this.targetClass = null;
		this.targetID = -1;
		this.targetInstanceID = -1;
		this.translate = false;
		this.viewOptions = -1;
		this.viewBy = null;
		this.wipeCutoff = -1;

		fightIDs = new ArrayList<>();
	}

	public TableFragment(String name, float abilityID, DataType dataType, int death, Difficulty difficulty,
			Boss encounterID, int endTime, List<Integer> fightIDs, String filterExpression, HostilityType hostilityType,
			KillType killType, String sourceAurasAbsent, String sourceAurasPresent, ClassSpec sourceClass, int sourceID,
			int sourceInstanceID, int startTime, String targetAurasAbsent, String targetAurasPresent,
			ClassSpec targetClass, int targetID, int targetInstanceID, boolean translate, int viewOptions,
			ViewType viewBy, int wipeCutoff) {
		this.name = name;
		this.abilityID = abilityID;
		this.dataType = dataType;
		this.death = death;
		this.difficulty = difficulty;
		this.encounterID = encounterID;
		this.endTime = endTime;
		this.fightIDs = fightIDs;
		this.filterExpression = filterExpression;
		this.hostilityType = hostilityType;
		this.killType = killType;
		this.sourceAurasAbsent = sourceAurasAbsent;
		this.sourceAurasPresent = sourceAurasPresent;
		this.sourceClass = sourceClass;
		this.sourceID = sourceID;
		this.sourceInstanceID = sourceInstanceID;
		this.startTime = startTime;
		this.targetAurasAbsent = targetAurasAbsent;
		this.targetAurasPresent = targetAurasPresent;
		this.targetClass = targetClass;
		this.targetID = targetID;
		this.targetInstanceID = targetInstanceID;
		this.translate = translate;
		this.viewOptions = viewOptions;
		this.viewBy = viewBy;
		this.wipeCutoff = wipeCutoff;

		if (this.fightIDs == null) {
			this.fightIDs = new ArrayList<>();
		}
	}

	public String buildFragment() {
		if (startTime > 0) {
			startTime = 0;
		}
		if (endTime <= 0) {
			endTime = Integer.MAX_VALUE;
		}

		StringBuilder builder = new StringBuilder();
		builder.append(name);
		builder.append(":");
		builder.append("table(");

		addField(builder, "abilityID", abilityID);
		addField(builder, "dataType", dataType);
		addField(builder, "death", death);
		addField(builder, "difficulty", difficulty);
		addField(builder, "encounterID", encounterID);
		addField(builder, "endTime", endTime);
		addField(builder, "fightIDs", fightIDs);
		addQuotedField(builder, "filterExpression", filterExpression);
		addField(builder, "hostilityType", hostilityType);
		addField(builder, "killType", killType);
		addQuotedField(builder, "sourceAurasAbsent", sourceAurasAbsent);
		addQuotedField(builder, "sourceAurasPresent", sourceAurasPresent);
		addField(builder, "sourceClass", sourceClass);
		addField(builder, "sourceID", sourceID);
		addField(builder, "sourceInstanceID", sourceInstanceID);
		addField(builder, "startTime", startTime);
		addQuotedField(builder, "targetAurasAbsent", targetAurasAbsent);
		addQuotedField(builder, "targetAurasPresent", targetAurasPresent);
		addField(builder, "targetClass", targetClass);
		addField(builder, "targetID", targetID);
		addField(builder, "targetInstanceID", targetInstanceID);
		addField(builder, "translate", translate);
		addField(builder, "viewOptions", viewOptions);
		addField(builder, "viewBy", viewBy);
		addField(builder, "wipeCutoff", wipeCutoff);

		builder.append(")");
		return builder.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getAbilityID() {
		return abilityID;
	}

	public void setAbilityID(float abilityID) {
		this.abilityID = abilityID;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public int getDeath() {
		return death;
	}

	public void setDeath(int death) {
		this.death = death;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	public Boss getEncounterID() {
		return encounterID;
	}

	public void setEncounterID(Boss encounterID) {
		this.encounterID = encounterID;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public void addFightID(int fightID) {
		this.fightIDs.add(fightID);
	}

	public List<Integer> getFightIDs() {
		return fightIDs;
	}

	public void setFightIDs(List<Integer> fightIDs) {
		this.fightIDs = fightIDs;
	}

	public String getFilterExpression() {
		return filterExpression;
	}

	public void setFilterExpression(String filterExpression) {
		this.filterExpression = filterExpression;
	}

	public HostilityType getHostilityType() {
		return hostilityType;
	}

	public void setHostilityType(HostilityType hostilityType) {
		this.hostilityType = hostilityType;
	}

	public KillType getKillType() {
		return killType;
	}

	public void setKillType(KillType killType) {
		this.killType = killType;
	}

	public String getSourceAurasAbsent() {
		return sourceAurasAbsent;
	}

	public void setSourceAurasAbsent(String sourceAurasAbsent) {
		this.sourceAurasAbsent = sourceAurasAbsent;
	}

	public String getSourceAurasPresent() {
		return sourceAurasPresent;
	}

	public void setSourceAurasPresent(String sourceAurasPresent) {
		this.sourceAurasPresent = sourceAurasPresent;
	}

	public ClassSpec getSourceClass() {
		return sourceClass;
	}

	public void setSourceClass(ClassSpec sourceClass) {
		this.sourceClass = sourceClass;
	}

	public int getSourceID() {
		return sourceID;
	}

	public void setSourceID(int sourceID) {
		this.sourceID = sourceID;
	}

	public int getSourceInstanceID() {
		return sourceInstanceID;
	}

	public void setSourceInstanceID(int sourceInstanceID) {
		this.sourceInstanceID = sourceInstanceID;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public String getTargetAurasAbsent() {
		return targetAurasAbsent;
	}

	public void setTargetAurasAbsent(String targetAurasAbsent) {
		this.targetAurasAbsent = targetAurasAbsent;
	}

	public String getTargetAurasPresent() {
		return targetAurasPresent;
	}

	public void setTargetAurasPresent(String targetAurasPresent) {
		this.targetAurasPresent = targetAurasPresent;
	}

	public ClassSpec getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(ClassSpec targetClass) {
		this.targetClass = targetClass;
	}

	public int getTargetID() {
		return targetID;
	}

	public void setTargetID(int targetID) {
		this.targetID = targetID;
	}

	public int getTargetInstanceID() {
		return targetInstanceID;
	}

	public void setTargetInstanceID(int targetInstanceID) {
		this.targetInstanceID = targetInstanceID;
	}

	public boolean isTranslate() {
		return translate;
	}

	public void setTranslate(boolean translate) {
		this.translate = translate;
	}

	public int getViewOptions() {
		return viewOptions;
	}

	public void setViewOptions(int viewOptions) {
		this.viewOptions = viewOptions;
	}

	public ViewType getViewBy() {
		return viewBy;
	}

	public void setViewBy(ViewType viewBy) {
		this.viewBy = viewBy;
	}

	public int getWipeCutoff() {
		return wipeCutoff;
	}

	public void setWipeCutoff(int wipeCutoff) {
		this.wipeCutoff = wipeCutoff;
	}
}
