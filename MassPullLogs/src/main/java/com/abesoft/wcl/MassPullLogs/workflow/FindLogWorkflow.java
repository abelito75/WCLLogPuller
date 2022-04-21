package com.abesoft.wcl.MassPullLogs.workflow;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.http.auth.AuthenticationException;

import com.abesoft.wcl.MassPullLogs.data.DefaultField;
import com.abesoft.wcl.MassPullLogs.data.LogData;
import com.abesoft.wcl.MassPullLogs.request.GenericGraphQLRequest;
import com.abesoft.wcl.MassPullLogs.request.constants.Boss;
import com.abesoft.wcl.MassPullLogs.request.fragments.CharacterRankingsFragment;
import com.fasterxml.jackson.databind.JsonNode;

public abstract class FindLogWorkflow extends AbstractWorkFlow {

	private String name;
	private List<Boss> bosses;
	
	private LogData log;
	
	public FindLogWorkflow(String name) throws IOException {
		this.name = name;
	}

	public void setBosses(List<Boss> bosses) {
		this.bosses = bosses;
	}
	
	/**
	 * DO NOT include the PAGE number you want. THIS IS HANDLED IN RUN
	 * 
	 * @return
	 */
	public abstract CharacterRankingsFragment generateFragment();

	/**
	 * 10 pages - 1000 logs
	 */
	public void find() {
		find(10);
	}
	
	public void find(int pages) {
		FRUIT_LOOPS: for (Boss boss : bosses) {
			for (int page : IntStream.rangeClosed(1, pages).boxed().toList()) {
				getLogs(page, boss);
				if(log != null) {
					break FRUIT_LOOPS;
				}
			}
		}
		writeToFile();
	}
	
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
	public void getLogs(int page, Boss boss) {
		CharacterRankingsFragment toGet = generateFragment();
		toGet.setPage(page);
		String queryHeader = "{worldData{encounter(id: ";
		String queryEnder = "){id,name," + toGet.buildFragment() + "}}}";

		String query = queryHeader + boss.getID() + queryEnder;

		GenericGraphQLRequest request = new GenericGraphQLRequest();
		try {
			request.buildRequest(query);
			request.setAuth();

			request.fireRequest();
			JsonNode node = request.getJSON();
			JsonNode encounter = node.get("data").get("worldData").get("encounter");

			JsonNode rankings = encounter.get("characterRankings").get("rankings");
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

				log = dataUnit;
				break;
			}
		} catch (AuthenticationException | UnsupportedEncodingException e) {
		}
	}
	
	public void writeToFile() {
		String toWrite = "Couldn't find a log";
		if(log != null) {
			toWrite = log.getValue("wclLink").toString();
		}
		
		try {
			Files.writeString(Paths.get(name + ".txt"), toWrite);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
