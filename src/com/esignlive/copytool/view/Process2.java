package com.esignlive.copytool.view;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.esignlive.copytool.App;
import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.service.SenderService;

import lombok.Getter;
import lombok.Setter;

public class Process2 {
	@Getter
	private JPanel frame;
	private JRadioButton rdbtnYes;
	private JRadioButton rdbtnNoallTemplates;
	private JScrollPane scrollPane;
	private JButton btnInviteSenders;
	private JButton btnNewButton;
	private JPanel panel;

	private Map<JLabel, JTextField> oldAndNewSenders = new LinkedHashMap<>();
	private Map<JLabel, JLabel> oldSenderStatus = new LinkedHashMap<>();

	@Setter
	private String errorMsg;
	private JButton btnNewButton_1;
	private JButton btnNewButton_2;
	private JLabel lblNewLabel_2;
	private JButton button;

	/**
	 * Create the application.
	 */
	public Process2() {
		initialize();
	}

	private Process2 getInstance() {
		return this;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JPanel();
		frame.setBounds(100, 100, 800, 700);
		frame.setLayout(null);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(553, 35, 144, 14);
		progressBar.setValue(50);
		frame.add(progressBar);

		JLabel progressBarLabel = new JLabel("2/4");
		progressBarLabel.setBounds(708, 35, 46, 14);
		frame.add(progressBarLabel);

		JLabel lblDoYouWant = new JLabel("Do you want to copy senders?");
		lblDoYouWant.setBounds(67, 51, 217, 14);
		frame.add(lblDoYouWant);

		rdbtnYes = new JRadioButton("Yes.");
		rdbtnYes.setBounds(67, 76, 109, 23);
		rdbtnYes.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				rdbtnYes.setSelected(true);
				rdbtnNoallTemplates.setSelected(false);
				btnNewButton.setEnabled(false);
				scrollPane.setVisible(true);
				btnInviteSenders.setEnabled(true);
				btnNewButton_1.setEnabled(true);
				button.setEnabled(true);

				if (UserData.oldSenderList == null || UserData.oldSenderList.size() == 0) {
					btnInviteSenders.setEnabled(false);
					btnNewButton_1.setEnabled(false);
					button.setEnabled(false);
					new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								errorMsg = null;
								List<String> oldEnvSenders = SenderService.getInstance().getOldEnvSenders();

								// after success
								// add labels
								scrollPane.setVisible(false);
								addLabels(oldEnvSenders);
								scrollPane.setVisible(true);

								btnInviteSenders.setEnabled(true);
								btnNewButton_1.setEnabled(true);
								button.setEnabled(true);
								scrollPane.repaint();
							} catch (Exception e1) {
								e1.printStackTrace();
								// show connection test fail dialog
								JOptionPane.showMessageDialog(frame, e1.getMessage(),
										"Fail load old environment senders", JOptionPane.ERROR_MESSAGE);
							}

						}
					}).start();
				}
			}
		});
		frame.add(rdbtnYes);

		rdbtnNoallTemplates = new JRadioButton("No. (All Templates and Layouts will be created by new Owner)");
		rdbtnNoallTemplates.setBounds(67, 114, 491, 23);
		rdbtnNoallTemplates.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				rdbtnYes.setSelected(false);
				rdbtnNoallTemplates.setSelected(true);
				scrollPane.setVisible(false);
				btnInviteSenders.setEnabled(false);
				btnNewButton.setEnabled(true);
			}
		});

		frame.add(rdbtnNoallTemplates);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(31, 156, 740, 425);
		scrollPane.setVisible(false);
		frame.add(scrollPane);

		panel = new JPanel();
		scrollPane.setViewportView(panel);
		panel.setLayout(null);

		JLabel lblNewLabel = new JLabel("Old Environment ");
		lblNewLabel.setBounds(88, 11, 149, 27);
		panel.add(lblNewLabel);

		JLabel lblNewEnvironmentSenders = new JLabel("New Environment ");
		lblNewEnvironmentSenders.setBounds(465, 11, 149, 27);
		panel.add(lblNewEnvironmentSenders);

		btnNewButton_1 = new JButton("=>");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				for (JLabel oldSenderEmailLabel : oldAndNewSenders.keySet()) {
					String senderEmail = oldSenderEmailLabel.getText();
					oldAndNewSenders.get(oldSenderEmailLabel).setText(senderEmail);
				}
			}
		});
		btnNewButton_1.setBounds(300, 74, 61, 92);
		panel.add(btnNewButton_1);

		btnNewButton_2 = new JButton("<html>\r\nView</br>\r\nError</br>\r\nReport</br>\r\n</html>");
		btnNewButton_2.setHorizontalAlignment(SwingConstants.LEFT);
		btnNewButton_2.setVisible(false);

		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// to do
				System.out.println(errorMsg);

				JTextArea ta = new JTextArea(20, 50);
