package network_packages;

import curve.Player;

public class SignalTurn extends SocketPackage {

	private static final long serialVersionUID = 1L;
	
	public static final int SIGNAL_LEFT_TRIGGERED = 0;
	public static final int SIGNAL_LEFT_STOPPED = 1;
	public static final int SIGNAL_RIGHT_STOPPED = 2;
	public static final int SIGNAL_RIGHT_TRIGGERED = 3;
	
	private Player player;
	private int signalType;

	public SignalTurn(Player player, int signalType) {
		super(0, SocketPackage.PACKAGE_SIGNAL_TURN);
		this.player = player;
		this.signalType = signalType;
	}

	public Player getPlayer() {
		return player;
	}

	public int getSignalType() {
		return signalType;
	}
	
	
}
