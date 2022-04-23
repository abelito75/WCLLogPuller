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
	private static long second = 60000;
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
	private int resetsIn;
	private int pointsPerRequest;
	private int totalRequests;
	private int requests;
	private long lastRefreshTime;
	
	private KeyMonitor() {
		limit = 0;
		currentPointsUsed = -1;
		resetsIn = 0;
		pointsPerRequest = 5;
		totalRequests = 0;
		requests = 0;
		lastRefreshTime = System.currentTimeMillis();

		
		gatherInfo();
	}

	/**
	 * Each call should fire this off to increase the points spent. 
	 * Every handful of requests we will get the actual points and fix what we have
	 * The assumption made with this call should be a rich blend meaning we are over estimating 
	 */
	public void requestFired() {
		totalRequests += 1;
		requests += 1;
		currentPointsUsed += pointsPerRequest;
		
		
		manageRequests();
		
		if(totalRequests != 0 && totalRequests % 20 == 0) {
			gatherInfo();
		}
		
		maybeSleep();
	}

	/**
	 * Manage max requests per minute 
	 * The actual limit of this is 600 requests per minute but we play it safe with 500
	 * The cooldown is only a minute so its not the end of the world IMO
	 */
	private void manageRequests() {
		long currentTime = System.currentTimeMillis();
		if(requests >= 500) {
			long waitTime = currentTime - lastRefreshTime;
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// get current time again
			currentTime = System.currentTimeMillis();
		}
		
		if(currentTime > lastRefreshTime + second) {
			lastRefreshTime = currentTime;
			requests = 0;
		}
		
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
