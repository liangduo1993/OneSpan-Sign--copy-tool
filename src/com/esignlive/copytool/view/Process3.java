package com.esignlive.copytool.view;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import lombok.Getter;

public class Process3 {
	@Getter
	private JPanel frame;
	private JTable table;

	

	/**
	 * Create the application.
	 */
	public Process3() {
		initialize();
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
		btnNewButton.setBounds(125, 560, 163, 53);
		frame.add(btnNewButton);
		
		JButton btnNextProcess = new JButton("Next Process");
		btnNextProcess.setEnabled(false);
		btnNextProcess.setBounds(500, 560, 163, 53);
		frame.add(btnNextProcess);
		
		JLabel lblDoYouWant = new JLabel("Do you want to copy Templates?");
		lblDoYouWant.setBounds(76, 62, 217, 14);
		frame.add(lblDoYouWant);
		
		JRadioButton rdbtnYes = new JRadioButton("Yes.");
		rdbtnYes.setBounds(76, 87, 109, 23);
		frame.add(rdbtnYes);
		
		JRadioButton rdbtnNo = new JRadioButton("No.");
		rdbtnNo.setBounds(76, 125, 491, 23);
		frame.add(rdbtnNo);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(65, 178, 680, 348);
		frame.add(scrollPane);
		
		JPanel panel = new JPanel();
		scrollPane.setViewportView(panel);
		panel.setLayout(null);
		
		table = new JTable();
		table.setBounds(10, 11, 658, 412);
		panel.add(table);
	}

}
