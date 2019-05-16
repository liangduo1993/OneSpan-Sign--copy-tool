package com.esignlive.copytool.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.esignlive.copytool.App;

public class Process0 {
	private JPanel frame;

	public JPanel getFrame() {
		return frame;
	}

	public Process0() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JPanel();
		frame.setBounds(100, 100, 800, 700);
		frame.setLayout(null);

		
		String content = "<html>\r\n" + 
			
				"<br/><h4>Before you start:</h4>\r\n" + 
				"<br/>To be able to completely copy your accounts, please make sure your account level settings are the same. You can contact our support team at support@onespan.com to have these settled.\r\n" + 
				"<br/>\r\n" + 
				"<br/><h4>Applicable scenarios:</h4>\r\n" + 
				"<br/>If you want to copy the whole account:\r\n" + 
				"<br/>1. copy whole account to another whole account\r\n" + 
				"<br/>2. copy whole account to a sender\r\n" + 
				"<br/>If you only want to copy one sender:\r\n" + 
				"<br/>1. source and destination senders could be any of the account owner, manager or regular member.\r\n" + 
				"<br/>\r\n" + 
				"<br/><h4>Support Functions:</h4>\r\n" + 
				"<br/>1. Support both credentials: API KEY and Username/Password;\r\n" + 
				"<br/>2. Allow copy account within the same Instance, you can invite new emails and map them to senders in source account (if the new sender wasn't specified, all templates/layouts will be <br/>created by new owner);\r\n" + 
				"<br/>3. Easily check the account's Sender Limitation by clicking on the owner email;\r\n" + 
				"<br/>4. Enable uploading original documents instead of downloading documents with watermark if source environment was sandbox. (you need to collect all original documents in one folder with <br/>the same names as in templates);\r\n" + 
				"<br/>5. Allow choosing partial templates/layouts to copy.\r\n" + 
				"<br/>\r\n" + 
				"<br/>Please make a post on Developer Community (https://developer.esignlive.com/) if the tool doesn't work properly or you have any suggestions on this tool!"+
				"</html>";
		JLabel lblNewLabel = new JLabel(content);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(48, 30, 706, 509);
		frame.add(lblNewLabel);

		JButton btnNewButton = new JButton("Get Start");
		btnNewButton.addActionListener((new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				App.setMainFrame(new Process1().getFrame());
			}
		}));

		btnNewButton.setBounds(303, 578, 161, 48);
		frame.add(btnNewButton);
	}
}
