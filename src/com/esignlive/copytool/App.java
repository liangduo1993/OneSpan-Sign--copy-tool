package com.esignlive.copytool;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.esignlive.copytool.view.Process0;

public class App {

	private static JFrame mainFrame;
	
	
	public static void setMainFrame(JPanel frame) {
		mainFrame.setVisible(false);
		mainFrame.setContentPane(frame);
		mainFrame.setVisible(true);
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new App();
					mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public App() {
		mainFrame = new JFrame();
		mainFrame.setBounds(100, 100, 800, 700);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(null);
		
		setMainFrame(new Process0().getFrame());
	}
	
	
	

}
