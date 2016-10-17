package networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import landing_pages.JoinGameConfigPanel;

public class LanIpTester implements Runnable {
	
	String ownIp;
	String remoteHost;
	
	JoinGameConfigPanel joinPanel;
	
	public LanIpTester(JoinGameConfigPanel joinPanel, String remoteHost) {
		this.joinPanel = joinPanel;
		try {
			this.ownIp = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {	
		}
		this.remoteHost = remoteHost;
		
	}
	
	@Override
	public void run() {
		if (remoteHost == ownIp) {
			joinPanel.connectingError("Can not connect to same host");
		} else {
			Socket socket = null;
			try {
				socket = new Socket(remoteHost, GameServer.DEFAULT_SERVER_PORT);
			} catch (UnknownHostException e) {
				joinPanel.connectingError("Host not found.");
				return;
			} catch (IOException e) {
				joinPanel.connectingError("Could not connect to server");
				return;
			}
			joinPanel.connectingSuccess(socket);
		}
		
	}
	
	/**
	private void checkHosts(String subnet) {
		int timeout = 500;
		for (int i = 100, count = 0; i < 255 && count < 255; ++i, ++count) {
			i %= 256;
			String host = subnet + "." + i;
			try {
				if (InetAddress.getByName(host).isReachable(timeout)) {
					if (host != ownIp ) {
						hosts.add(host);
						System.out.println(host);
					}
				}
			} catch (IOException e) {}
		}
	}
	*/

}
