/**
 * 
 */
package com.abesoft.wcl.MassPullLogs.request.monitor;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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
	
	private AtomicInteger limit;
	private AtomicInteger currentPointsUsed;
	private AtomicInteger resetsIn;
	private AtomicInteger pointsPerRequest;
	private AtomicInteger totalRequests;
	private AtomicInteger requests;
	private AtomicLong lastRefreshTime;
	private boolean goodFetch;
	
	public KeyMonitor() {
		limit = new AtomicInteger(0);
		currentPointsUsed = new AtomicInteger(-5);
		resetsIn = new AtomicInteger(0);
		pointsPerRequest = new AtomicInteger(5);
		totalRequests = new AtomicInteger(0);
		requests = new AtomicInteger(0);
		lastRefreshTime = new AtomicLong(System.currentTimeMillis());
		goodFetch = false;

		
		gatherInfo(currentPointsUsed.get());
	}

	/**
	 * Each call should fire this off to increase the points spent. 
	 * Every handful of requests we will get the actual points and fix what we have
	 * The assumption made with this call should be a rich blend meaning we are over estimating 
	 */
	public void requestFired() {
		int tRequests = totalRequests.getAndIncrement();
		int cRequests = requests.getAndIncrement();
		int cPoints = currentPointsUsed.getAndAdd(pointsPerRequest.get());
		
		
		manageRequests(cRequests);
		
		if(tRequests != 0 && tRequests % 100 == 0) {
			gatherInfo(cPoints);
		}
		
		maybeSleep(cPoints);
	}

	/**
	 * Manage max requests per minute 
	 * The actual limit of this is 600 requests per minute but we play it safe with 500
	 * The cooldown is only a minute so its not the end of the world IMO
	 */
	private void manageRequests(int currentRequests) {
		long currentTime = System.currentTimeMillis();
		if(currentRequests >= 500) {
			long waitTime = currentTime - lastRefreshTime.get();
			try {
				System.out.println("sleeping due to request spam");
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// get current time again
			lastRefreshTime.set(System.currentTimeMillis());
			requests.set(0);
		}
		
		if(currentTime > lastRefreshTime.get() + second) {
			lastRefreshTime.set(currentTime);
			requests.set(0);
		}
		
	}

	public void gatherInfo(int cPoints) {
		
		RateLimitRequest request = new RateLimitRequest();
		try {
			request.buildRequest(query);
			request.fireRequest();
			
			JsonNode root = request.getJSON();
			
			JsonNode rateLimitData = JsonLib.travelDownTree(root, "data/rateLimitData");
			
			limit.set(rateLimitData.get("limitPerHour").asInt());
			
			int resetsInNow = rateLimitData.get("pointsResetIn").asInt();
			
			int realPointsSpent = rateLimitData.get("pointsSpentThisHour").asInt();
			
			// this is some `lets figure out how far off and adjust to that math`
			// currentPointsUsed only = -1 on init call
			// if resetsInNow > resetsIn then points have reset don't remath
			if(cPoints != -1 && resetsInNow < resetsIn.get()) {
				double pointRatio = realPointsSpent / ((double) cPoints);
				pointsPerRequest.set((int) Math.round(pointsPerRequest.get() * pointRatio));
			}
			
			resetsIn.set(resetsInNow);
			currentPointsUsed.set(realPointsSpent);
			goodFetch = true;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Determine if we need to sleep or not
	 */
	private void maybeSleep(int cPoints) {
		// if we are super close to our limit
		float bufferedPoints = cPoints * 1.1f;
		if(bufferedPoints >= limit.get()) {
			try {
				System.out.println("Going to sleep for " + resetsIn + " seconds. So we can reset the API key's");
				Thread.sleep(resetsIn.get());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public boolean isGoodFetch() {
		return goodFetch;
	}
	
}
