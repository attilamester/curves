package network_packages;

import java.util.List;

import curve.Player;

public class PlayInfo extends SocketPackage {
	
	private List<Player> players;
	
	public PlayInfo(int clientID, List<Player> players) {
		super(clientID, SocketPackage.PACKAGE_PLAY_INFO);
		this.players = players;
	}

	public List<Player> getPlayers() {
		return players;
	}
	
}
