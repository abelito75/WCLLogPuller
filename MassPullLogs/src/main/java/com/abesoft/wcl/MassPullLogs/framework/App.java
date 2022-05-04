/**
 * 
 */
package com.abesoft.wcl.MassPullLogs.framework;

import com.abesoft.wcl.MassPullLogs.gui.scenes.ExampleScene;
import com.abesoft.wcl.MassPullLogs.gui.scenes.HomeScene;
import com.formdev.flatlaf.FlatDarkLaf;

/**
 *
 * @author Abelito75
 *
 */
public class App {
	
	private static App app;

	public static void main(String[] args) {
		guiConfig();
		getApp();
		// controller needs to be fully setup before we can build the monitor
		app.getController().generateMonitor();
		app.getGui().setContent(new ExampleScene().getPanel());
		
	}
	
	public static App getApp() {
		if (app == null) {
			// synchronized block to remove overhead
			synchronized (App.class) {
				if (app == null) {
					// if instance is null, initialize
					app = new App();
				}

			}
		}
		return app;
	}
	
	public static void guiConfig() {
		FlatDarkLaf.setup();
		System.setProperty( "flatlaf.menuBarEmbedded", "false" );
	}
	
	private Controller controller;
	private Gui gui;
	
	private App() {
		controller = new Controller();
		gui = new Gui();
		gui.setVisible(true);
	}
	
	public Controller getController() {
		return controller;
	}
	
	public Gui getGui() {
		return gui;
	}
	
}
