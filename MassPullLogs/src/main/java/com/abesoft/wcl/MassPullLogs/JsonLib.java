package com.abesoft.wcl.MassPullLogs;

import java.util.stream.StreamSupport;

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
	
	/**
	 * Takes a root node and travels to data/entries/ then sums all the "total" fields
	 * @param root
	 * @return
	 */
	public static int totalEntriesFromRoot(JsonNode root) {
		return totalNodes(travelDownTree(root, "data/entries"), "total");
	}
	
	public static int totalNodes(JsonNode root, String field) {
		return StreamSupport.stream(root.spliterator(), true).map(node -> node.get(field).asInt()).reduce(0, Integer::sum);
	}
	
}
