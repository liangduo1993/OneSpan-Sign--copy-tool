package com.esignlive.copytool.view;

import java.awt.Dimension;
import java.awt.Insets;
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

import org.json.JSONException;

import com.esignlive.copytool.App;
import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.service.SenderService;
import com.esignlive.copytool.vo.AccountVo;
import com.esignlive.copytool.vo.SenderVo;

public class Process2 {
	private JPanel frame;
	private JRadioButton rdbtnYes;
	private JRadioButton rdbtnNoallTemplates;
	private JScrollPane scrollPane;
	private JButton btnInviteSenders;
	private JButton btnNewButton;
	private JPanel panel;

	private Map<JButton, JTextField> oldAndNewSenders = new LinkedHashMap<>();
	private Map<JButton, JLabel> oldSenderStatus = new LinkedHashMap<>();

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

	public JPanel getFrame() {
		return frame;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
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
		rdbtnYes.setBounds(67, 76, 264, 23);
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

				if (UserData.oldSenderMap == null || UserData.oldSenderMap.size() == 0) {
					rdbtnYes.setText("Yes. (Loading sender list...)");
					rdbtnNoallTemplates.setEnabled(false);
					btnInviteSenders.setEnabled(false);
					btnNewButton_1.setEnabled(false);
					button.setEnabled(false);
					new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								errorMsg = null;
								List<SenderVo> oldEnvSenders = SenderService.getInstance().getOldEnvSenders();
								SenderService.getInstance().setNewEnvOwner();

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
							rdbtnNoallTemplates.setEnabled(true);
							// rdbtnYes.setText("Yes. (Please click on the email from list for more
							// information!)");
							rdbtnYes.setText("Yes.");
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
		lblNewEnvironmentSenders.setBounds(473, 11, 149, 27);
		panel.add(lblNewEnvironmentSenders);

		btnNewButton_1 = new JButton("=>");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				for (JButton oldSenderEmailLabel : oldAndNewSenders.keySet()) {
					String senderEmail = oldSenderEmailLabel.getText().substring(0,
							oldSenderEmailLabel.getText().lastIndexOf(":") - 1);
					oldAndNewSenders.get(oldSenderEmailLabel).setText(senderEmail);
				}
			}
		});
		btnNewButton_1.setBounds(346, 74, 61, 92);
		panel.add(btnNewButton_1);

		button = new JButton("<=");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				for (JTextField jTextField : oldAndNewSenders.values()) {
					jTextField.setText("");
				}

			}
		});
		button.setBounds(346, 186, 61, 92);
		panel.add(button);

		// buttons
		btnInviteSenders = new JButton("Invite Senders");
		btnInviteSenders.setBounds(67, 600, 162, 38);
		btnInviteSenders.setEnabled(false);

		btnInviteSenders.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// make two buttons unenable
				btnNewButton_1.setEnabled(false);
				button.setEnabled(false);
				btnNewButton_2.setEnabled(false);
				btnNewButton_2.setVisible(false);
				btnInviteSenders.setEnabled(false);
				// clear invite status labels
				clearInvitationStatus();

				Map<JButton, String> newSenderList = new LinkedHashMap<>();
				for (JButton iLabel : oldAndNewSenders.keySet()) {
					newSenderList.put(iLabel, oldAndNewSenders.get(iLabel).getText().trim());
				}
				System.out.println(newSenderList);
				new Thread(new Runnable() {

					@Override
					public void run() {
						Map<JButton, Boolean> inviteResult = SenderService.getInstance().inviteSenders(newSenderList,
								getInstance());

						boolean canNext = true;

						// set labels
						for (JButton oldSenderEmail : inviteResult.keySet()) {
							if (inviteResult.get(oldSenderEmail) != null) {
								if (!inviteResult.get(oldSenderEmail)) {
									canNext = false;
								}
							}
						}

						// if has error, show error report button
						// make panel buttons enable
						btnNewButton_1.setEnabled(true);
						button.setEnabled(true);
						if (errorMsg != null && !errorMsg.trim().equals("")) {
							btnNewButton_2.setVisible(true);
							btnNewButton_2.setEnabled(true);
						}

						// if all senders are successfully invited, enable next process
						if (canNext) {
							btnNewButton.setEnabled(true);
						}
						btnInviteSenders.setEnabled(true);
						scrollPane.repaint();
					}
				}).start();

			}
		});

		frame.add(btnInviteSenders);

		btnNewButton = new JButton("Prepare Accounts");
		btnNewButton.setBounds(483, 600, 162, 38);
		btnNewButton.setEnabled(false);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnInviteSenders.setEnabled(false);
				btnNewButton.setEnabled(false);
				rdbtnYes.setEnabled(false);
				rdbtnNoallTemplates.setEnabled(false);

