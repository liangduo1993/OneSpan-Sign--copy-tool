package com.esignlive.copytool.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.service.LayoutService;
import com.esignlive.copytool.vo.LayoutVo;

public class Process4 {
	private JPanel frame;

	private JRadioButton rdbtnYes;
	private JRadioButton rdbtnNo;
	private JPanel panel;
	private JScrollPane scrollPane;
	private Map<String, JLabel> oldEnvLayoutCopyStatus = new LinkedHashMap<>(); // <old layout id, status label>
	private JButton btnNextProcess;
	private JButton btnNewButton_2;

	private String errorMsg;
	
	private Map<JCheckBox, String> layoutCheckboxs = new LinkedHashMap<>();// <checkbox, layout id>

	/**
	 * Create the application.
	 */
	public Process4() {
		initialize();
	}

	private Process4 getInstance() {
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
		progressBar.setValue(100);
		frame.add(progressBar);

		JLabel progressBarLabel = new JLabel("4/4");
		progressBarLabel.setBounds(708, 35, 46, 14);
		frame.add(progressBarLabel);

		JButton btnNewButton = new JButton("Copy Layouts");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				new Thread(new Runnable() {

					@Override
					public void run() {
						btnNewButton.setEnabled(false);
						btnNewButton.setText("Processing...");

						new Thread(new Runnable() {

							@Override
							public void run() {
								errorMsg = null;
								Map<String, Boolean> copyTemplate = LayoutService.getInstance().copyLayouts(null,
										getInstance());

								btnNewButton.setEnabled(true);
								btnNewButton.setText("Copy Layouts");

								// show report when copy templates completed
								StringBuilder report = new StringBuilder(200);
								report.append("Total of ").append(copyTemplate.size()).append(" layouts.");
								int success = 0;
								for (Boolean isSuccess : copyTemplate.values()) {
									if (isSuccess) {
										success++;
									}
								}
								report.append("Successfully copy ").append(success).append(" layouts.");

								JOptionPane.showMessageDialog(frame, report.toString(), "Copy Layouts Report",
										JOptionPane.INFORMATION_MESSAGE);

								// enable error report if error occurs
								if (errorMsg != null && !errorMsg.trim().equals("")) {
									btnNewButton_2.setVisible(true);
								}

								// enable next process
								btnNextProcess.setEnabled(true);
							}
						}).start();

					}
				}).start();

			}
		});
		btnNewButton.setBounds(103, 591, 163, 53);
		frame.add(btnNewButton);

		btnNextProcess = new JButton("Get Account Report");
		btnNextProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNextProcess.setEnabled(false);
		btnNextProcess.setVisible(false);

		btnNextProcess.setBounds(512, 591, 185, 53);
		frame.add(btnNextProcess);

		JLabel lblDoYouWant = new JLabel("Do you want to copy Layouts?");
		lblDoYouWant.setBounds(76, 62, 217, 14);
		frame.add(lblDoYouWant);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(74, 159, 680, 415);
		frame.add(scrollPane);

		panel = new JPanel();
		scrollPane.setViewportView(panel);
		panel.setLayout(null);

		JLabel lblTemplateListIn = new JLabel("Layout List in Old Environment");
		lblTemplateListIn.setBounds(34, 11, 328, 14);
		panel.add(lblTemplateListIn);

		JLabel lblDoYouWant_2 = new JLabel("Do you want to specify Layout IDs?");
		lblDoYouWant_2.setBounds(76, 172, 287, 14);
		lblDoYouWant_2.setVisible(false);
		frame.add(lblDoYouWant_2);

		JRadioButton radioButton_2 = new JRadioButton("Yes.");
		radioButton_2.setVisible(false);
		radioButton_2.setBounds(369, 168, 54, 23);
		frame.add(radioButton_2);

		JRadioButton radioButton_3 = new JRadioButton("No.");
		radioButton_3.setSelected(true);
		radioButton_3.setVisible(false);
		radioButton_3.setBounds(452, 168, 46, 23);
		frame.add(radioButton_3);

		JButton button = new JButton("Select your folder location");
		button.setEnabled(false);
		button.setVisible(false);
		button.setBounds(545, 159, 191, 40);
		frame.add(button);

		rdbtnYes = new JRadioButton("Yes.");
		rdbtnYes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnNo.setSelected(false);

				rdbtnYes.setText("Yes. (Loading All Existing Layouts...)");

