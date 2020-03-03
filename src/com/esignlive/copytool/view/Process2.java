package com.esignlive.copytool.view;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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

import com.alibaba.fastjson.JSONException;
import com.esignlive.copytool.App;
import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.service.SenderService;
import com.esignlive.copytool.vo.SenderVo;

public class Process2 {
	private JPanel frame;
	private JRadioButton mode1Radio;
	private JRadioButton mode2Radio;
	private JRadioButton mode3Radio;
	private List<JRadioButton> modeList = new ArrayList<>();

	private JScrollPane scrollPane;
	private JPanel panel;
	private JButton selectAllSenderBtn;
	private JButton selectNoneSenderBtn;

	private Map<JButton, JTextField> oldAndNewSenders = new LinkedHashMap<>();
	private Map<JButton, JLabel> oldSenderStatus = new LinkedHashMap<>();

	private String errorMsg;
	private JButton errorMsgBtn;
	private JButton inviteSenderBtn;
	private JButton nextProcessBtn;

	public JRadioButton getMode1Radio() {
		return mode1Radio;
	}

	public JRadioButton getMode2Radio() {
		return mode2Radio;
	}

	public JRadioButton getMode3Radio() {
		return mode3Radio;
	}

	public List<JRadioButton> getModeList() {
		return modeList;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public JPanel getPanel() {
		return panel;
	}

	public JButton getSelectAllSenderBtn() {
		return selectAllSenderBtn;
	}

	public JButton getSelectNoneSenderBtn() {
		return selectNoneSenderBtn;
	}

	public Map<JButton, JTextField> getOldAndNewSenders() {
		return oldAndNewSenders;
	}

	public Map<JButton, JLabel> getOldSenderStatus() {
		return oldSenderStatus;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public JButton getErrorMsgBtn() {
		return errorMsgBtn;
	}

	public JButton getInviteSenderBtn() {
		return inviteSenderBtn;
	}

	public JButton getNextProcessBtn() {
		return nextProcessBtn;
	}

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

		JLabel lblDoYouWant = new JLabel("As Account Owner:");
		lblDoYouWant.setBounds(67, 51, 217, 14);
		frame.add(lblDoYouWant);

		mode1Radio = new JRadioButton("Copy all senders and match them in new environment.");
		mode1Radio.setBounds(67, 72, 703, 23);
		modeList.add(mode1Radio);

		mode1Radio.addActionListener(
				new ModeRadioActionListener(1, "(Loading sender list...)", "Fail to load sender list!") {

					@Override
					public void beforeCall() {
						scrollPane.setVisible(true);

					}

					@Override
					public void afterCall() {
						// after success
						// add labels
						scrollPane.setVisible(true);
						scrollPane.repaint();
						getInviteSenderBtn().setEnabled(true);
						getSelectAllSenderBtn().setEnabled(true);
						getSelectAllSenderBtn().setVisible(true);
						getSelectNoneSenderBtn().setEnabled(true);
						getSelectNoneSenderBtn().setVisible(true);
						
					}
				});

		frame.add(mode1Radio);

		mode2Radio = new JRadioButton("Create all templates/layouts by new owner.");
		mode2Radio.setBounds(67, 105, 703, 23);
		modeList.add(mode2Radio);
//		mode2Radio.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				mode1Radio.setSelected(false);
//				mode2Radio.setSelected(true);
//				scrollPane.setVisible(false);
//				inviteSenderBtn.setEnabled(false);
//				nextProcessBtn.setEnabled(true);
//			}
//		});
		
		
		mode2Radio.addActionListener(
				new ModeRadioActionListener(2, "(Loading sender list...)", "Fail to load sender list!") {

					@Override
					public void beforeCall() {
						scrollPane.setVisible(false);
					}

					@Override
					public void afterCall() {
						nextProcessBtn.setEnabled(true);
					}
				});

		

		frame.add(mode2Radio);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(30, 187, 740, 418);
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

		selectAllSenderBtn = new JButton("=>");
		selectAllSenderBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				for (JButton oldSenderEmailLabel : oldAndNewSenders.keySet()) {
					String senderEmail = oldSenderEmailLabel.getText().substring(0,
							oldSenderEmailLabel.getText().lastIndexOf(":") - 1);
					oldAndNewSenders.get(oldSenderEmailLabel).setText(senderEmail);
				}
			}
		});
		selectAllSenderBtn.setBounds(346, 74, 61, 92);
		panel.add(selectAllSenderBtn);

		selectNoneSenderBtn = new JButton("<=");
		selectNoneSenderBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				for (JTextField jTextField : oldAndNewSenders.values()) {
					jTextField.setText("");
				}

			}
		});
		selectNoneSenderBtn.setBounds(346, 186, 61, 92);
		panel.add(selectNoneSenderBtn);

		// buttons
		inviteSenderBtn = new JButton("Invite Senders");
		inviteSenderBtn.setBounds(112, 613, 162, 38);
		inviteSenderBtn.setEnabled(false);

		inviteSenderBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// make two buttons unenable
				selectAllSenderBtn.setEnabled(false);
				selectNoneSenderBtn.setEnabled(false);
				errorMsgBtn.setEnabled(false);
				errorMsgBtn.setVisible(false);
				inviteSenderBtn.setEnabled(false);
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
						selectAllSenderBtn.setEnabled(true);
						selectNoneSenderBtn.setEnabled(true);
						if (errorMsg != null && !errorMsg.trim().equals("")) {
							errorMsgBtn.setVisible(true);
							errorMsgBtn.setEnabled(true);
						}

						// if all senders are successfully invited, enable next process
						if (canNext) {
							nextProcessBtn.setEnabled(true);
						}
						inviteSenderBtn.setEnabled(true);
						scrollPane.repaint();
					}
				}).start();

			}
		});

		frame.add(inviteSenderBtn);

		nextProcessBtn = new JButton("Prepare Accounts");
		nextProcessBtn.setBounds(528, 613, 162, 38);
		nextProcessBtn.setEnabled(false);
		nextProcessBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inviteSenderBtn.setEnabled(false);
				nextProcessBtn.setEnabled(false);
				mode1Radio.setEnabled(false);
				mode2Radio.setEnabled(false);

				// // set isCopySender
				// if (rdbtnYes.isSelected()) {
				// UserData.copySender = true;
				// } else if (rdbtnNoallTemplates.isSelected()) {
				// UserData.copySender = false;
				// }

				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
