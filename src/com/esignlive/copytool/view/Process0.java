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
				"Welcome to OneSpan Sign Copy Tool!<br/>\r\n" + 
				"<br/>\r\n" + 
				"Before you start:<br/>\r\n" + 
				"To be able to completely copy your accounts, please make sure your account level settings are the same. You can contact our support team at sign.support@onespan.com to have these settled.<br/>\r\n" + 
				"<br/>\r\n" + 
				"Support Functions:<br/>\r\n" + 
				"1. Support both credentials: API KEY and Username/Password;<br/>\r\n" + 
				"2. Allow copy account within same Instance, you can invite new emails and map them to senders in source account (if new sender wasn't specified, all templates/layouts will be created by new owner);<br/>\r\n" + 
				"3. Easily check account's Sender Limitation by clicking on the owner email;<br/>\r\n" + 
				"4. Enable uploading original documents instead of downloading documents with watermark if source environment was sandbox. (you need to collect all original documents in one folder with the same names as in templates);<br/>\r\n" + 
				"5. Allow choosing partial templates/layouts to copy.<br/>\r\n" + 
				"<br/>\r\n" + 
				"Please make a post on Developer Community (https://developer.esignlive.com/) if the tool doesn't work properly or you have any suggestions on this tool!<br/>\r\n" + 
				"</html>";
		JLabel lblNewLabel = new JLabel(content);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(49, 44, 706, 411);
		frame.add(lblNewLabel);

		JButton btnNewButton = new JButton("Get Start");
		btnNewButton.addActionListener((new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				App.setMainFrame(new Process1().getFrame());
			}
		}));

		btnNewButton.setBounds(307, 524, 161, 48);
		frame.add(btnNewButton);
	}
}
