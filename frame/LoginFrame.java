package frame;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import component.LoginComponent;

public class LoginFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final String BEFORE_EXIT_MESSAGE = "You are trying to exit without sending your AT&T address to mainframe!";
	private static final String BEFORE_EXIT_TITLE = "Can't exit now!";
	private static final String EXIT_MESSAGE = "Your AT&T IP address wasn't sent to mainframe!";
	private static final String EXIT_TITLE = "Force Exit!";

	private static enum ExitOption {

		FORCE_EXIT("Force Exit"), CANCEL("Cancel");

		String optionText;

		ExitOption(String optionText) {
			this.optionText = optionText;
		}

		@Override
		public String toString() {
			return optionText;
		}
	}

	public LoginFrame() {
		LoginComponent loginComponent = new LoginComponent();
		add(loginComponent);
		setTitle("IP Sender");
		moveToScreenCenter();

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				if (JOptionPane.showOptionDialog(LoginFrame.this, BEFORE_EXIT_MESSAGE,
						BEFORE_EXIT_TITLE, JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE, null, ExitOption.values(),
						ExitOption.CANCEL) == ExitOption.FORCE_EXIT.ordinal()) {
					JOptionPane.showMessageDialog(LoginFrame.this,
							EXIT_MESSAGE, EXIT_TITLE,
							JOptionPane.WARNING_MESSAGE);
					System.exit(0);
				}
			}
		});
	}

	public void moveToScreenCenter() {
		pack();
		Dimension dimensionAfterPack = getSize();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		int x = (int) (screenSize.width / 2 - dimensionAfterPack.getWidth() / 2);
		int y = (int) (screenSize.height / 2 - dimensionAfterPack.getHeight() / 2);
		this.setLocation(x, y);
	}
}