//				ta.setBounds(new Rectangle(800, 500));
				ta.setText(errorMsg);
//				ta.setWrapStyleWord(true);
//				ta.setLineWrap(true);
//				ta.setCaretPosition(0);
				ta.setEditable(false);

				JOptionPane.showMessageDialog(frame, new JScrollPane(ta), "Inviting Sender Error",
						JOptionPane.ERROR_MESSAGE);

			}
		});
		btnNewButton_2.setVerticalAlignment(SwingConstants.TOP);
		btnNewButton_2.setBounds(300, 301, 61, 60);
		panel.add(btnNewButton_2);

		button = new JButton("<=");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				for (JTextField jTextField : oldAndNewSenders.values()) {
					jTextField.setText("");
				}

			}
		});
		button.setBounds(300, 186, 61, 92);
		panel.add(button);

		// buttons
		btnInviteSenders = new JButton("Invite Senders");
		btnInviteSenders.setBounds(122, 600, 162, 38);
		btnInviteSenders.setEnabled(false);

		btnInviteSenders.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// make two buttons unenable
				btnNewButton_1.setEnabled(false);
				button.setEnabled(false);
				btnNewButton_2.setEnabled(false);
				btnNewButton_2.setVisible(false);

				// clear invite status labels
				clearInvitationStatus();

				Map<JLabel, String> newSenderList = new LinkedHashMap<>();
				for (JLabel iLabel : oldAndNewSenders.keySet()) {
					newSenderList.put(iLabel, oldAndNewSenders.get(iLabel).getText());
				}

				new Thread(new Runnable() {

					@Override
					public void run() {
						Map<JLabel, Boolean> inviteResult = SenderService.getInstance().inviteSenders(newSenderList,
								getInstance());

						boolean canNext = true;

						// set labels
						for (JLabel oldSenderEmail : inviteResult.keySet()) {
							boolean isSuccess = inviteResult.get(oldSenderEmail);

							if (!isSuccess) {
								canNext = false;
							}

						}

						// if has error, show error report button
						// make panel buttons enable
						btnNewButton_1.setEnabled(true);
						button.setEnabled(true);
						if (errorMsg != null && !errorMsg.trim().equals("<html></html>")) {
							btnNewButton_2.setVisible(true);
							btnNewButton_2.setEnabled(true);
						}

						// if all senders are successfully invited, enable next process
						if (canNext) {
							btnNewButton.setEnabled(true);
						}
						scrollPane.repaint();
					}
				}).start();

			}
		});

		frame.add(btnInviteSenders);

		btnNewButton = new JButton("Next Process");
		btnNewButton.setBounds(483, 600, 162, 38);
		btnNewButton.setEnabled(false);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// set isCopySender
				if (rdbtnYes.isSelected()) {
					UserData.copySender = true;
				} else if (rdbtnNoallTemplates.isSelected()) {
					UserData.copySender = false;
				}

				try {
					SenderService.getInstance().setNewEnvOwnerEmail();
				} catch (Exception ex) {
					// to do

				}

				App.setMainFrame(new Process3().getFrame());
			}
		});
		frame.add(btnNewButton);

	}

	private void addLabels(List<String> oldEnvSenders) {
		int y = 57;
		for (int i = 0; i < oldEnvSenders.size(); i++) {
			JLabel lblTestData = new JLabel(oldEnvSenders.get(i));
			lblTestData.setBounds(17, y, 250, 20);
			panel.add(lblTestData);

			// add new sender column
			JTextField txtSampleText = new JTextField();
			txtSampleText.setBounds(385, y - 1, 250, 22);
			panel.add(txtSampleText);
			oldAndNewSenders.put(lblTestData, txtSampleText);

			// add invite status column
			JLabel lblNewLabel_1 = new JLabel("");
			lblNewLabel_1.setBounds(682, y, 20, 20);
			panel.add(lblNewLabel_1);
			oldSenderStatus.put(lblTestData, lblNewLabel_1);

			y += 30;

			panel.setPreferredSize(new Dimension(scrollPane.getWidth() - 30, y + 15));
		}

	}

	public void setInvitationStatus(JLabel oldSenderEmail, boolean inviteStatus) {
		scrollPane.setVisible(false);
		String statusText = inviteStatus == true ? "√" : "×";
		oldSenderStatus.get(oldSenderEmail).setText(statusText);
		scrollPane.setVisible(true);
	}

	public void clearInvitationStatus() {
		scrollPane.setVisible(false);

		for (JLabel jLabel : oldSenderStatus.values()) {
			jLabel.setText("");
		}

		scrollPane.setVisible(true);
	}

}
