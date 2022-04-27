/**
 * 
 */
package com.abesoft.wcl.MassPullLogs.gui.scenes;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abesoft.wcl.MassPullLogs.framework.App;
import com.abesoft.wcl.MassPullLogs.gui.components.JLogLabel;

/**
 *
 * @author Abelito75
 *
 */
public class HomeScene {
	
	private static Logger log = LogManager.getLogger(HomeScene.class);
	
	private JPanel mainPanel;

	public HomeScene() {
		mainPanel = new JPanel(new BorderLayout());
		
		setup();
	}
	
	private void setup() {
		JPanel north = new JPanel();
		if(!App.getApp().getController().isKeyValid()) {
			//key isn't valid so lets add a warning and a button for them
			JLogLabel label = new JLogLabel(log);
			label.setWarn("Your WCL V2 API key isn't setup.");
			JButton button = new JButton("Setup Key");
			button.addActionListener(e ->  App.getApp().getGui().setContent(new SetupKeyScene().getPanel()));
			
			north.add(label);
			north.add(button);
		}
		
		mainPanel.add(north, BorderLayout.NORTH);
		
		
		
	}
	
	public JPanel getPanel() {
		return mainPanel;
	}
}
