package network_packages;

import java.util.List;

import curve.Player;

public class PlayInfo extends SocketPackage {
	private static final long serialVersionUID = 1;
	
	private List<Player> players;
	private boolean preGame;
	
	public PlayInfo(int clientID, List<Player> players, boolean preGame) {
		super(clientID, SocketPackage.PACKAGE_PLAY_INFO);
		this.players = players;
		this.preGame = preGame;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public boolean isPreGame() {
		return preGame;
	}
	
	
}
