package com.esignlive.copytool.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.esignlive.copytool.App;
import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.service.TemplateService;

import lombok.Getter;
import lombok.Setter;

public class Process3 {
	@Getter
	private JPanel frame;

	private JRadioButton rdbtnYes;
	private JRadioButton rdbtnNo;
	private JPanel panel;
	private JScrollPane scrollPane;
	private Map<String, JLabel> oldEnvTemplateCopyStatus = new LinkedHashMap<>(); // <old temp id, status label>
	private JButton btnNextProcess;
	private JButton btnNewButton_2;

	@Setter
	private String errorMsg;

	/**
	 * Create the application.
	 */
	public Process3() {
		initialize();
	}

	private Process3 getInstance() {
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
		progressBar.setValue(75);
		frame.add(progressBar);

		JLabel progressBarLabel = new JLabel("3/4");
		progressBarLabel.setBounds(708, 35, 46, 14);
		frame.add(progressBarLabel);

		JButton btnNewButton = new JButton("Copy Templates");
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
								Map<String, Boolean> copyTemplate = TemplateService.getInstance().copyTemplate(null,
										null, getInstance());

								errorMsg = null;
								btnNewButton.setEnabled(true);
								btnNewButton.setText("Copy Templates");

								// show report when copy templates completed
								StringBuilder report = new StringBuilder(200);
								report.append("<html>");
								report.append("Total of ").append(copyTemplate.size()).append(" templates.</br>");
								int success = 0;
								for (Boolean isSuccess : copyTemplate.values()) {
									if (isSuccess) {
										success++;
									}
								}
								report.append("Successfully copy ").append(success).append(" templates.</br>");
								report.append("</html>");

								JOptionPane.showMessageDialog(frame, report.toString(), "Copy Templates Report",
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

		btnNextProcess = new JButton("Next Process");
		btnNextProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				App.setMainFrame(new Process4().getFrame());
			}
		});
		btnNextProcess.setEnabled(false);
		btnNextProcess.setBounds(512, 591, 163, 53);
		frame.add(btnNextProcess);

		JLabel lblDoYouWant = new JLabel("Do you want to copy Templates?");
		lblDoYouWant.setBounds(76, 62, 217, 14);
		frame.add(lblDoYouWant);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(74, 286, 680, 288);
		frame.add(scrollPane);

		panel = new JPanel();
		scrollPane.setViewportView(panel);
		panel.setLayout(null);

		JLabel lblTemplateListIn = new JLabel("Template List in Old Environment");
		lblTemplateListIn.setBounds(34, 11, 328, 14);
		panel.add(lblTemplateListIn);

		JLabel lblDoYouWant_1 = new JLabel("Do you want to upload original Document?");
		lblDoYouWant_1.setBounds(76, 164, 287, 14);
		frame.add(lblDoYouWant_1);

		JButton btnNewButton_1 = new JButton("Select your folder location");
		btnNewButton_1.setEnabled(false);
		btnNewButton_1.setBounds(545, 151, 191, 40);
		frame.add(btnNewButton_1);

		JRadioButton radioButton = new JRadioButton("Yes.");
		radioButton.setBounds(369, 160, 54, 23);
		frame.add(radioButton);

		JRadioButton radioButton_1 = new JRadioButton("No.");
		radioButton_1.setSelected(true);
		radioButton_1.setBounds(452, 160, 46, 23);
		frame.add(radioButton_1);

		JLabel lblDoYouWant_2 = new JLabel("Do you want to specify Template IDs?");
		lblDoYouWant_2.setBounds(76, 236, 287, 14);
		frame.add(lblDoYouWant_2);

		JRadioButton radioButton_2 = new JRadioButton("Yes.");
		radioButton_2.setBounds(369, 232, 54, 23);
		frame.add(radioButton_2);

		JRadioButton radioButton_3 = new JRadioButton("No.");
		radioButton_3.setSelected(true);
		radioButton_3.setBounds(452, 232, 46, 23);
		frame.add(radioButton_3);

		JButton button = new JButton("Select your folder location");
		button.setEnabled(false);
		button.setBounds(545, 223, 191, 40);
		frame.add(button);

		rdbtnYes = new JRadioButton("Yes.");
		rdbtnYes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnNo.setSelected(false);

				rdbtnYes.setText("Yes. (Loading All Existing Templates...)");
				
				lblDoYouWant_1.setVisible(true);
				radioButton.setVisible(true);
				radioButton_1.setVisible(true);
				btnNewButton_1.setVisible(true);
				lblDoYouWant_2.setVisible(true);
				radioButton_2.setVisible(true);
				radioButton_3.setVisible(true);
				button.setVisible(true);

				scrollPane.setVisible(true);
				btnNewButton.setVisible(true);
				btnNewButton.setEnabled(true);
				btnNextProcess.setEnabled(false);

				if (UserData.oldEnvTemplates == null || UserData.oldEnvTemplates.size() == 0) {
					btnNewButton.setEnabled(false);
					// load all templates in old env
					new Thread(new Runnable() {

						@Override
						public void run() {
							try {

								Map<String, String> oldEnvTemplates = TemplateService.getInstance()
										.getOldEnvTemplates();
								System.out.println(oldEnvTemplates);
								System.out.println(oldEnvTemplates.size());

								// add labels
								scrollPane.setVisible(false);
								addLabels(oldEnvTemplates);
								scrollPane.setVisible(true);

								btnNewButton.setEnabled(true);
								
								rdbtnYes.setText("Yes.");
								
								scrollPane.repaint();
							} catch (Exception e1) {
								e1.printStackTrace();
								// show connection test fail dialog
								JOptionPane.showMessageDialog(frame, e1.getMessage(),
										"Fail load old environment Templates", JOptionPane.ERROR_MESSAGE);
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

				lblDoYouWant_1.setVisible(false);
				radioButton.setVisible(false);
				radioButton_1.setVisible(false);
				btnNewButton_1.setVisible(false);
				lblDoYouWant_2.setVisible(false);
				radioButton_2.setVisible(false);
				radioButton_3.setVisible(false);
				button.setVisible(false);

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
				JTextArea ta = new JTextArea(20, 50);
				ta.setText(errorMsg);
				ta.setEditable(false);

				JOptionPane.showMessageDialog(frame, new JScrollPane(ta), "Copy Template Error",
						JOptionPane.ERROR_MESSAGE);
				
			}
		});
		btnNewButton_2.setBounds(272, 591, 101, 53);
		btnNewButton_2.setVisible(false);
		frame.add(btnNewButton_2);
		rdbtnNo.doClick();
	}

	private void addLabels(Map<String, String> oldEnvTemplates) {
		// to do
		int y = 57;

		Map.Entry<String, String>[] array = oldEnvTemplates.entrySet().toArray(new Map.Entry[oldEnvTemplates.size()]);

		// String[] oldEnvTemplatesArray = oldEnvTemplates.keySet().toArray(new
		// String[oldEnvTemplates.size()]);

		for (int i = 0; i < array.length; i++) {
			JLabel lblTestData = new JLabel(array[i].getKey() + " : " + array[i].getValue());
			lblTestData.setBounds(17, y, 500, 20);
			panel.add(lblTestData);

			// add copy template status column
			JLabel lblNewLabel_1 = new JLabel("");
			lblNewLabel_1.setBounds(550, y, 20, 20);
			panel.add(lblNewLabel_1);
			oldEnvTemplateCopyStatus.put(array[i].getKey(), lblNewLabel_1);

			y += 30;

			panel.setPreferredSize(new Dimension(scrollPane.getWidth() - 30, y + 30));
			// System.out.println(panel.getPreferredSize().getHeight());
		}
	}

	public void setCopyStatus(String oldTemplateId, boolean copyStatus) {
		scrollPane.setVisible(false);
		String statusText = copyStatus == true ? "√" : "×";
		oldEnvTemplateCopyStatus.get(oldTemplateId).setText(statusText);
		scrollPane.setVisible(true);
	}

}
