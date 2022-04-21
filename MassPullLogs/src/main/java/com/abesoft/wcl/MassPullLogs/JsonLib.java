package com.abesoft.wcl.MassPullLogs;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonLib {

	private JsonLib() {}
	
	public static JsonNode travelDownTree(JsonNode root, String path) {
		if(root == null) {
			return null;
		}
		
		if(path == null) {
			return root;
		}
		
		String[] howToTravel = path.split("/");
		JsonNode toReturn = root;
		for(String jump : howToTravel) {
			toReturn = toReturn.get(jump);
			if(toReturn == null) {
				break;
			}
		}
		
		return toReturn;
	}
	
}
