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

		JLabel lblNewLabel = new JLabel(
				"<html>Welcome to OneSpan Sign Copy Tool<br/>\r\n\r\n<br/><br/><br/>\r\n\r\nWhat has been changed:<br/>\r\nProgram will no longer download documents from template(as in sandbox env, the documents will contains the watermark), instead, program will read files in desktop/docs folder. Please add this \"docs\" folder to your desktop and collect all original documents in this path.\r\n<br/><br/>\r\nRequirement:<br/>\r\nAll document files names should be same (current implementation) or a mapping naming convention (you can contact developer@esignlive.com or make a post to ask for a customized code) to your template document name.\r\n<br/><br/>\r\nAdditional Function:<br/>\r\nSelect by template id function: is the same with how you choose sender, put all template ids(in old environment) in a csv file, and input your path when prompting it.\r\n<br/><br/>\r\nPlease direct send an email to developer@esignlive.com if the tool doesn't work properly, and please feel free to make a post sharing your suggestions to this tool!\r\n\r\n\r\n\r\n</html>");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(201, 44, 423, 411);
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
