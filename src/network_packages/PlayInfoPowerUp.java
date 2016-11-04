package network_packages;

import power_up.PowerUp;

public class PlayInfoPowerUp extends SocketPackage {
	private static final long serialVersionUID = 1;

	private PowerUp newPowerUp;
	
	public PlayInfoPowerUp(PowerUp newPowerUp) {
		super(0, SocketPackage.PACKAGE_PLAY_INFO_POWER_UP);
		this.newPowerUp = newPowerUp;
	}

	public PowerUp getNewPowerUp() {
		return newPowerUp;
	}
	
	
}