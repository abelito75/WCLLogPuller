package com.abesoft.wcl.MassPullLogs;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.StreamSupport;

import org.apache.http.auth.AuthenticationException;

import com.abesoft.wcl.MassPullLogs.data.DefaultField;
import com.abesoft.wcl.MassPullLogs.data.LogData;
import com.abesoft.wcl.MassPullLogs.request.GenericGraphQLRequest;
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
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) throws IOException, InterruptedException, AuthenticationException {

//		App app = new App();
//		app.generateDataSets();
//		app.gatherTopLogs();
//		app.grabSourceIDs();;
//		app.cleanData();
//		app.getData();
//		app.saveData();
//		Binding binding = new Binding();
//		binding.setVariable("foo", new Integer(2));
//		GroovyShell shell = new GroovyShell(binding);
//
//		Object value = shell.evaluate(new File("Scripty.groovy"));

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

				Map<String, JsonNode> tables = (Map<String, JsonNode>) data.getField("Tables").getValue();
				JsonNode statsNode = tables.get("stats").get("data").get("combatantInfo").get("stats");
				haste = statsNode.get("Haste").get("max").asInt();

				JsonNode castsNode = tables.get("casts").get("data").get("entries");
				for (JsonNode node : castsNode) {
					fofCasts += node.get("total").asInt();
				}

				JsonNode summonsNode = tables.get("summons").get("data").get("entries");
				for (JsonNode node : summonsNode) {
					foSummons += node.get("total").asInt();
				}

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

	public Map<Boss, List<LogData>> dataset;

	public void generateDataSets() {
		dataset = new LinkedHashMap<>();
	}

	public void gatherTopLogs() {
		for (Boss boss : Boss.values()) {
			if (boss == Boss.VIGILANT_GUARDIAN) {
				continue;
			}

			System.out.println("boss: " + boss.getName());

			List<LogData> units = new ArrayList<>();
			dataset.put(boss, units);

			for (int page = 1; page < 3; page++) {
				try {
					GenericGraphQLRequest request = new GenericGraphQLRequest();
					request.buildRequest(topLogsQuery(String.valueOf(boss.getID()), page));
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

						units.add(dataUnit);
					}

				} catch (UnsupportedEncodingException | AuthenticationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public String topLogsQuery(String bossID, int page) {
		String s = "{worldData{encounter(id:" + bossID + "){id,name,";

		CharacterRankingsFragment fragment = new CharacterRankingsFragment();
		fragment.setDifficulity(Difficulty.MYTHIC);
		fragment.setMetric(Metric.HPS);
		fragment.setSpecName(ClassSpec.PALADIN_HOLY);
		fragment.setClassName(ClassSpec.PALADIN_HOLY);
		fragment.setPage(page);

		s += fragment.buildFragment();
		s += "}}}";

		return s;
	}

	public void grabSourceIDs() {
		for (Entry<Boss, List<LogData>> info : dataset.entrySet()) {
			int i = 1;

			System.out.println("Current Boss: " + info.getKey().getName());

			for (LogData unit : info.getValue()) {

				if (i % 10 == 0) {
					System.out.println("Number parsed so far: " + i);
				}

				GenericGraphQLRequest request = new GenericGraphQLRequest();
				try {
					request.buildRequest(sourceIDsQuery(unit.getReportCode()));
					request.setAuth();
					request.fireRequest();
					JsonNode root = request.getJSON();

					JsonNode actors = root.get("data").get("reportData").get("report").get("masterData").get("actors");

					StreamSupport.stream(actors.spliterator(), false)
							.filter(e -> e.get("name").asText().equals(unit.getPlayerName())).findFirst()
							.ifPresent(e -> unit.setSourceID(e.get("id").asText()));
					i++;
				} catch (UnsupportedEncodingException | AuthenticationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public String sourceIDsQuery(String reportCode) {
		String s = "{reportData {report(code: \"" + reportCode
				+ "\") {masterData {actors(type: \"Player\") {id,name,server,}}}}}";
		return s;
	}

	public void cleanData() {
		for (Entry<Boss, List<LogData>> info : dataset.entrySet()) {
			info.getValue().removeIf(item -> item.getSourceID() == null);
		}
	}

	public void getData() {
		for (Entry<Boss, List<LogData>> info : dataset.entrySet()) {
			int i = 1;

			System.out.println("Current Boss: " + info.getKey().getName());

			for (LogData unit : info.getValue()) {
				if (i % 10 == 0) {
					System.out.println("Number parsed so far: " + i);
				}

				GenericGraphQLRequest request = new GenericGraphQLRequest();
				try {
					request.buildRequest(tableQuery(unit));
					request.setAuth();
					request.fireRequest();
					JsonNode root = request.getJSON();

					JsonNode report = root.get("data").get("reportData").get("report");

					JsonNode stats = report.get("stats");

					int haste = stats.get("data").get("combatantInfo").get("stats").get("Haste").get("max").asInt();
					unit.setFieldValue("hasteRating", haste, true, "Haste Rating");
					unit.setFieldValue("hastePer", (haste / 33.0), true, "Haste %");

					JsonNode buffs = report.get("buffs");
					int totalBuffs = 0;
					JsonNode buffAuras = buffs.get("data").get("auras");
					for (JsonNode aura : buffAuras) {
						totalBuffs += aura.get("totalUses").asInt();
					}

					unit.setFieldValue("totalEnms", totalBuffs, true, "ENMs");

					JsonNode summons = report.get("summons");
					JsonNode fTemp = summons.get("data").get("entries").get(0);

					unit.setFieldValue("clones", fTemp == null ? 0 : fTemp.get("total").asInt(), true, "Clones");

					i++;
				} catch (UnsupportedEncodingException | AuthenticationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
		}
	}

	public String tableQuery(LogData unit) {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("reportData{");
		builder.append("report(code:\"");
		builder.append(unit.getReportCode());
		builder.append("\"){");
		TableFragment stats = new TableFragment("stats");
		stats.addFightID(Integer.valueOf(unit.getFightID()));
		stats.setDataType(DataType.SUMMARY);
		stats.setSourceID(Integer.valueOf(unit.getSourceID()));
		TableFragment buffs = new TableFragment("buffs");
		buffs.addFightID(Integer.valueOf(unit.getFightID()));
		buffs.setDataType(DataType.BUFFS);
		buffs.setAbilityID(344008);
		buffs.setViewBy(ViewType.SOURCE);
		TableFragment summons = new TableFragment("summons");
		summons.addFightID(Integer.valueOf(unit.getFightID()));
		summons.setDataType(DataType.SUMMONS);
		summons.setSourceID(Integer.valueOf(unit.getSourceID()));
		summons.setAbilityID(327006);
		summons.setViewBy(ViewType.SOURCE);

		builder.append(stats.buildFragment());
		builder.append(",");
		builder.append(buffs.buildFragment());
		builder.append(",");
		builder.append(summons.buildFragment());
		builder.append("}");
		builder.append("}");
		builder.append("}");
		return builder.toString();
	}
}