//				lblDoYouWant_2.setVisible(true);
//				radioButton_2.setVisible(true);
//				radioButton_3.setVisible(true);
//				button.setVisible(true);

				scrollPane.setVisible(true);
				btnNewButton.setVisible(true);
				btnNewButton.setEnabled(true);
				btnNextProcess.setEnabled(false);

				if (UserData.oldEnvLayouts == null || UserData.oldEnvLayouts.size() == 0) {
					btnNewButton.setEnabled(false);
					// load all templates in old env
					new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								// <layout id, layout name>
								Map<String, String> oldEnvLayouts = LayoutService.getInstance().getOldEnvLayouts();
								System.out.println(oldEnvLayouts);
								System.out.println(oldEnvLayouts.size());

								// add labels
								scrollPane.setVisible(false);
								addLabels(oldEnvLayouts);
								scrollPane.setVisible(true);

								btnNewButton.setEnabled(true);

								rdbtnYes.setText("Yes.");

								scrollPane.repaint();
							} catch (Exception e1) {
								e1.printStackTrace();
								// show connection test fail dialog
								JOptionPane.showMessageDialog(frame, e1.getMessage(),
										"Fail load old environment Layouts", JOptionPane.ERROR_MESSAGE);
							}
							scrollPane.repaint();
						}

					}).start();

				}

			}
		});
		rdbtnYes.setBounds(76, 87, 383, 23);
		frame.add(rdbtnYes);

		rdbtnNo = new JRadioButton("No.");
		rdbtnNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnYes.setSelected(false);

//				lblDoYouWant_2.setVisible(false);
//				radioButton_2.setVisible(false);
//				radioButton_3.setVisible(false);
//				button.setVisible(false);

				scrollPane.setVisible(false);
				btnNewButton.setVisible(false);
				btnNewButton.setEnabled(false);
				btnNextProcess.setEnabled(true);

			}
		});
		rdbtnNo.setBounds(76, 125, 54, 23);
		frame.add(rdbtnNo);

		btnNewButton_2 = new JButton("Error Message");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(errorMsg);
				JTextArea ta = new JTextArea(20, 50);
				ta.setText(errorMsg);
				ta.setEditable(false);

				JOptionPane.showMessageDialog(frame, new JScrollPane(ta), "Copy Layouts Error",
						JOptionPane.ERROR_MESSAGE);

			}
		});
		btnNewButton_2.setBounds(272, 591, 151, 53);
		btnNewButton_2.setVisible(false);
		frame.add(btnNewButton_2);
		rdbtnNo.doClick();
	}

	private void addLabels(Map<String, String> oldEnvTemplates) {
		// to do
		int y = 57;

		Map.Entry<String, String>[] array = oldEnvTemplates.entrySet().toArray(new Map.Entry[oldEnvTemplates.size()]);

		JCheckBox chckbxNewCheckBox = new JCheckBox("                              Choose All");
		chckbxNewCheckBox.setBounds(6, 32, 200, 20);
		chckbxNewCheckBox.setEnabled(false);
		panel.add(chckbxNewCheckBox);
		chckbxNewCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (JCheckBox jCheckbox : layoutCheckboxs.keySet()) {
					jCheckbox.setSelected(chckbxNewCheckBox.isSelected());
				}
			}
		});

		for (int i = 0; i < array.length; i++) {
			JCheckBox chckbxNewCheckBoxTemp = new JCheckBox("");
			chckbxNewCheckBoxTemp.setBounds(6, y, 20, 20);
			chckbxNewCheckBoxTemp.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JCheckBox source = (JCheckBox) e.getSource();
					String layoutId = null;
					if ((layoutId = layoutCheckboxs.get(source)) != null) {
						LayoutVo layoutVo = UserData.oldEnvLayouts.get(layoutId);
						if (layoutVo != null) {
							layoutVo.setIsCopy(source.isSelected());
						}
					}
				}
			});

			panel.add(chckbxNewCheckBoxTemp);

			JLabel lblTestData = new JLabel(array[i].getKey() + " : " + array[i].getValue());
			lblTestData.setBounds(50, y, 550, 20);
			panel.add(lblTestData);

			// add copy template status column
			JLabel lblNewLabel_1 = new JLabel("");
			lblNewLabel_1.setBounds(600, y, 20, 20);
			panel.add(lblNewLabel_1);
			oldEnvLayoutCopyStatus.put(array[i].getKey(), lblNewLabel_1);

			layoutCheckboxs.put(chckbxNewCheckBoxTemp, array[i].getKey());

			y += 30;

			panel.setPreferredSize(new Dimension(scrollPane.getWidth() - 30, y + 30));
			// System.out.println(panel.getPreferredSize().getHeight());
		}

		chckbxNewCheckBox.setEnabled(true);
	}

	public void setCopyStatus(String oldTemplateId, boolean copyStatus) {
		scrollPane.setVisible(false);
		String statusText = copyStatus == true ? "√" : "×";
		oldEnvLayoutCopyStatus.get(oldTemplateId).setText(statusText);
		scrollPane.setVisible(true);
	}

}
