/**
 * 
 */
package com.abesoft.wcl.MassPullLogs.framework;

import java.awt.BorderLayout;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

/**
 *
 * @author Abelito75
 *
 */
public class Gui {

	private JFrame frame;
	private JMenuBar menuBar;
	private JPanel masterPanel;
	
	public Gui() {
		frame = new JFrame("WCL Mass Log Puller");
		menuBar = new JMenuBar();
		masterPanel = new JPanel(new BorderLayout());
		frame.add(masterPanel);
		frame.setJMenuBar(menuBar);
		frame.setSize(500, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}
	
	public void addWindowListener(WindowListener listen) {
		frame.addWindowListener(listen);
	}
	
	public void removeWindowListener(WindowListener listen) {
		frame.removeWindowListener(listen);
	}
	
	public void setContent(JPanel panel) {
		masterPanel.removeAll();
		masterPanel.add(panel, BorderLayout.CENTER);
		masterPanel.revalidate();
		masterPanel.repaint();
	}
}
