package com.abesoft.wcl.MassPullLogs.workflow;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.apache.http.auth.AuthenticationException;

import com.abesoft.wcl.MassPullLogs.JsonLib;
import com.abesoft.wcl.MassPullLogs.data.DefaultField;
import com.abesoft.wcl.MassPullLogs.data.LogData;
import com.abesoft.wcl.MassPullLogs.output.CSVOutput;
import com.abesoft.wcl.MassPullLogs.request.GenericBuilders;
import com.abesoft.wcl.MassPullLogs.request.GenericGraphQLRequest;
import com.abesoft.wcl.MassPullLogs.request.constants.Boss;
import com.abesoft.wcl.MassPullLogs.request.fragments.TableFragment;
import com.fasterxml.jackson.databind.JsonNode;

public abstract class DataFetchWorkFlow extends AbstractWorkFlow {

	/**
	 * List of logs, this should be at max 100 at a time 
	 */
	private List<LogData> logs;
	/**
	 * Our cute little file writer
	 */
	private CSVOutput output;
	/**
	 * You get 4 threads by default to run this query
	 */
	private int threads;
	
	/**
	 * Cute little creator
	 * @param name The name the file will be
	 * @throws IOException for creation of the FileWriter
	 */
	public DataFetchWorkFlow(String name) throws IOException {
		super(name);
		threads = 4;
		logs = new ArrayList<>();
		output = new CSVOutput(new File(name + ".csv"));
	}

	/**
	 * The queries you want query for a log
	 * @param data the log data, used for setting sourceID and what not
	 * @return A list of fragments to query against
	 */
	public abstract List<TableFragment> generateFragmentForLog(LogData data);

	/**
	 * This method will parse data from a log. You will have the WCL data tables you made in data
	 * @param data the log to extract and store data in/from
	 */
	public abstract void parseData(LogData data);
	
	/**
	 * Run the data dive
	 * @param pages How many WCL pages you want to query. Each page is 100 logs.
	 */
	public void run(int pages) {
		//System.out.println("Bosses we are attempting to query: " + bosses.stream().map(e -> e.getName()).collect(Collectors.joining(", ")));
		//System.out.println("Desired Logs Per Boss: " + convertPageToAmount(pages));
		for (Boss boss : bosses) {
			//System.out.println("Starting parsing for boss: " + boss.getName());

			for (int page : IntStream.rangeClosed(1, pages).boxed().toList()) {
				//System.out.println("Attempting to get " + convertPageToAmount(page - 1) + " to " + convertPageToAmount(page) + " logs for " + boss.getName());
				logs.clear();
				int attempts = 0;
				while (attempts < 2 && !getLogs(page, boss)) {
					attempts++;
				}

				if (attempts == 2) {
					// we failed to get the logs so we just move on... sad days
					//System.out.println("Attempting to get logs " + convertPageToAmount(page - 1) + " to " + convertPageToAmount(page) + " failed for boss " + boss.getName());
					continue;
				}

				//System.out.println("Number of Logs Gathered: " + logs.size());

				removeAnonymous();
				
				// divide up into segments
				// we are gonna give each thread n logs. If there are less than 25 logs then only 1 thread will handle it
				List<List<LogData>> segmentData = new ArrayList<>();
				int i = 0;
				for(LogData data : logs) {
					int listToUse = i / (100/threads);
					if(segmentData.size() <= listToUse) {
						List<LogData> listData = new ArrayList<>();
						segmentData.add(listData);
					}
					segmentData.get(listToUse).add(data);
					i++;
				}
				

				//System.out.println("None Anonymous Logs: " + logs.size());
				CountDownLatch startSignal = new CountDownLatch(1);
				CountDownLatch endSignal = new CountDownLatch(segmentData.size());
				
				
				
				for (List<LogData> data : segmentData) {
					new Thread(new Worker(data, startSignal, endSignal)).start();
				}
				startSignal.countDown();
				try {
					endSignal.await();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				//System.out.println("Logs fully processed " + logs.size() + ". Moving onto next bunch");
				
			}
		}
		try {
			output.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class Worker implements Runnable {
		
		private List<LogData> segment;
		private CountDownLatch start;
		private CountDownLatch end;
		
		public Worker(List<LogData> data, CountDownLatch start, CountDownLatch end) {
			segment = data;
			this.start = start;
			this.end = end;
		}

		@Override
		public void run() {
			try {
				start.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (LogData data : segment) {
				int attempts = 0;
				while (attempts < 2 && !runWorkflow(data)) {
					attempts++;
				}

				if (attempts == 2) {
					//System.out.println("Attempting to get data for " + data.getValue("wclLink") + " failed.");
					continue;
				}

				writeToFile(data);
			}
			end.countDown();
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
				//System.out.println("Throwing out " + data.getValue("wclLink") + " due to in ability to get sourceID");
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
	 * The data is stored in petIDs and sourceID
	 * To access petIDs requires a nasty untyped cast
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws AuthenticationException
	 */
	public void getSourceID(LogData data) throws UnsupportedEncodingException, AuthenticationException {
		String query = GenericBuilders.buildSourceIDsQuery(data);

		GenericGraphQLRequest request = new GenericGraphQLRequest();
		request.buildRequest(query);
		request.fireRequest();
		JsonNode root = request.getJSON();
		JsonNode actors = JsonLib.travelDownTree(root, "data/reportData/report/masterData/actors");

		StreamSupport.stream(actors.spliterator(), false)
				.filter(e -> e.get("name").asText().equals(data.getPlayerName())).findFirst()
				.ifPresent(e -> data.setSourceID(e.get("id").asText()));

		List<JsonNode> pets = StreamSupport.stream(actors.spliterator(), false)
				.filter(e -> e.get("petOwner").asText().equals(data.getSourceID())).toList();

		Map<String, List<String>> petIDs = new HashMap<>();

		data.setFieldValue(DefaultField.PET_IDS.getOutputName(), petIDs, false);
		data.setPetIDs(petIDs);

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
	
	/**
	 * get the name... duh
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	public void setThreads(int threads) {
		this.threads = threads;
	}
	
	public int getThreads() {
		return threads;
	}
	
	
	
	/**
	 * The data query
	 * @param dataUnit the log we are acting on
	 * @throws AuthenticationException
	 * @throws UnsupportedEncodingException
	 */
	public void getData(LogData dataUnit) throws AuthenticationException, UnsupportedEncodingException {

		List<TableFragment> fragments = generateFragmentForLog(dataUnit);
		
		String query = GenericBuilders.buildDataQuery(dataUnit, fragments);

		GenericGraphQLRequest request = new GenericGraphQLRequest();
		request.buildRequest(query);
		request.fireRequest();
		JsonNode root = request.getJSON();
		JsonNode tableRoot = JsonLib.travelDownTree(root, "data/reportData/report");

		Map<String, JsonNode> dataMapping = new LinkedHashMap<>();

		for (TableFragment frag : fragments) {
			dataMapping.put(frag.getName(), tableRoot.get(frag.getName()));
		}
		
		dataUnit.setTables(dataMapping);
	}

	@Override
	protected boolean logGatheredHook(LogData dataUnit) {
		logs.add(dataUnit);
		return false;
	}

	/**
	 * Writes a single log to a file
	 * This does instantly flush. So its not super optimal
	 * @param dataUnit log to write
	 */
	public synchronized void writeToFile(LogData dataUnit) {
		try {
			output.writeRecord(dataUnit);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
