package network_packages;

import java.awt.Color;
import java.util.List;

public class SignalStartGame extends SocketPackage {
	private static final long serialVersionUID = 1;
	
	private double defaultCurveAngle;
	private double defaultCurveSpeed;
	private List<String> serverNames;
	private List<Color> serverColors;
	private List<String> otherNames;
	private List<Color> otherColors;
	
	
	public SignalStartGame(double defaultCurveAngle, double defaultCurveSpeed, List<String> serverNames, List<Color> serverColors, List<String> otherName, List<Color> otherColors) {
		super(42, SocketPackage.PACKAGE_SIGNAL_START_GAME);
		
		this.defaultCurveAngle = defaultCurveAngle;
		this.defaultCurveSpeed = defaultCurveSpeed;
		this.serverNames = serverNames;
		this.serverColors = serverColors;
		this.otherNames = otherNames;
		this.otherColors = otherColors;
		
	}

	public double getDefaultCurveAngle() {
		return defaultCurveAngle;
	}

	public double getDefaultCurveSpeed() {
		return defaultCurveSpeed;
	}

	public List<String> getNames() {
		return serverNames;
	}

	public List<Color> getColors() {
		return serverColors;
	}

	public List<String> getOtherNames() {
		return otherNames;
	}

	public List<Color> getOtherColors() {
		return otherColors;
	}
}
