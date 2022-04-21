package com.abesoft.wcl.MassPullLogs.workflow;

import java.io.IOException;
import java.util.List;

import com.abesoft.wcl.MassPullLogs.data.LogData;
import com.abesoft.wcl.MassPullLogs.request.constants.Boss;

public class FindLogWorkflow extends AbstractWorkFlow {

	private String name;
	private List<Boss> bosses;
	
	private LogData logs;
	
	public FindLogWorkflow(String name) throws IOException {
		this.name = name;
	}

	public void setBosses(List<Boss> bosses) {
		this.bosses = bosses;
	}

	// now we just do top log requests till we find the data we desire
	
}
