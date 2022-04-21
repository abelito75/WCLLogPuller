package com.abesoft.wcl.MassPullLogs.workflow;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.apache.http.auth.AuthenticationException;

import com.abesoft.wcl.MassPullLogs.data.DefaultField;
import com.abesoft.wcl.MassPullLogs.data.LogData;
import com.abesoft.wcl.MassPullLogs.output.CSVOutput;
import com.abesoft.wcl.MassPullLogs.request.GenericGraphQLRequest;
import com.abesoft.wcl.MassPullLogs.request.constants.Boss;
import com.abesoft.wcl.MassPullLogs.request.fragments.CharacterRankingsFragment;
import com.abesoft.wcl.MassPullLogs.request.fragments.TableFragment;
import com.fasterxml.jackson.databind.JsonNode;

public abstract class DataFetchWorkFlow extends AbstractWorkFlow {

	private String name;
	private List<Boss> bosses;

	private List<LogData> logs;

	private CSVOutput output;

	public DataFetchWorkFlow(String name) throws IOException {
		this.name = name;
		logs = new ArrayList<>();
		output = new CSVOutput(new File(name + ".csv"));
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

	public abstract List<TableFragment> generateFragmentForLog(LogData data);

	public abstract void parseData(LogData data);

	public void run(int pages) {
		System.out.println("Bosses we are attempting to query: "
				+ bosses.stream().map(e -> e.getName()).collect(Collectors.joining(", ")));
		System.out.println("Desired Logs Per Boss: " + convertPageToAmount(pages));
		for (Boss boss : bosses) {
			System.out.println("Starting parsing for boss: " + boss.getName());

			for (int page : IntStream.rangeClosed(1, pages).boxed().toList()) {
				System.out.println("Attempting to get " + convertPageToAmount(page - 1) + " to "
						+ convertPageToAmount(page) + " logs for " + boss.getName());
				logs.clear();
				int attempts = 0;
				while (attempts < 2 && !getLogs(page, boss)) {
					attempts++;
				}

				if (attempts == 2) {
					// we failed to get the logs so we just move on... sad days
					System.out.println("Attempting to get logs " + convertPageToAmount(page - 1) + " to "
							+ convertPageToAmount(page) + " failed for boss " + boss.getName());
					continue;
				}

				System.out.println("Number of Logs Gathered: " + logs.size());

				removeAnonymous();

				System.out.println("None Anonymous Logs: " + logs.size());
				Iterator<LogData> itty = logs.iterator();
				while (itty.hasNext()) {
					LogData data = itty.next();
					attempts = 0;
					while (attempts < 2 && !runWorkflow(data)) {
						attempts++;
					}

					if (attempts == 2) {
						System.out.println("Attempting to get data for " + data.getValue("wclLink") + " failed.");
						itty.remove();
						continue;
					}

					writeData(data);
				}
				System.out.println("Logs fully processed " + logs.size() + ". Moving onto next bunch");

			}
		}
		try {
			output.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Converts page to logs on a page... simple but DRY ideas
	 * 
	 * @param page which page are we on?
	 * @return page * 100
	 */
	private int convertPageToAmount(int page) {
		return page * 100;
	}

	/**
	 * An independent method for easy re-running
	 * 
	 * @param the data we are basing this run off of
	 * @return if the run was successful or not
	 */
	private boolean runWorkflow(LogData data) {
		try {
			getSourceID(data);
			if (isDirtyData(data)) {
				System.out.println("Throwing out " + data.getValue("wclLink") + " due to bad data");
				return false;
			}
			getData(data);
			parseData(data);
		} catch (Exception e) {
			return false;
		}
		return true;
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
	public boolean getLogs(int page, Boss boss) {
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

				logs.add(dataUnit);
			}
		} catch (AuthenticationException | UnsupportedEncodingException e) {
			return false;
		}
		return true;
	}

	/**
	 * These logs can't be used as there is no real way to get the sourceID for the
	 * player in question easily This might find someone actually named Anonymous
	 * who isn't anonymously logging but w/e we are talking about a 1% error rate at
	 * max (most likely)
	 */
	public void removeAnonymous() {
		logs.removeIf(log -> log.getPlayerName() == null || "Anonymous".equalsIgnoreCase(log.getPlayerName())
				|| log.getPlayerName().isBlank());
	}

	/**
	 * Gets the id for the player and all his pets
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws AuthenticationException
	 */
	public void getSourceID(LogData data) throws UnsupportedEncodingException, AuthenticationException {
		String queryHeader = "{reportData {report(code: \"";
		String queryEnder = "\"){masterData{actors{id,name,server,petOwner}}}}}";

		String query = queryHeader + data.getReportCode() + queryEnder;

		GenericGraphQLRequest request = new GenericGraphQLRequest();
		request.buildRequest(query);
		request.setAuth();
		request.fireRequest();
		JsonNode root = request.getJSON();
		JsonNode actors = root.get("data").get("reportData").get("report").get("masterData").get("actors");

		StreamSupport.stream(actors.spliterator(), false)
				.filter(e -> e.get("name").asText().equals(data.getPlayerName())).findFirst()
				.ifPresent(e -> data.setSourceID(e.get("id").asText()));

		List<JsonNode> pets = StreamSupport.stream(actors.spliterator(), false)
				.filter(e -> e.get("petOwner").asText().equals(data.getSourceID())).toList();

		Map<String, List<String>> petIDs = new HashMap<>();

		data.setFieldValue("petIDs", petIDs, false);

		pets.forEach(pet -> {
			String petName = pet.get("name").asText();
			List<String> currentPetIDs = petIDs.get(petName);
			if (currentPetIDs == null) {
				currentPetIDs = new ArrayList<>();
				petIDs.put(petName, currentPetIDs);
			}
			currentPetIDs.add(pet.get("id").asText());
		});

	}

	/**
	 * Remove any null sourceIDers
	 */
	public boolean isDirtyData(LogData data) {
		return data.getSourceID() == null || data.getSourceID().isBlank();
	}

	public List<LogData> getLogData() {
		return logs;
	}

	public String getName() {
		return name;
	}

	public void getData(LogData dataUnit) throws AuthenticationException, UnsupportedEncodingException {
		String queryHeader = "{reportData{report(code:\"";
		String queryMiddle = "\"){";
		String queryFooter = "}}}";

		List<TableFragment> fragments = generateFragmentForLog(dataUnit);

		String query = queryHeader + dataUnit.getReportCode() + queryMiddle;

		for (TableFragment frag : fragments) {
			query += frag.buildFragment();
		}

		query += queryFooter;

		GenericGraphQLRequest request = new GenericGraphQLRequest();
		request.buildRequest(query);
		request.setAuth();
		request.fireRequest();
		JsonNode root = request.getJSON();

		JsonNode tableRoot = root.get("data").get("reportData").get("report");

		Map<String, JsonNode> dataMapping = new LinkedHashMap<>();

		for (TableFragment frag : fragments) {
			dataMapping.put(frag.getName(), tableRoot.get(frag.getName()));
		}

		dataUnit.setFieldValue("Tables", dataMapping);

	}

	public void writeData(LogData dataUnit) {
		try {
			output.writeRecord(dataUnit);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
