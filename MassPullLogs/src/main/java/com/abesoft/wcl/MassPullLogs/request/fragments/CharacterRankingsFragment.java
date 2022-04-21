package com.abesoft.wcl.MassPullLogs.request.fragments;

import com.abesoft.wcl.MassPullLogs.request.constants.ClassSpec;
import com.abesoft.wcl.MassPullLogs.request.constants.Covenant;
import com.abesoft.wcl.MassPullLogs.request.constants.Difficulty;
import com.abesoft.wcl.MassPullLogs.request.constants.Metric;
import com.abesoft.wcl.MassPullLogs.request.constants.Soulbind;

public class CharacterRankingsFragment extends AbstractFragment {

	private String bracket;
	private Difficulty difficulity;
	private String filter;
	private int page;
	private String partition;
	private String region;
	private String serverSlug;
	private int size;
	private Metric metric;
	private boolean includeCombatantInfo;
	private ClassSpec className;
	private ClassSpec specName;
	private Covenant covenantID;
	private Soulbind soulbindID;

	public CharacterRankingsFragment() {

	}

	public CharacterRankingsFragment(String bracket, Difficulty difficulity, String filter, int page, String partition,
			String region, String serverSlug, int size, Metric metric, boolean includeCombatantInfo,
			ClassSpec className, ClassSpec specName, Covenant covenantID, Soulbind soulbindID) {
		this.bracket = bracket;
		this.difficulity = difficulity;
		this.filter = filter;
		this.page = page;
		this.partition = partition;
		this.region = region;
		this.serverSlug = serverSlug;
		this.size = size;
		this.metric = metric;
		this.includeCombatantInfo = includeCombatantInfo;
		this.className = className;
		this.specName = specName;
		this.covenantID = covenantID;
		this.soulbindID = soulbindID;
	}

	public String buildFragment() {
		StringBuilder builder = new StringBuilder();
		builder.append("characterRankings(");

		addField(builder, "bracket", bracket);
		addField(builder, "difficulty", difficulity);
		addQuotedField(builder, "filter", filter);
		addField(builder, "page", page);
		addField(builder, "partition", partition);
		addField(builder, "region", region);
		addField(builder, "serverSlug", serverSlug);
		addField(builder, "size", size);
		addField(builder, "metric", metric.getName());
		addField(builder, "includeCombatantInfo", includeCombatantInfo);
		addQuotedField(builder, "className", className.getClassName());
		addQuotedField(builder, "specName", specName.getSpec());
		addField(builder, "covenantID", covenantID != null ? covenantID.getId() : -1);
		addField(builder, "soulbindID", soulbindID);

		builder.append(")");
		return builder.toString();
	}

	public String getBracket() {
		return bracket;
	}

	public void setBracket(String bracket) {
		this.bracket = bracket;
	}

	public Difficulty getDifficulity() {
		return difficulity;
	}

	public void setDifficulity(Difficulty difficulity) {
		this.difficulity = difficulity;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getPartition() {
		return partition;
	}

	public void setPartition(String partition) {
		this.partition = partition;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getServerSlug() {
		return serverSlug;
	}

	public void setServerSlug(String serverSlug) {
		this.serverSlug = serverSlug;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Metric getMetric() {
		return metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	public boolean isIncludeCombatantInfo() {
		return includeCombatantInfo;
	}

	public void setIncludeCombatantInfo(boolean includeCombatantInfo) {
		this.includeCombatantInfo = includeCombatantInfo;
	}

	public ClassSpec getClassName() {
		return className;
	}

	public void setClassName(ClassSpec className) {
		this.className = className;
	}

	public ClassSpec getSpecName() {
		return specName;
	}

	public void setSpecName(ClassSpec specName) {
		this.specName = specName;
	}

	public Covenant getCovenantID() {
		return covenantID;
	}

	public void setCovenantID(Covenant covenantID) {
		this.covenantID = covenantID;
	}

	public Soulbind getSoulbindID() {
		return soulbindID;
	}

	public void setSoulbindID(Soulbind soulbindID) {
		this.soulbindID = soulbindID;
	}
}
