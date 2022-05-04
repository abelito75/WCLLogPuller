/**
 * 
 */
package com.abesoft.wcl.MassPullLogs.gui.scenes;

import java.awt.GridLayout;

import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abesoft.wcl.MassPullLogs.gui.components.JLogLabel;

/**
 *
 * @author Abelito75
 *
 */
public class ExampleScene {
	
	private static Logger log = LogManager.getLogger(ExampleScene.class);

	private JPanel panel;

	public ExampleScene() {
		panel = new JPanel(new GridLayout(0,1));
		panel.setSize(200,200);
		setup();
	}
	
	private void setup() {
		JLogLabel debug = new JLogLabel(log);
		debug.setDebug("Debug");
		JLogLabel info = new JLogLabel(log);
		info.setInfo("Info");
		JLogLabel warn = new JLogLabel(log);
		warn.setWarn("Warn");
		JLogLabel error = new JLogLabel(log);
		error.setError("Error");
		JLogLabel fatal = new JLogLabel(log);
		fatal.setFatal("Fatal");
		panel.add(debug);
		panel.add(info);
		panel.add(warn);
		panel.add(error);
		panel.add(fatal);
		panel.revalidate();
		panel.repaint();
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
}
