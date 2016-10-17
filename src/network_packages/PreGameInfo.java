package network_packages;

import java.util.List;

import landing_pages.LocalGameConfigPanel.PlayerConfigRow;

public class PreGameInfo extends SocketPackage {
	private static final long serialVersionUID = 1;
	
	private List<PlayerConfigRow> players;
	
	public PreGameInfo(int clientID, List<PlayerConfigRow> players) {
		super(clientID, SocketPackage.PACKAGE_PRE_GAME);
		this.players = players;
	}

	public List<PlayerConfigRow> getPlayers() {
		return players;
	}
}
