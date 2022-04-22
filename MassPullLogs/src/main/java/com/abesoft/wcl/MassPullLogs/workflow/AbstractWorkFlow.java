package com.abesoft.wcl.MassPullLogs.workflow;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.abesoft.wcl.MassPullLogs.JsonLib;
import com.abesoft.wcl.MassPullLogs.data.DefaultField;
import com.abesoft.wcl.MassPullLogs.data.LogData;
import com.abesoft.wcl.MassPullLogs.request.GenericGraphQLRequest;
import com.abesoft.wcl.MassPullLogs.request.constants.Boss;
import com.abesoft.wcl.MassPullLogs.request.fragments.CharacterRankingsFragment;
import com.fasterxml.jackson.databind.JsonNode;

public abstract class AbstractWorkFlow {
	
	/**
	 * Name of file to save to
	 */
	protected String name;
	/**
	 * List of bosses to query
	 */
	protected List<Boss> bosses;
	
	
	public AbstractWorkFlow(String name) {
		this.name = name;
		bosses = new ArrayList<>();
	}
	
	/**
	 * Set bosses to query
	 * @param bosses bosses to query
	 */
	public void setBosses(List<Boss> bosses) {
		this.bosses = bosses;
	}
	
	
	public abstract void writeToFile(LogData dataUnit);
	
	
	/**
	 * DO NOT include the PAGE number you want. THIS IS HANDLED IN RUN
	 * 
	 * @return
	 */
	public abstract CharacterRankingsFragment generateFragment();
	
	/**
	 * Queries for top logs from requested fragment Will generate LogData holders
	 * that contain
	 * <ul>
	 * <li>Character name</li>
	 * <li>Character Class</li>
	 * <li>Character Spec</li>
	 * <li>Report Code</li>
	 * <li>Fight ID</li>
	 * </ul>
	 * 
	 * @param toGet
	 */
	public boolean getLogs(int page, Boss boss) {
		CharacterRankingsFragment toGet = generateFragment();
		toGet.setPage(page);
		String queryHeader = "{worldData{encounter(id: ";
		String queryEnder = "){id,name," + toGet.buildFragment() + "}}}";

		String query = queryHeader + boss.getID() + queryEnder;

		GenericGraphQLRequest request = new GenericGraphQLRequest();
		try {
			request.buildRequest(query);
			request.fireRequest();
			JsonNode node = request.getJSON();
			

			JsonNode encounter = JsonLib.travelDownTree(node, "data/worldData/encounter");
			JsonNode rankings = JsonLib.travelDownTree(encounter, "characterRankings/rankings");
			for (JsonNode rank : rankings) {
				LogData dataUnit = new LogData();

				dataUnit.disableAllFields();
				dataUnit.getField(DefaultField.BOSS_NAME.getOutputName()).setOutputField(true);
				dataUnit.getField(DefaultField.PLAYER_NAME.getOutputName()).setOutputField(true);

				dataUnit.setBossName(encounter.get("name").asText());

				dataUnit.setPlayerName(rank.get("name").asText());
				dataUnit.setPlayerClass(rank.get("class").asText());
				dataUnit.setPlayerSpec(rank.get("spec").asText());

				JsonNode reportNode = rank.get("report");
				dataUnit.setReportCode(reportNode.get("code").asText());
				dataUnit.setFightID(reportNode.get("fightID").asText());
				dataUnit.outputWCLLink();
				
				boolean breakerHook = logGatheredHook(dataUnit);
				if(breakerHook) {
					return breakerHook;
				}
			}
		} catch (UnsupportedEncodingException e) {
			return false;
		}
		return true;
	}

	/**
	 * Allows for post processing of a logs first gather
	 * @return true if you want to break out of the log gathering process
	 */
	protected abstract boolean logGatheredHook(LogData dataUnit);
	
	
	

}
