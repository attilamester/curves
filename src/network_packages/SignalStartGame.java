package network_packages;

import java.util.List;

import curve.Player;

public class SignalStartGame extends SocketPackage {
	private static final long serialVersionUID = 1;
	
	private double defaultCurveAngle;
	private double defaultCurveSpeed;
	private List<Player> players;
	
	public SignalStartGame(double defaultCurveAngle, double defaultCurveSpeed, List<Player> players) {
		super(42, SocketPackage.PACKAGE_SIGNAL_START_GAME);
		
		this.defaultCurveAngle = defaultCurveAngle;
		this.defaultCurveSpeed = defaultCurveSpeed;
		this.players = players;
		
	}

	public double getDefaultCurveAngle() {
		return defaultCurveAngle;
	}

	public double getDefaultCurveSpeed() {
		return defaultCurveSpeed;
	}

	public List<Player> getPlayers() {
		return players;
	}
	
}
