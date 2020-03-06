package com.esignlive.copytool.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class OSSProxyPanel extends JPanel {
	public JTextField proxyScript,proxyAddress,proxyPort;
	public JRadioButton rdbtnProxyHost, rdbtnProxyAutomaticScript,rdbtnNoProxy;
	
	public OSSProxyPanel() {
		this.setBounds(0, 0, 630, 170);
		this.setLayout(null);
		
		rdbtnNoProxy = new JRadioButton("No Proxy");
		rdbtnNoProxy.setBounds(24, 22, 200, 50);
		this.add(rdbtnNoProxy);
		
		rdbtnProxyHost = new JRadioButton("Proxy: Manual Setup");
		rdbtnProxyHost.setBounds(228, 22, 200, 50);
		this.add(rdbtnProxyHost);
				
		rdbtnProxyAutomaticScript = new JRadioButton("Proxy: Automatic Script");
		rdbtnProxyAutomaticScript.setBounds(430, 22, 200, 50);
		this.add(rdbtnProxyAutomaticScript);
		
		proxyScript = new JTextField();
		proxyScript.setBounds(174, 86, 433, 29);
		this.add(proxyScript);
		proxyScript.setColumns(10);
				
		proxyAddress = new JTextField();
		proxyAddress.setBounds(24, 126, 261, 29);
		this.add(proxyAddress);
		proxyAddress.setColumns(10);
						
		proxyPort = new JTextField();
		proxyPort.setBounds(326, 126, 61, 29);
		this.add(proxyPort);
		proxyPort.setColumns(10);
		
										
		JLabel lblProxyPort = new JLabel("Proxy Port");
		lblProxyPort.setBounds(322, 79, 169, 43);
		this.add(lblProxyPort);
		
		JLabel lblAutomaticProxyScript = new JLabel("Script Address");
		lblAutomaticProxyScript.setBounds(24, 79, 169, 43);
		this.add(lblAutomaticProxyScript);
		
		JLabel lblProxyAddress = new JLabel("Proxy Address");
		lblProxyAddress.setBounds(24, 79, 169, 43);
		this.add(lblProxyAddress);
		lblProxyAddress.setVisible(false);
		lblAutomaticProxyScript.setVisible(false);
		lblProxyPort.setVisible(false);
		proxyPort.setVisible(false);
		proxyAddress.setVisible(false);
		proxyScript.setVisible(false);
				
				
		rdbtnProxyAutomaticScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdbtnProxyAutomaticScript.isSelected()) {
					rdbtnProxyAutomaticScript.setEnabled(false);
					rdbtnProxyHost.setEnabled(true);
					rdbtnNoProxy.setEnabled(true);
					
					rdbtnProxyHost.setSelected(false);
					rdbtnNoProxy.setSelected(false);

					lblProxyAddress.setVisible(false);
					lblProxyPort.setVisible(false);
					proxyAddress.setVisible(false);
					proxyPort.setVisible(false);
					lblAutomaticProxyScript.setVisible(true);
					proxyScript.setVisible(true);
				}
			}
		});
		
		rdbtnProxyHost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdbtnProxyHost.isSelected()) {
					rdbtnProxyAutomaticScript.setEnabled(true);
					rdbtnProxyHost.setEnabled(false);
					rdbtnNoProxy.setEnabled(true);
					
					
					rdbtnProxyAutomaticScript.setSelected(false);
					rdbtnNoProxy.setSelected(false);
					
					lblProxyAddress.setVisible(true);
					lblProxyPort.setVisible(true);
					proxyAddress.setVisible(true);
					proxyPort.setVisible(true);
					lblAutomaticProxyScript.setVisible(false);
					proxyScript.setVisible(false);
				}
			}
		});
		
		rdbtnNoProxy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdbtnNoProxy.isSelected()) {
					rdbtnProxyAutomaticScript.setEnabled(true);
					rdbtnProxyHost.setEnabled(true);
					rdbtnNoProxy.setEnabled(false);
					
					rdbtnProxyHost.setSelected(false);
					rdbtnProxyAutomaticScript.setSelected(false);

					lblProxyAddress.setVisible(false);
					lblProxyPort.setVisible(false);
					proxyAddress.setVisible(false);
					proxyPort.setVisible(false);
					lblAutomaticProxyScript.setVisible(false);
					proxyScript.setVisible(false);
				}
			}
		});
		rdbtnNoProxy.doClick();
	}
	
}
