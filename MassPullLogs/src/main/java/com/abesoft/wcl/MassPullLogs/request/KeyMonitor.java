/**
 * 
 */
package com.abesoft.wcl.MassPullLogs.request;

import java.io.UnsupportedEncodingException;

import org.apache.http.auth.AuthenticationException;

import com.abesoft.wcl.MassPullLogs.JsonLib;
import com.fasterxml.jackson.databind.JsonNode;

/**
 *	Attempts to do some very very terrible key monitoring and querying to make things not be bad for weak API keys
 *
 * @author Abelito75
 *
 */
public class KeyMonitor {

	private static final String query = "{rateLimitData{limitPerHour, pointsSpentThisHour, pointsResetIn}}";
	private static KeyMonitor watcher;

	public static KeyMonitor getMonitor() {
		if (watcher == null) {
			// synchronized block to remove overhead
			synchronized (KeyMonitor.class) {
				if (watcher == null) {
					// if instance is null, initialize
					watcher = new KeyMonitor();
				}

			}
		}
		return watcher;
	}
	
	private int limit;
	
	private int currentPointsUsed;
	private int requests;
	private int resetsIn;
	
	private int pointsPerRequest;
	
	private KeyMonitor() {
		limit = 0;
		
		currentPointsUsed = -1;
		requests = 0;
		resetsIn = 0;
		
		pointsPerRequest = 5;
		
		gatherInfo();
	}

	/**
	 * Each call should fire this off to increase the points spent. 
	 * Every handful of requests we will get the actual points and fix what we have
	 * The assumption made with this call should be a rich blend meaning we are over estimating 
	 */
	public void requestFired() {
		requests += 1;
		currentPointsUsed += pointsPerRequest;
		
		if(requests != 0 && requests % 20 == 0) {
			gatherInfo();
		}
		
		maybeSleep();
	}

	public void gatherInfo() {
		
		RateLimitRequest request = new RateLimitRequest();
		try {
			request.buildRequest(query);
			request.setAuth();
			request.fireRequest();
			
			JsonNode root = request.getJSON();
			
			JsonNode rateLimitData = JsonLib.travelDownTree(root, "data/rateLimitData");
			
			limit = rateLimitData.get("limitPerHour").asInt();
			
			int resetsInNow = rateLimitData.get("pointsResetIn").asInt();
			
			int realPointsSpent = rateLimitData.get("pointsSpentThisHour").asInt();
			
			// this is some `lets figure out how far off and adjust to that math`
			// currentPointsUsed only = -1 on init call
			// if resetsInNow > resetsIn then points have reset don't remath
			if(currentPointsUsed != -1 && resetsInNow < resetsIn) {
				double pointRatio = realPointsSpent / ((double) currentPointsUsed);
				pointsPerRequest = (int) Math.round(pointsPerRequest * pointRatio);
			}
			
			resetsIn = resetsInNow;
			currentPointsUsed = realPointsSpent;
			
		} catch (UnsupportedEncodingException | AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Determine if we need to sleep or not
	 */
	private void maybeSleep() {
		// if we are super close to our limit
		float bufferedPoints = currentPointsUsed * 1.1f;
		if(bufferedPoints >= limit) {
			try {
				System.out.println("Going to sleep for " + resetsIn + " seconds. So we can reset the API key's");
				Thread.sleep(resetsIn);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
