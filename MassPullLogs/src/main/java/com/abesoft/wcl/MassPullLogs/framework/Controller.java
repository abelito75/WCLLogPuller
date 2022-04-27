/**
 * 
 */
package com.abesoft.wcl.MassPullLogs.framework;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.abesoft.wcl.MassPullLogs.request.auth.AuthToken;
import com.abesoft.wcl.MassPullLogs.request.monitor.KeyMonitor;

/**
 *
 * @author Abelito75
 *
 */
public class Controller {

	private String key;
	private String secret;
	
	private AuthToken token;
	private KeyMonitor monitor;
	
	public Controller() {
		refreshKey();
		generateAuthToken();
	}
	
	public void refreshKey() {
		try {
			List<String> credsToLoad = Files.readAllLines(Paths.get("creds.creds"));
			if (credsToLoad.size() != 2) {
				throw new RuntimeException("Invalid amount of Creds");
			}

			key = credsToLoad.get(0);
			secret = credsToLoad.get(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * We should just run verify here
	 * @return
	 */
	public boolean isKeyValid() {
		return token != null;
	}
	
	public void generateAuthToken() {
		AuthToken token = new AuthToken(key, secret);
		if(token.isAuthenticatedAndNotExpired()) {
			this.token = token;
		} else {
			this.token = null;
		}
	}
	
	public boolean isMonitored() {
		return monitor != null;
	}
	
	public void generateMonitor() {
		if(token == null) {
			return;
		}
		
		KeyMonitor monitor = new KeyMonitor();
		if(monitor.isGoodFetch()) {
			this.monitor = monitor;
		}else {
			this.monitor = null;
		}
	}
	
	public boolean isReadyForCalls() {
		return isKeyValid() && isMonitored();
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	public AuthToken getToken() {
		return token;
	}
	
	public KeyMonitor getMonitor() {
		return monitor;
	}
	
	public void saveCreds() {
		try {
			String toSave = key + "\n" + secret;
			Files.writeString(Paths.get("creds.creds"), toSave);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
