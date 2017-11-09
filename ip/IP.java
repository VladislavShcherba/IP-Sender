package ip;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IP {

	public static String getIBMIP() throws SocketException, IBMIPNotFoundException {
		
		boolean ipFound = false;
		String result = null;

		Enumeration<NetworkInterface> networkInteraces = NetworkInterface.getNetworkInterfaces();
		while (networkInteraces.hasMoreElements() && !ipFound) {
			NetworkInterface networkInterace = networkInteraces.nextElement();
			Enumeration<InetAddress> inetAddresses = networkInterace.getInetAddresses();
			while (inetAddresses.hasMoreElements() && !ipFound) {
				InetAddress inetAddress = inetAddresses.nextElement();
				if (inetAddress.getAddress()[0] == 9) {
					ipFound = true;
					result = inetAddress.getHostAddress();
				}
			}
		}
		
		if(!ipFound) {
			throw new IBMIPNotFoundException();
		}
		return result;
	}
	
}
