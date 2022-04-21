package com.abesoft.wcl.MassPullLogs.request.fragments;

import java.util.List;

import com.abesoft.wcl.MassPullLogs.request.constants.AbstractConstant;
import com.abesoft.wcl.MassPullLogs.request.constants.Boss;
import com.abesoft.wcl.MassPullLogs.request.constants.ClassSpec;
import com.abesoft.wcl.MassPullLogs.request.constants.Difficulty;

public abstract class AbstractFragment {

	public abstract String buildFragment();

	protected boolean addField(StringBuilder toBuildOn, String fieldName, String fieldValue) {
		if (fieldValue == null || fieldValue.isBlank()) {
			return false;
		}
		toBuildOn.append(fieldName);
		toBuildOn.append(":");
		toBuildOn.append(fieldValue);
		toBuildOn.append(" ");
		return true;
	}

	protected boolean addQuotedField(StringBuilder toBuildOn, String fieldName, String fieldValue) {
		if (fieldValue == null || fieldValue.isBlank()) {
			return false;
		}
		toBuildOn.append(fieldName);
		toBuildOn.append(":");
		toBuildOn.append("\"");
		toBuildOn.append(fieldValue);
		toBuildOn.append("\"");
		toBuildOn.append(" ");
		return true;
	}

	protected boolean addField(StringBuilder toBuildOn, String fieldName, int fieldValue) {
		if (fieldValue < 0) {
			return false;
		}
		toBuildOn.append(fieldName);
		toBuildOn.append(":");
		toBuildOn.append(fieldValue);
		toBuildOn.append(" ");
		return true;
	}

	protected boolean addField(StringBuilder toBuildOn, String fieldName, boolean fieldValue) {
		toBuildOn.append(fieldName);
		toBuildOn.append(":");
		toBuildOn.append(fieldValue);
		toBuildOn.append(" ");
		return true;
	}

	protected boolean addField(StringBuilder toBuildOn, String fieldName, float fieldValue) {
		if (fieldValue <= 0) {
			return false;
		}

		toBuildOn.append(fieldName);
		toBuildOn.append(":");
		toBuildOn.append(fieldValue);
		toBuildOn.append(" ");
		return true;
	}

	protected boolean addField(StringBuilder toBuildOn, String fieldName, List<?> fieldValue) {
		if (fieldValue == null || fieldValue.isEmpty()) {
			return false;
		}

		String toInsert = "[";

		for (Object value : fieldValue) {
			toInsert += value;
		}

		toInsert += "]";

		toBuildOn.append(fieldName);
		toBuildOn.append(":");
		toBuildOn.append(toInsert);
		toBuildOn.append(" ");
		return true;
	}

	public void addField(StringBuilder builder, String name, AbstractConstant constant) {
		if (constant == null) {
			return;
		}

		String constantValue = constant.getName();
		addField(builder, name, constantValue);
	}

	public void addField(StringBuilder builder, String name, ClassSpec classSpec) {
		if (classSpec == null) {
			return;
		}

		String constantValue = classSpec.getClassName();

		addField(builder, name, constantValue);
	}

	public void addField(StringBuilder builder, String name, Boss boss) {
		if (boss == null) {
			return;
		}

		int constantValue = boss.getID();

		addField(builder, name, String.valueOf(constantValue));
	}

	public void addField(StringBuilder builder, String name, Difficulty boss) {
		if (boss == null) {
			return;
		}

		int constantValue = boss.getWclNumber();

		addField(builder, name, String.valueOf(constantValue));
	}
}
