package network_packages;

import java.io.Serializable;

public class SocketPackage  implements Serializable {
	private static final long serialVersionUID = 1;
	
	public static final int PACKAGE_HAND_SHAKE = 0;
	public static final int PACKAGE_PRE_GAME = 1;
	
	private int clientID;
	private int type;
	
	public SocketPackage(int clientID, int type) {
		this.clientID = clientID;
		this.type = type;
	}

	public int getClientID() {
		return clientID;
	}

	public int getType() {
		return type;
	}
	
}
