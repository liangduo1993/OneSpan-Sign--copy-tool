package com.esignlive.copytool.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
	private JLabel lblNewLabel_1;

	@Setter
	private String errorMsg;
	private JButton btnNewButton_1;
	private JButton btnNewButton_2;
	private JLabel lblNewLabel_2;

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
				btnNewButton_1.setEnabled(false);

				new Thread(new Runnable() {

					@Override
					public void run() {
						try {

							List<String> oldEnvSenders = SenderService.getInstance().getOldEnvSenders();

							// after success
							// add labels
							addLabels(oldEnvSenders);

							btnInviteSenders.setEnabled(true);
							btnNewButton_1.setEnabled(true);
							scrollPane.repaint();
						} catch (Exception e1) {
							e1.printStackTrace();
							// show connection test fail dialog
							JOptionPane.showMessageDialog(frame, e1.getMessage(), "Fail load old environment senders",
									JOptionPane.ERROR_MESSAGE);
						}

					}
				}).start();

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
		btnNewButton_1.setBounds(300, 139, 61, 92);
		panel.add(btnNewButton_1);

		btnNewButton_2 = new JButton("<html>\r\nView</br>\r\nError</br>\r\nReport</br>\r\n</html>");
		btnNewButton_2.setHorizontalAlignment(SwingConstants.LEFT);
		btnNewButton_2.setVisible(false);

		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// to do
				System.out.println(errorMsg);

				JOptionPane.showMessageDialog(frame, errorMsg, "Inviting Sender Error", JOptionPane.ERROR_MESSAGE);

			}
		});
		btnNewButton_2.setVerticalAlignment(SwingConstants.TOP);
		btnNewButton_2.setBounds(300, 278, 61, 60);
		panel.add(btnNewButton_2);

		// buttons
		btnInviteSenders = new JButton("Invite Senders");
		btnInviteSenders.setBounds(122, 600, 162, 38);
		btnInviteSenders.setEnabled(false);

		btnInviteSenders.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// make two buttons unenable
				btnNewButton_1.setEnabled(false);
				btnNewButton_2.setEnabled(false);
				btnNewButton_2.setVisible(false);

				Map<JLabel, String> newSenderList = new LinkedHashMap<>();
				for (JLabel iLabel : oldAndNewSenders.keySet()) {
					newSenderList.put(iLabel, oldAndNewSenders.get(iLabel).getText());
				}

				new Thread(new Runnable() {

					@Override
					public void run() {
						Map<JLabel, Boolean> inviteResult = SenderService.getInstance().inviteSenders(newSenderList,
								getInstance());

						// set labels
						for (JLabel oldSenderEmail : inviteResult.keySet()) {
							scrollPane.setVisible(false);

							String text = inviteResult.get(oldSenderEmail) == true ? "√" : "×";
							lblNewLabel_1 = new JLabel(text);
							lblNewLabel_1.setBounds(682, oldSenderEmail.getY(), 20, 20);
							panel.add(lblNewLabel_1);

							scrollPane.setVisible(true);
						}

						// if has error, show error report button
						// make two buttons enable
						btnNewButton_1.setEnabled(true);
						if (errorMsg != null && !errorMsg.isEmpty()) {
							btnNewButton_2.setVisible(true);
							btnNewButton_2.setEnabled(true);
						}

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

			JTextField txtSampleText = new JTextField();
			txtSampleText.setBounds(385, y - 1, 250, 22);
			panel.add(txtSampleText);

			oldAndNewSenders.put(lblTestData, txtSampleText);

			y += 30;

			panel.setPreferredSize(new Dimension(scrollPane.getWidth() - 30, y + 15));

		}

	}

}