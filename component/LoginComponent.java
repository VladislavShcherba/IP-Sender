package component;

import ip.IP;
import ip.IBMIPNotFoundException;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import connection.ZOSConnection;

public class LoginComponent extends JComponent {

	private static final long serialVersionUID = 1L;

	private JLabel idLabel = new JLabel("ID");
	private JLabel passwordLabel = new JLabel("Password");
	private JTextField idField = new JTextField(10);
	private JPasswordField passwordField = new JPasswordField(10);
	private JButton okButton = new JButton("Ok");

	private static final String DATASET_NAME = ".MYIP";
	private static final String HOST_NAME = "meggp.vipa.uk.ibm.com";

	private static final String SOCKET_ERROR_MESSAGE = "Can't resolve your IPs. AT&T IP wasn't sent to mainframe.";
	private static final String SOCKET_ERROR_TITLE = "Can't resolve your IPs!";
	private static final String NO_IBM_IP_ERROR_MESSAGE = "Can't find your AT&T IP. AT&T IP wasn't sent to mainframe.";
	private static final String NO_IBM_IP_ERROR_TITLE = "Can't find your AT&T IP!";
	private static final String CONNECTION_ERROR_MESSAGE = "Can't connect to mainframe.";
	private static final String CONNECTION_ERROR_TITLE = "Connection error!";
	private static final String LOGIN_ERROR_MESSAGE = "Unexpected error during login. AT&T IP wasn't sent to mainframe.";
	private static final String LOGIN_ERROR_TITLE = "Login unexpected error!";
	private static final String UNSUCCESSFUL_LOGIN_MESSAGE = "Invalid ID or password. Please try again!";
	private static final String UNSUCCESSFUL_LOGIN_TITLE = "Invalid password!";
	private static final String WRITE_ERROR_MESSAGE = "Unexpected error during writing your IP. AT&T IP wasn't sent to mainframe.";
	private static final String WRITE_ERROR_TITLE = "Writing unexpected error!";
	private static final String CLOSE_ERROR_MESSAGE = "Unexpected error while closing ZOS connection.";
	private static final String CLOSE_ERROR_TITLE = "Unexpected closing error!";
	private static final String RESOURCES_ERROR_MESSAGE = "Unexpected error while closing resources.";
	private static final String RESOURCES_ERROR_TITLE = "Unexpected closing error!";
	private static final String SUCCESS_MESSAGE = "Your AT&T IP address was sent successfully.";
	private static final String SUCCESS_TITLE = "Success!";

	public LoginComponent() {

		setLayout(new GridBagLayout());

		add(idLabel, new GBC(0, 0).setAnchor(GridBagConstraints.WEST));
		add(idField, new GBC(1, 0).setAnchor(GridBagConstraints.EAST));
		add(passwordLabel, new GBC(0, 1).setAnchor(GridBagConstraints.WEST));
		add(passwordField, new GBC(1, 1).setAnchor(GridBagConstraints.WEST));

		add(okButton, new GBC(0, 2, 2, 1).setAnchor(GridBagConstraints.CENTER));
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {

				boolean success = false;

				String id = idField.getText();
				String password = new String(passwordField.getPassword());
				String ip = null;
				try {
					ip = IP.getIBMIP();
				} catch (SocketException e) {
					JOptionPane.showMessageDialog(null, SOCKET_ERROR_MESSAGE,
							SOCKET_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				} catch (IBMIPNotFoundException e) {
					JOptionPane.showMessageDialog(null,
							NO_IBM_IP_ERROR_MESSAGE, NO_IBM_IP_ERROR_TITLE,
							JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}

				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				PrintWriter writer = new PrintWriter(new BufferedOutputStream(
						byteArrayOutputStream));
				writer.print(ip);
				writer.close();
				try {
					byteArrayOutputStream.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null,
							RESOURCES_ERROR_MESSAGE, RESOURCES_ERROR_TITLE,
							JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}
				InputStream inputStream = new BufferedInputStream(
						new ByteArrayInputStream(byteArrayOutputStream
								.toByteArray()));

				ZOSConnection connection = new ZOSConnection(HOST_NAME);
				try {
					connection.open();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null,
							CONNECTION_ERROR_MESSAGE, CONNECTION_ERROR_TITLE,
							JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}
				try {
					success = connection.login(id, password);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, LOGIN_ERROR_MESSAGE,
							LOGIN_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}

				if (!success) {
					JOptionPane.showMessageDialog(null,
							UNSUCCESSFUL_LOGIN_MESSAGE,
							UNSUCCESSFUL_LOGIN_TITLE,
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				try {
					connection
							.write("'" + id + DATASET_NAME + "'", inputStream);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, WRITE_ERROR_MESSAGE,
							WRITE_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}
				try {
					connection.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, CLOSE_ERROR_MESSAGE,
							CLOSE_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}
				try {
					inputStream.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null,
							RESOURCES_ERROR_MESSAGE, RESOURCES_ERROR_TITLE,
							JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}

				JOptionPane.showMessageDialog(null, SUCCESS_MESSAGE,
						SUCCESS_TITLE, JOptionPane.INFORMATION_MESSAGE);
				System.exit(0);
			}
		});
	}
}

class GBC extends GridBagConstraints {
	private static final long serialVersionUID = 1L;

	public GBC(int gridx, int gridy) {
		this.gridx = gridx;
		this.gridy = gridy;
	}

	public GBC(int gridx, int gridy, int gridwidth, int gridheight) {
		this.gridx = gridx;
		this.gridy = gridy;
		this.gridwidth = gridwidth;
		this.gridheight = gridheight;
	}

	public GBC setAnchor(int anchor) {
		this.anchor = anchor;
		return this;
	}

	public GBC setFill(int fill) {
		this.fill = fill;
		return this;
	}

	public GBC setWeight(double weightx, double weighty) {
		this.weightx = weightx;
		this.weighty = weighty;
		return this;
	}
}
