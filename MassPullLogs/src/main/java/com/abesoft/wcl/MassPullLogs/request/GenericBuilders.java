package com.abesoft.wcl.MassPullLogs.request;

import java.util.List;

import com.abesoft.wcl.MassPullLogs.data.LogData;
import com.abesoft.wcl.MassPullLogs.request.constants.Boss;
import com.abesoft.wcl.MassPullLogs.request.fragments.CharacterRankingsFragment;
import com.abesoft.wcl.MassPullLogs.request.fragments.TableFragment;

public class GenericBuilders {
	private GenericBuilders() {
	}

	public static String buildTopLogsQuery(Boss boss, CharacterRankingsFragment fragment) {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("worldData{");
		builder.append("encounter(id: ");
		builder.append(boss.getID());
		builder.append("){");
		builder.append("id,");
		builder.append("name,");
		builder.append(fragment.buildFragment());
		builder.append("}");
		builder.append("}");
		builder.append("}");
		return builder.toString();
	}

	public static String buildSourceIDsQuery(LogData unit) {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("reportData{");
		builder.append("report(code: \"");
		builder.append(unit.getReportCode());
		builder.append("\"){");
		builder.append("masterData {");
		builder.append("actors{");
		builder.append("id,");
		builder.append("name,");
		builder.append("server,");
		builder.append("petOwner");
		builder.append("}");
		builder.append("}");
		builder.append("}");
		builder.append("}");
		builder.append("}");
		return builder.toString();
	}

	public static String buildDataQuery(LogData unit, List<TableFragment> fragment) {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("reportData{");
		builder.append("report(code: \"");
		builder.append(unit.getReportCode());
		builder.append("\"){");
		for (TableFragment frag : fragment) {
			builder.append(frag.getName());
			builder.append(": ");
			builder.append(frag.buildFragment());
			builder.append(" ");
		}
		builder.append("}");
		builder.append("}");
		builder.append("}");
		return builder.toString();
	}

}