//				// set isCopySender
//				if (rdbtnYes.isSelected()) {
//					UserData.copySender = true;
//				} else if (rdbtnNoallTemplates.isSelected()) {
//					UserData.copySender = false;
//				}

				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							SenderService.getInstance().senderNextProcessCallback();
							App.setMainFrame(new Process3().getFrame());
						} catch (Exception ex) {
							// to do
							// show error msg, and allow try again, try again should be non effect to
							// existing setting
							JTextArea ta = new JTextArea(20, 50);
							ta.setText(ex.getMessage());
							ta.setEditable(false);

							JOptionPane.showMessageDialog(frame, new JScrollPane(ta), "Error, Please try again!",
									JOptionPane.ERROR_MESSAGE);

							btnInviteSenders.setEnabled(true);
							btnNewButton.setEnabled(true);

							rdbtnYes.setEnabled(true);
							rdbtnNoallTemplates.setEnabled(true);
						}
					}
				}).start();

			}
		});

		frame.add(btnNewButton);

		btnNewButton_2 = new JButton("<html>\r\nView</br>\r\nError</br>\r\nReport</br>\r\n</html>");
		btnNewButton_2.setBounds(242, 601, 162, 37);
		frame.add(btnNewButton_2);
		btnNewButton_2.setVisible(false);

		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// to do
				System.out.println(errorMsg);

				JTextArea ta = new JTextArea(20, 50);
				ta.setText(errorMsg);
				ta.setEditable(false);

				JOptionPane.showMessageDialog(frame, new JScrollPane(ta), "Inviting Sender Error",
						JOptionPane.ERROR_MESSAGE);

			}
		});

	}

	private void addLabels(List<SenderVo> oldEnvSenders) {
		// source owner information
		JButton btnNewButton_4 = new JButton(UserData.sourceCredential.getSenderVo().getEmail() + " : "
				+ UserData.sourceCredential.getSenderVo().getSenderType());
		btnNewButton_4.setBackground(null);
		btnNewButton_4.setHorizontalAlignment(SwingConstants.LEFT);
		btnNewButton_4.setMargin(new Insets(0, 0, 0, 0));
		btnNewButton_4.setOpaque(false);
		btnNewButton_4.setContentAreaFilled(false);
		btnNewButton_4.setBorderPainted(false);
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//
//				JTextArea ta = new JTextArea(20, 50);
//				ta.setText(UserData.sourceCredential.getSenderVo().getContent().toString());
//				ta.setEditable(false);
//
//				JOptionPane.showMessageDialog(frame, new JScrollPane(ta), "Source Environment Owner Informaton",
//						JOptionPane.OK_OPTION);
//				System.out.println(((JButton) e.getSource()).getText());

			}
		});
		btnNewButton_4.setBounds(17, 57, 310, 20);
		panel.add(btnNewButton_4);

		// destination owner information
		JButton btnNewButton_5 = new JButton(UserData.destinationCredential.getSenderVo().getEmail() + " : "
				+ UserData.destinationCredential.getSenderVo().getSenderType());
		btnNewButton_5.setBackground(null);
		btnNewButton_5.setHorizontalAlignment(SwingConstants.LEFT);
		btnNewButton_5.setMargin(new Insets(0, 0, 0, 0));
		btnNewButton_5.setOpaque(false);
		btnNewButton_5.setContentAreaFilled(false);
		btnNewButton_5.setBorderPainted(false);
		btnNewButton_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				JTextArea ta = new JTextArea(20, 50);
//				ta.setText(UserData.destinationCredential.getSenderVo().getContent().toString());
//				ta.setEditable(false);
//
//				JOptionPane.showMessageDialog(frame, new JScrollPane(ta), "Destination Environment Owner Informaton",
//						JOptionPane.OK_OPTION);
//				System.out.println(((JButton) e.getSource()).getText());
				
				try {
					String senderLimitation = UserData.destinationCredential.getSenderVo().getContent().getJSONObject("account").getJSONArray("licenses").getJSONObject(0).getJSONObject("plan").getJSONArray("quotas").getJSONObject(0).getString("limit");
					JOptionPane.showMessageDialog(frame, "Sender Limitation: " + senderLimitation,
							"Destination Environment Owner Info", JOptionPane.OK_OPTION);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
				

			}
		});
		btnNewButton_5.setBounds(425, 57, 250, 20);
		panel.add(btnNewButton_5);

		// source sender list
		int y = 87;
		for (int i = 0; i < oldEnvSenders.size(); i++) {
			JButton btnNewButton_3 = new JButton(
					oldEnvSenders.get(i).getEmail() + " : " + oldEnvSenders.get(i).getSenderType());
			btnNewButton_3.setBackground(null);
			btnNewButton_3.setHorizontalAlignment(SwingConstants.LEFT);
			btnNewButton_3.setMargin(new Insets(0, 0, 0, 0));
			btnNewButton_3.setOpaque(false);
			btnNewButton_3.setContentAreaFilled(false);
			// btnNewButton_3.setBorderPainted(false);
			btnNewButton_3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					System.out.println(((JButton) e.getSource()).getText());

				}
			});
			btnNewButton_3.setBounds(17, y, 310, 20);
			panel.add(btnNewButton_3);

			//
			// JLabel lblTestData = new JLabel(oldEnvSenders.get(i).getEmail() + " : " +
			// oldEnvSenders.get(i).getSenderType());
			// lblTestData.setBounds(17, y, 310, 20);
			// panel.add(lblTestData);

			// add new sender column
			JTextField txtSampleText = new JTextField();
			txtSampleText.setBounds(425, y - 1, 250, 22);
			panel.add(txtSampleText);
			oldAndNewSenders.put(btnNewButton_3, txtSampleText);

			// add invite status column
			JLabel lblNewLabel_1 = new JLabel("");
			lblNewLabel_1.setBounds(695, y, 20, 20);
			panel.add(lblNewLabel_1);
			oldSenderStatus.put(btnNewButton_3, lblNewLabel_1);

			y += 30;

			panel.setPreferredSize(new Dimension(scrollPane.getWidth() - 30, y + 30));
		}

	}

	public void setInvitationStatus(JButton oldSenderEmail, boolean inviteStatus) {
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
