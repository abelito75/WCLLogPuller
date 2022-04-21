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

/**
 * This workflow is to find a singular log. The mentality behind it is 
 * attempting to find a single trinket or a person using a niche trinket
 *
 * @author Abelito75
 *
 */
public abstract class FindLogWorkflow extends AbstractWorkFlow {

	/**
	 * The log we found
	 */
	private LogData log;
	
	/**
	 * Create this
	 * @param name What the file will be named
	 */
	public FindLogWorkflow(String name) {
		super(name);
	}

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
		writeToFile(log);
	}
	

	@Override
	protected boolean logGatheredHook(LogData dataUnit) {
		log = dataUnit;
		return true;
	}
	
	/**
	 * The the data to file
	 */
	public void writeToFile(LogData dataUnit) {
		String toWrite = "Couldn't find a log";
		if(log != null) {
			toWrite = log.getValue("wclLink").toString();
		}
		
		try {
			Files.writeString(Paths.get(name + ".csv"), toWrite);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
