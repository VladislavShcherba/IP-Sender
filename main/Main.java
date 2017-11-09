package main;

import java.awt.EventQueue;

import frame.LoginFrame;

public class Main {

	public static void main(String... args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				LoginFrame frame = new LoginFrame();
				frame.setVisible(true);
			}
		});
	}
}
