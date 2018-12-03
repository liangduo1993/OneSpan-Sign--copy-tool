package com.esignlive.copytool.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import com.esignlive.copytool.App;
import com.esignlive.copytool.data.UserData;
import com.esignlive.copytool.service.EndpointService;
import com.esignlive.copytool.service.SenderService;
import com.esignlive.copytool.utils.InstanceUtil;

import lombok.Getter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

public class Process1 {

	@Getter
	private JPanel frame;
	private JTextField textField;
	private JTextField textField_1;
	private JButton btnNewButton;
	/**
	 * Create the application.
	 */
	public Process1() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize() {
		frame = new JPanel();
		frame.setVisible(true);
		frame.setBounds(100, 100, 800, 700);
		frame.setLayout(null);

		JLabel lblNewLabel = new JLabel("Source API KEY:");
		lblNewLabel.setBounds(114, 122, 120, 38);
		frame.add(lblNewLabel);

		JLabel lblSourceEnvironment = new JLabel("Source Environment:");
		lblSourceEnvironment.setBounds(114, 191, 120, 38);
		frame.add(lblSourceEnvironment);

		JLabel lblDestinationApiKey = new JLabel("Destination API KEY:");
		lblDestinationApiKey.setBounds(114, 323, 120, 38);
		frame.add(lblDestinationApiKey);

		JLabel lblDestinationEnvironment = new JLabel("Destination Environment:");
		lblDestinationEnvironment.setBounds(114, 392, 144, 38);
		frame.add(lblDestinationEnvironment);

		textField = new JTextField();
		textField.setBounds(277, 127, 326, 29);
		frame.add(textField);
		textField.setColumns(10);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(553, 35, 144, 14);
		progressBar.setValue(25);
		frame.add(progressBar);

		JLabel progressBarLabel = new JLabel("1/4");
		progressBarLabel.setBounds(708, 35, 46, 14);
		frame.add(progressBarLabel);

		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(InstanceUtil.endPointList.keySet().toArray()));
		comboBox.setBounds(277, 188, 190, 38);
		frame.add(comboBox);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(277, 323, 326, 29);
		frame.add(textField_1);

		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(InstanceUtil.endPointList.keySet().toArray()));
		comboBox_1.setBounds(277, 384, 190, 38);
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
							System.out.println((String)comboBox.getSelectedItem());
							
							
							UserData.sourceApiKey = textField.getText();
							UserData.sourceApiUrl = InstanceUtil.getUrlByKey((String)comboBox.getSelectedItem());
							UserData.destinationApiKey = textField_1.getText();
							UserData.destinationApiUrl = InstanceUtil.getUrlByKey((String)comboBox_1.getSelectedItem());
							
							EndpointService.getInstance().testConnection();
							btnNextProcess.setEnabled(true);
							
							//show success dialog
							JOptionPane.showMessageDialog(frame,
									"API Keys are all valid!",
								    "Connection test success",
								    JOptionPane.INFORMATION_MESSAGE);
							
							
						} catch (Exception exception) {
							exception.printStackTrace();
							
							//to do
							//show connection test fail dialog
							JOptionPane.showMessageDialog(frame,
									"Please make sure your API Keys are valid and in a match with Instance!",
								    "Connection test fail",
								    JOptionPane.ERROR_MESSAGE);
						}	
						btnNewButton.setEnabled(true);
					}
				}).start();
				
				
			}

			
		});
		btnNewButton.setBounds(145, 576, 144, 38);
		frame.add(btnNewButton);

		

		
	}
}
