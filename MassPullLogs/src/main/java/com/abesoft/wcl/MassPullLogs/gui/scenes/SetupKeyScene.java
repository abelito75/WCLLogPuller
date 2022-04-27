/**
 * 
 */
package com.abesoft.wcl.MassPullLogs.gui.scenes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abesoft.wcl.MassPullLogs.framework.App;
import com.abesoft.wcl.MassPullLogs.framework.Controller;
import com.abesoft.wcl.MassPullLogs.gui.components.JLogLabel;
import com.abesoft.wcl.MassPullLogs.request.auth.AuthToken;

/**
 *
 * @author Abelito75
 *
 */
public class SetupKeyScene {
	
	private static Logger log = LogManager.getLogger(SetupKeyScene.class);

	private JPanel mainPanel;

	public SetupKeyScene() {
		mainPanel = new JPanel(new BorderLayout());

		setup();
	}

	private void setup() {

		Controller controller = App.getApp().getController();

		String key = controller.getKey();

		JPanel responsePanel = new JPanel();

		JTextField keyField = new JTextField(key, 30);
		JLabel keyFieldLabel = new JLabel("Key: ");
		keyFieldLabel.setLabelFor(keyField);

		JTextField secretField = new JTextField(30);
		JLabel secretFieldLabel = new JLabel("Secret: ");
		secretFieldLabel.setLabelFor(keyField);

		JButton test = new JButton("Test");
		test.addActionListener(e -> {
			JProgressBar bar = new JProgressBar();
			bar.setIndeterminate(true);
			responsePanel.add(bar);
			responsePanel.revalidate();
			responsePanel.repaint();
			
			
			new Thread(() -> {
				AuthToken token = new AuthToken(keyField.getText(), secretField.getText());
				JLogLabel responseLabel = new JLogLabel(log);
				if (token.isAuthenticatedAndNotExpired()) {
					responseLabel.setInfo("Success");
				} else {
					responseLabel.setWarn("Failure");
				}
				responsePanel.removeAll();
				responsePanel.add(responseLabel);
				responsePanel.revalidate();
				responsePanel.repaint();
			}).start();
		});

		JButton save = new JButton("Save");
		save.addActionListener(e -> {
			controller.setKey(keyField.getText());
			controller.setSecret(secretField.getText());
			controller.generateAuthToken();
			controller.saveCreds();
		});

		JButton goHome = new JButton("Go Home");
		goHome.addActionListener(e -> App.getApp().getGui().setContent(new HomeScene().getPanel()));

		mainPanel.add(goHome, BorderLayout.NORTH);

		JPanel body = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		body.add(responsePanel, gbc);
		gbc.gridwidth = 1;
		gbc.gridy++;
		body.add(keyFieldLabel, gbc);
		gbc.gridx++;
		body.add(keyField, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		body.add(secretFieldLabel, gbc);
		gbc.gridx++;
		body.add(secretField, gbc);
		gbc.gridx = 0;
		gbc.gridy++;

		JPanel buttons = new JPanel();
		buttons.add(test);
		buttons.add(save);

		gbc.gridwidth = 2;

		body.add(buttons, gbc);

		mainPanel.add(body, BorderLayout.CENTER);
	}

	public JPanel getPanel() {
		return mainPanel;
	}
}
