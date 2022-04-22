package com.abesoft.wcl.MassPullLogs;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.auth.AuthenticationException;

import com.abesoft.wcl.MassPullLogs.data.LogData;
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
public class App {

	public static void main(String[] args) throws IOException, InterruptedException, AuthenticationException {
		
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
		DataFetchWorkFlow flow = new DataFetchWorkFlow("WWFOData") {

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

			@Override
			public List<TableFragment> generateFragmentForLog(LogData data) {
				List<Integer> fightID = Arrays.asList(Integer.parseInt(data.getFightID()));
				int sourceID = Integer.parseInt(data.getSourceID());

				TableFragment stats = new TableFragment("stats");
				stats.setFightIDs(fightID);
				stats.setDataType(DataType.SUMMARY);
				stats.setSourceID(sourceID);

				TableFragment casts = new TableFragment("casts");
				casts.setFightIDs(fightID);
				casts.setDataType(DataType.CASTS);
				casts.setViewBy(ViewType.SOURCE);
				casts.setViewOptions(0);
				casts.setFilterExpression("source.owner.name = \\\"" + data.getPlayerName() + "\\\"");
				casts.setAbilityID(330898);

				TableFragment summons = new TableFragment("summons");
				summons.setFightIDs(fightID);
				summons.setDataType(DataType.SUMMONS);
				summons.setSourceID(sourceID);
				summons.setAbilityID(327004);
				summons.setViewBy(ViewType.SOURCE);

				return Arrays.asList(stats, casts, summons);
			}

			@Override
			public void parseData(LogData data) {
				int haste = 0;
				int fofCasts = 0;
				int foSummons = 0;

				Map<String, JsonNode> tables = data.getTables();
				
				JsonNode statsNode = tables.get("stats");
				JsonNode hasteNode = JsonLib.travelDownTree(statsNode, "data/combatantInfo/stats/Haste/max");
				if(hasteNode != null) {
					haste = hasteNode.asInt();

				}
				
				JsonNode castsNode = tables.get("casts");
				fofCasts = JsonLib.totalEntriesFromRoot(castsNode);

				JsonNode summonsNode = tables.get("summons");
				foSummons = JsonLib.totalEntriesFromRoot(summonsNode);

				data.setFieldValue("haste", haste, true, "Haste");
				data.setFieldValue("fofCasts", fofCasts, true, "FoF Casts");
				data.setFieldValue("foSummons", foSummons, true, "Clones");
			}
		};

		List<Boss> bossesToQuery = Arrays.asList(Boss.values()).stream().filter(boss -> boss != Boss.VIGILANT_GUARDIAN)
				.toList();
		flow.setBosses(bossesToQuery);
		flow.run(2);
	}

	private static String timeDifference(Date startDate, Date endDate) {
		long end = endDate.getTime();
		long start = startDate.getTime();

		double diffTime = (end - start) / 1000.0;

		return diffTime + " Seconds";
	}
}
