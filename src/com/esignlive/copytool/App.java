package com.esignlive.copytool;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
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
	 * 
	 * @throws IOException
	 */
	public App() throws IOException {
		mainFrame = new JFrame();
		mainFrame.setBounds(100, 100, 800, 700);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(null);
		InputStream is = App.class.getResourceAsStream("/favicon.775850b.ico");
		BufferedImage image = ImageIO.read(is);
		mainFrame.setIconImage(image);
		mainFrame.setTitle("OneSpan Sign - Template & Layout Copy Tool - v1.5.1");
		setMainFrame(new Process0().getFrame());
	}

}
