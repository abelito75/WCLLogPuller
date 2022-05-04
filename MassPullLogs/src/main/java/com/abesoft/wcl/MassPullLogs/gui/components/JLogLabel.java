/**
 * 
 */
package com.abesoft.wcl.MassPullLogs.gui.components;

import java.awt.Color;

import javax.swing.JLabel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is a super super barebones label that also logs and colors its messages
 *
 * @author Abelito75
 */
public class JLogLabel extends JLabel {
	
	
	private static final Color DEBUG_COLOR = new Color(112,128,144);
	private static final Color INFO_COLOR = new Color(49,145,119);
	private static final Color WARN_COLOR = new Color(255,140,0);
	private static final Color ERROR_COLOR = new Color(208,55,55);
	private static final Color FATAL_COLOR = new Color(122,18,29);

	
	/**
	 * random bs to fix warning since all of swing is seralized zzz. this will never be used
	 */
	private static final long serialVersionUID = -5497966375019959676L;
	private Logger logger;
	
	public JLogLabel(Logger logger) {
		super();
		this.logger = logger;
	}
	
	public void setDebug(String text) {
		if(!isValidText(text)) {
			return;
		}
		logger.debug(text);
		super.setText(text);
		super.setForeground(DEBUG_COLOR);
	}
	
	public void setInfo(String text) {
		if(!isValidText(text)) {
			return;
		}
		logger.info(text);
		super.setText(text);
		super.setForeground(INFO_COLOR);
	}
	
	public void setWarn(String text) {
		if(!isValidText(text)) {
			return;
		}
		logger.warn(text);
		super.setText(text);
		super.setForeground(WARN_COLOR);
	}
	
	public void setError(String text) {
		if(!isValidText(text)) {
			return;
		}
		logger.error(text);
		super.setText(text);
		super.setForeground(ERROR_COLOR);
	}
	
	public void setFatal(String text) {
		if(!isValidText(text)) {
			return;
		}
		logger.fatal(text);
		super.setText(text);
		super.setForeground(FATAL_COLOR);
	}
	
	public void clear() {
		super.setText("");
		super.setForeground(Color.black);
	}
	
	private boolean isValidText(String text) {
		return text != null && !text.isBlank();
	}
}
