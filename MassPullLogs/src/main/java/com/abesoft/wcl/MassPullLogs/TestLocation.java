package com.abesoft.wcl.MassPullLogs;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.auth.AuthenticationException;

import com.abesoft.wcl.MassPullLogs.data.LogData;
import com.abesoft.wcl.MassPullLogs.framework.App;
import com.abesoft.wcl.MassPullLogs.gui.scenes.ExampleScene;
import com.abesoft.wcl.MassPullLogs.request.constants.Boss;
import com.abesoft.wcl.MassPullLogs.request.constants.ClassSpec;
import com.abesoft.wcl.MassPullLogs.request.constants.Covenant;
import com.abesoft.wcl.MassPullLogs.request.constants.DataType;
import com.abesoft.wcl.MassPullLogs.request.constants.Difficulty;
import com.abesoft.wcl.MassPullLogs.request.constants.Metric;
import com.abesoft.wcl.MassPullLogs.request.constants.ViewType;
import com.abesoft.wcl.MassPullLogs.request.fragments.CharacterRankingsFragment;
import com.abesoft.wcl.MassPullLogs.request.fragments.TableFragment;
import com.abesoft.wcl.MassPullLogs.workflow.DataFetchWorkFlow;
import com.abesoft.wcl.MassPullLogs.workflow.FindLogWorkflow;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Hello world!
 *
 */
public class TestLocation {

	public static void main(String[] args) throws IOException, InterruptedException, AuthenticationException {
		
		App app = App.getApp();
		app.getController().generateMonitor();
		app.getGui().setContent(new ExampleScene().getPanel());
		//testFind();
		testDataQuery();
		
	}
	
	private static void testFind() throws IOException {
		FindLogWorkflow findFlow = new FindLogWorkflow("TestFind") {

			@Override
			public CharacterRankingsFragment generateFragment() {
				CharacterRankingsFragment toGet = new CharacterRankingsFragment();
				toGet.setClassName(ClassSpec.MONK_WINDWALKER);
				toGet.setSpecName(ClassSpec.MONK_WINDWALKER);
				toGet.setMetric(Metric.DPS);
				toGet.setDifficulity(Difficulty.HEROIC);
				toGet.setCovenantID(Covenant.VENTHYR);
				toGet.setIncludeCombatantInfo(false);
				return toGet;
			}
		};
		
		List<Boss> bossesToQuery = Arrays.asList(Boss.values()).stream().filter(boss -> boss != Boss.VIGILANT_GUARDIAN)
				.toList();
		findFlow.setBosses(bossesToQuery);
		findFlow.find();
	}
	
	private static void testDataQuery() throws IOException {
		Date start = new Date();
		
		DataFetchWorkFlow flow = new DataFetchWorkFlow("ELTData") {

			@Override
			public CharacterRankingsFragment generateFragment() {
				CharacterRankingsFragment toGet = new CharacterRankingsFragment();
				toGet.setClassName(ClassSpec.MONK_WINDWALKER);
				toGet.setSpecName(ClassSpec.MONK_WINDWALKER);
				toGet.setMetric(Metric.DPS);
				toGet.setDifficulity(Difficulty.MYTHIC);
				toGet.setIncludeCombatantInfo(false);
				return toGet;
			}

			@Override
			public List<TableFragment> generateFragmentForLog(LogData data) {
				List<Integer> fightID = Arrays.asList(Integer.parseInt(data.getFightID()));
				int sourceID = Integer.parseInt(data.getSourceID());

				TableFragment elt = new TableFragment("ELT");
				elt.setFightIDs(fightID);
				elt.setSourceID(sourceID);
				elt.setDataType(DataType.DAMAGE_DONE);
				elt.setViewBy(ViewType.ABILITY);
				elt.setAbilityID(335913);

				return Arrays.asList(elt);
			}

			@Override
			public void parseData(LogData data) {
				Map<String, JsonNode> tables = data.getTables();
				
				JsonNode statsNode = tables.get("ELT");
				JsonNode entries = JsonLib.travelDownTree(statsNode, "data/entries");
				
				int hitCount = 0;
				int missCount = 0;
				
				for(JsonNode node: entries) {
					hitCount += node.get("hitCount").asInt();
					missCount += node.get("missCount").asInt();
				}
				
				int total = hitCount + missCount;
				double hitRatio = (hitCount / ((double) total));
				double missRatio = (missCount / ((double) total));
				
				double formatedHitRatio = Math.round(hitRatio * 100) / 100d;
				double formatedMissRatio = Math.round(missRatio * 100) / 100d;
				
				data.setFieldValue("hitCount", hitCount, true, "Hit Count");
				data.setFieldValue("missCount", missCount, true, "Miss  Count");
				data.setFieldValue("totalAttempts", total, true, "Total Count");
				data.setFieldValue("hitRatio", formatedHitRatio, true, "Hit Ratio");
				data.setFieldValue("missRatio", formatedMissRatio, true, "Miss Ratio");
			}
		};

		List<Boss> bossesToQuery = Arrays.asList(Boss.values()).stream().filter(boss -> boss != Boss.VIGILANT_GUARDIAN)
				.toList();
		flow.setBosses(bossesToQuery);
		flow.run(2);
		Date endDate = new Date();
		
		System.out.println("Duration of test: " + timeDifference(start,endDate));
		
	}

	private static String timeDifference(Date startDate, Date endDate) {
		long end = endDate.getTime();
		long start = startDate.getTime();

		double diffTime = (end - start) / 1000.0;

		return diffTime + " Seconds";
	}
}
