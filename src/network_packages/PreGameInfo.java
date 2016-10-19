package network_packages;

import java.util.List;

import landing_pages.PlayerConfigRow.TextFieldPlaceholder;

public class PreGameInfo extends SocketPackage {
	private static final long serialVersionUID = 1;
	
	private List<TextFieldPlaceholder> players;
	
	public PreGameInfo(int clientID, List<TextFieldPlaceholder> players) {
		super(clientID, SocketPackage.PACKAGE_PRE_GAME);
		this.players = players;
	}

	public List<TextFieldPlaceholder> getPlayers() {
		return this.players;
	}
}
