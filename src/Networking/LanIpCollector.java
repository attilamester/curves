package Networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import LandingPages.JoinGamePanel;

public class LanIpCollector implements Runnable {
	
	List<String> hosts;
	
	String ownIp;
	String subnet;
	
	JoinGamePanel joinPanel;
	
	public LanIpCollector(JoinGamePanel joinPanel) {
		this.joinPanel = joinPanel;
		try {
			this.ownIp = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {	
		}
		this.subnet = new String("192.168.0");
		hosts = new ArrayList<>();
	}
	
	@Override
	public void run() {
		this.checkHosts(subnet);
		this.joinPanel.searchDoneForHost(this.hosts);
	}
	
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
	

}
