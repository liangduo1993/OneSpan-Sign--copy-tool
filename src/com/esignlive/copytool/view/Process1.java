package com.esignlive.copytool.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.AccessMode;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.esignlive.copytool.App;
import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.service.EndpointService;
import com.esignlive.copytool.utils.InstanceUtil;
import com.esignlive.copytool.utils.StringUtils;
import com.esignlive.copytool.vo.AccountVo;

public class Process1 {

	private JPanel frame;
	private JTextField textField;
	private JTextField textField_1;
	private JButton btnNewButton;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;

	private JRadioButton rdbtnWithApiKey;
	private JRadioButton rdbtnWithCredentials;
	private JRadioButton destiAPIKeyButton;
	private JRadioButton destCrenButton;

	/**
	 * Create the application.
	 */
	public Process1() {
		initialize();
	}

	public JPanel getFrame() {
		return frame;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JPanel();
		frame.setVisible(true);
		frame.setBounds(100, 100, 800, 700);
		frame.setLayout(null);

		JLabel lblNewLabel = new JLabel("Source API KEY:");
		lblNewLabel.setBounds(114, 123, 120, 38);
		frame.add(lblNewLabel);

		JLabel lblSourceEnvironment = new JLabel("Source Environment:");
		lblSourceEnvironment.setBounds(114, 204, 120, 38);
		frame.add(lblSourceEnvironment);

		JLabel lblDestinationApiKey = new JLabel("Destination API KEY:");
		lblDestinationApiKey.setBounds(114, 389, 120, 38);
		frame.add(lblDestinationApiKey);

		JLabel lblDestinationEnvironment = new JLabel("Destination Environment:");
		lblDestinationEnvironment.setBounds(114, 470, 144, 38);
		frame.add(lblDestinationEnvironment);

		textField = new JTextField();
		textField.setBounds(277, 128, 326, 29);
		frame.add(textField);
		textField.setColumns(10);

		JLabel lblSourceUsername = new JLabel("Source Username");
		lblSourceUsername.setBounds(114, 104, 120, 38);
		frame.add(lblSourceUsername);

		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(277, 109, 326, 29);
		frame.add(textField_2);

		JLabel lblSourcePassowrd = new JLabel("Source Passowrd");
		lblSourcePassowrd.setBounds(114, 155, 120, 38);
		frame.add(lblSourcePassowrd);

		textField_3 = new JTextField();
		textField_3.setColumns(10);
		textField_3.setBounds(277, 160, 326, 29);
		frame.add(textField_3);

		JLabel lblDestinationUsername = new JLabel("Destination Username");
		lblDestinationUsername.setBounds(114, 367, 120, 38);
		frame.add(lblDestinationUsername);

		textField_4 = new JTextField();
		textField_4.setColumns(10);
		textField_4.setBounds(277, 372, 326, 29);
		frame.add(textField_4);

		JLabel lblDestinationPassowrd = new JLabel("Destination Passowrd");
		lblDestinationPassowrd.setBounds(114, 418, 120, 38);
		frame.add(lblDestinationPassowrd);

		textField_5 = new JTextField();
		textField_5.setColumns(10);
		textField_5.setBounds(277, 423, 326, 29);
		frame.add(textField_5);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(553, 35, 144, 14);
		progressBar.setValue(25);
		frame.add(progressBar);

		JLabel progressBarLabel = new JLabel("1/4");
		progressBarLabel.setBounds(708, 35, 46, 14);
		frame.add(progressBarLabel);

		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(InstanceUtil.endPointList.keySet().toArray()));
		comboBox.setBounds(277, 201, 286, 38);
		frame.add(comboBox);

		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(277, 389, 326, 29);
		frame.add(textField_1);

		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(InstanceUtil.endPointList.keySet().toArray()));
		comboBox_1.setBounds(277, 462, 286, 38);
		frame.add(comboBox_1);

		JButton btnNextProcess = new JButton("Next Process");
		btnNextProcess.setEnabled(false);
		btnNextProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				App.setMainFrame(new Process2().getFrame());
			}
		});
		btnNextProcess.setBounds(502, 576, 152, 38);
		frame.add(btnNextProcess);

		btnNewButton = new JButton("Test Connection");
		btnNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnNewButton.setEnabled(false);

				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							System.out.println(textField.getText());
							System.out.println(comboBox.getSelectedItem());
							System.out.println(textField_1.getText());
							System.out.println((String) comboBox.getSelectedItem());
							UserData.sourceApiUrl = InstanceUtil.getUrlByKey((String) comboBox.getSelectedItem());
							UserData.destinationApiUrl = InstanceUtil
									.getUrlByKey((String) comboBox_1.getSelectedItem());
							System.out.println(UserData.sourceApiUrl);
							System.out.println(UserData.destinationApiUrl);
							
							if (!StringUtils.isEmpty(textField.getText())) {
								UserData.sourceCredential.setApiKey(textField.getText().trim());
							}
							if (!StringUtils.isEmpty(textField_2.getText())) {
								UserData.sourceCredential.setUsername(textField_2.getText().trim());
							}
							if (!StringUtils.isEmpty(textField_3.getText())) {
								UserData.sourceCredential.setPassword(textField_3.getText().trim());
							}
							if (!StringUtils.isEmpty(textField_1.getText())) {
								UserData.destinationCredential.setApiKey(textField_1.getText().trim());
							}
							if (!StringUtils.isEmpty(textField_4.getText())) {
								UserData.destinationCredential.setUsername(textField_4.getText().trim());
							}
							if (!StringUtils.isEmpty(textField_5.getText())) {
								UserData.destinationCredential.setPassword(textField_5.getText().trim());
							}

							EndpointService.getInstance().testConnection();
							btnNextProcess.setEnabled(true);

							// show success dialog
							JOptionPane.showMessageDialog(frame, "API Keys are all valid!", "Connection test success",
									JOptionPane.INFORMATION_MESSAGE);
							
						} catch (Exception exception) {
							exception.printStackTrace();
							
							// to do
							// show connection test fail dialog
							JOptionPane.showMessageDialog(frame,
									exception.getMessage(),
									"Connection test fail", JOptionPane.ERROR_MESSAGE);
						}
						btnNewButton.setEnabled(true);
					}
				}).start();

			}

		});
		btnNewButton.setBounds(145, 576, 144, 38);
		frame.add(btnNewButton);

		rdbtnWithApiKey = new JRadioButton("With API KEY");
		rdbtnWithApiKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (rdbtnWithApiKey.isSelected()) {
					rdbtnWithCredentials.setSelected(false);
					lblNewLabel.setVisible(true);
					textField.setVisible(true);
					lblSourceUsername.setVisible(false);
					lblSourcePassowrd.setVisible(false);
					textField_2.setVisible(false);
					textField_3.setVisible(false);
					UserData.sourceCredential.setCredentialType(AccountVo.CredentialType.API_KEY);
				}else {
					rdbtnWithApiKey.setSelected(true);
				}
			}
		});
		rdbtnWithApiKey.setBounds(125, 58, 200, 50);

		frame.add(rdbtnWithApiKey);

		rdbtnWithCredentials = new JRadioButton("With Credentials");
		rdbtnWithCredentials.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (rdbtnWithCredentials.isSelected()) {
					rdbtnWithApiKey.setSelected(false);
					lblNewLabel.setVisible(false);
					textField.setVisible(false);
					lblSourceUsername.setVisible(true);
					lblSourcePassowrd.setVisible(true);
					textField_2.setVisible(true);
					textField_3.setVisible(true);
					UserData.sourceCredential.setCredentialType(AccountVo.CredentialType.CREDENTIAL);
				}else {
					rdbtnWithCredentials.setSelected(true);
				}
			}
		});
		rdbtnWithCredentials.setBounds(363, 55, 200, 50);
		frame.add(rdbtnWithCredentials);

		destiAPIKeyButton = new JRadioButton("With API KEY");
		destiAPIKeyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (destiAPIKeyButton.isSelected()) {
					destCrenButton.setSelected(false);
					lblDestinationApiKey.setVisible(true);
					textField_1.setVisible(true);
					lblDestinationUsername.setVisible(false);
					lblDestinationPassowrd.setVisible(false);
					textField_4.setVisible(false);
					textField_5.setVisible(false);
					UserData.destinationCredential.setCredentialType(AccountVo.CredentialType.API_KEY);
				}else {
					destiAPIKeyButton.setSelected(true);
				}
			}
		});
		destiAPIKeyButton.setBounds(125, 322, 200, 50);

		frame.add(destiAPIKeyButton);

		destCrenButton = new JRadioButton("With Credentials");
		destCrenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (destCrenButton.isSelected()) {
					destiAPIKeyButton.setSelected(false);
					lblDestinationApiKey.setVisible(false);
					textField_1.setVisible(false);
					lblDestinationUsername.setVisible(true);
					lblDestinationPassowrd.setVisible(true);
					textField_4.setVisible(true);
					textField_5.setVisible(true);
					UserData.destinationCredential.setCredentialType(AccountVo.CredentialType.CREDENTIAL);
				}else {
					destCrenButton.setSelected(true);
				}
			}
		});
		destCrenButton.setBounds(363, 319, 200, 50);
		frame.add(destCrenButton);

		// choose api key as credential by default
		destiAPIKeyButton.doClick();
		rdbtnWithApiKey.doClick();

	}
}