//							SenderService.getInstance().senderNextProcessCallback();
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

							inviteSenderBtn.setEnabled(true);
							nextProcessBtn.setEnabled(true);

							mode1Radio.setEnabled(true);
							mode2Radio.setEnabled(true);
						}
					}
				}).start();

			}
		});

		frame.add(nextProcessBtn);

		errorMsgBtn = new JButton("<html>\r\nView</br>\r\nError</br>\r\nReport</br>\r\n</html>");
		errorMsgBtn.setBounds(284, 613, 162, 37);
		frame.add(errorMsgBtn);

		mode3Radio = new JRadioButton("Just copy current sender.");
		mode3Radio.setBounds(67, 157, 703, 23);
		mode3Radio.addActionListener(
				new ModeRadioActionListener(3, "(Checking sender status...)", "Fail to load sender!") {

					@Override
					public void beforeCall() {
						scrollPane.setVisible(false);
					}

					@Override
					public void afterCall() {
						nextProcessBtn.setEnabled(true);
						
					}
				});
		modeList.add(mode3Radio);
		frame.add(mode3Radio);
		
		JLabel lblForRegularMember = new JLabel("As Regular Member:");
		lblForRegularMember.setBounds(67, 136, 217, 14);
		frame.add(lblForRegularMember);
		errorMsgBtn.setVisible(false);

		errorMsgBtn.addActionListener(new ActionListener() {
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

	public void addLabels(List<SenderVo> oldEnvSenders) {
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
				// JTextArea ta = new JTextArea(20, 50);
				// ta.setText(UserData.sourceCredential.getSenderVo().getContent().toString());
				// ta.setEditable(false);
				//
				// JOptionPane.showMessageDialog(frame, new JScrollPane(ta), "Source Environment
				// Owner Informaton",
				// JOptionPane.OK_OPTION);
				// System.out.println(((JButton) e.getSource()).getText());

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
				// JTextArea ta = new JTextArea(20, 50);
				// ta.setText(UserData.destinationCredential.getSenderVo().getContent().toString());
				// ta.setEditable(false);
				//
				// JOptionPane.showMessageDialog(frame, new JScrollPane(ta), "Destination
				// Environment Owner Informaton",
				// JOptionPane.OK_OPTION);
				// System.out.println(((JButton) e.getSource()).getText());

				try {
					String senderLimitation = UserData.destinationCredential.getSenderVo().getContent()
							.getJSONObject("account").getJSONArray("licenses").getJSONObject(0).getJSONObject("plan")
							.getJSONArray("quotas").getJSONObject(0).getString("limit");
					JOptionPane.showMessageDialog(frame, "Sender Limitation: " + senderLimitation,
							"Destination Environment Owner Info", JOptionPane.OK_OPTION);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

			}
		});
		btnNewButton_5.setBounds(425, 57, 250, 20);
		panel.add(btnNewButton_5);

		System.out.println(oldEnvSenders);
		
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

	abstract class ModeRadioActionListener implements ActionListener {
		private int copyMode;
		private String loadingText;
		private String errorTitle;

		public ModeRadioActionListener(int copyMode, String loadingText, String errorTitle) {
			super();
			this.copyMode = copyMode;
			this.loadingText = loadingText;
			this.errorTitle = errorTitle;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			UserData.copyMode = copyMode;
			for (JRadioButton jRadioButton : modeList) {
				jRadioButton.setSelected(false);
				jRadioButton.setEnabled(false);
			}
			String originalText = ((JRadioButton) e.getSource()).getText();
			((JRadioButton) e.getSource()).setSelected(true);
			((JRadioButton) e.getSource()).setText(loadingText);

			nextProcessBtn.setEnabled(false);
			inviteSenderBtn.setEnabled(false);
			errorMsgBtn.setVisible(false);
			scrollPane.setVisible(false);

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						errorMsg = null;

						beforeCall();
						SenderService.getInstance().handleRequest(getInstance());
						afterCall();
						
					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(frame, e1.getMessage(), errorTitle, JOptionPane.ERROR_MESSAGE);
					}
					for (JRadioButton jRadioButton : modeList) {
						jRadioButton.setEnabled(true);
					}
					((JRadioButton) e.getSource()).setText(originalText);
				}
			}).start();
		}

		public abstract void beforeCall();
		public abstract void afterCall();

	}
}
