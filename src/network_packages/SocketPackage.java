package network_packages;

import java.io.Serializable;

public class SocketPackage  implements Serializable {
	private static final long serialVersionUID = 1;
	
	public static final int PACKAGE_HAND_SHAKE = 0;
	public static final int PACKAGE_BREAK_UP = 1;
	public static final int PACKAGE_PRE_GAME = 2;
	public static final int PACKAGE_READY_REQUEST = 3;
	
	public static final int PACKAGE_SIGNAL_START_GAME = 10;
	public static final int PACKAGE_SIGNAL_PAUSE_GAME = 11;
	public static final int PACKAGE_SIGNAL_RESUME_GAME = 12;
	public static final int PACKAGE_SIGNAL_TURN = 13;
	
	public static final int PACKAGE_PLAY_INFO_PLAYERS = 100;
	public static final int PACKAGE_PLAY_INFO_POWER_UP = 101;
	
	private static int count = 0;
	
	private int id;
	private int clientID;
	private int type;
	
	public SocketPackage(int clientID, int type) {
		this.clientID = clientID;
		this.type = type;
		this.id = SocketPackage.count++;
	}

	public int getClientID() {
		return clientID;
	}

	public int getType() {
		return type;
	}
	
	public int getId() {
		return id;
	}
	
}
