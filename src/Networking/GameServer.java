package networking;

import java.io.IOException;

import generals.GameController;
import network_packages.PreGameInfo;
import network_packages.SocketPackage;

public class GameServer {

	public static final int DEFAULT_SERVER_PORT = 5555;
	
	GameController gameController;
	
	ServerThread serverThread;
	
	
	public GameServer(GameController gameController, int serverPort) throws Exception {
		this.gameController = gameController;
		this.serverThread = new ServerThread(this, serverPort);
	}

	public void startServer() {
		
		this.serverThread.start();

	}

	/**
	 * Function to be invoked on SERVER program
	 * 
	 */	
	public synchronized void receivedFromClient(int clientID, Object o) {
		
		switch (((SocketPackage)o).getType()) {
			case SocketPackage.PACKAGE_HAND_SHAKE:
				this.respondToClient(clientID, new SocketPackage(clientID, SocketPackage.PACKAGE_HAND_SHAKE));
				break;
				
			case SocketPackage.PACKAGE_PRE_GAME:
				PreGameInfo packet = (PreGameInfo)o;
				this.gameController.getLandingWindow().getLanGameConfigPanel().arrivedNewPlayerConfigs(
					packet.getClientID(), packet.getPlayers());
				break;
		}
	}

	/**
	 * Function to be invoked on CLIENT program
	 * 
	 */	
	public void receivedFromServer(Object o) {
		switch (((SocketPackage)o).getType()) {
		case SocketPackage.PACKAGE_HAND_SHAKE:
			//this.respondToClient(clientID, new SocketPackage(clientID, SocketPackage.PACKAGE_HAND_SHAKE));
			break;
			
		case SocketPackage.PACKAGE_PRE_GAME:
			PreGameInfo packet = (PreGameInfo)o;
			this.gameController.getLandingWindow().getJoinGameConfigPanel().arrivedNewPlayerConfigs(
				packet.getClientID(), packet.getPlayers());
			break;
	}
	}
	
	private void respondToClient(int clientID, Object o) {
		try {
			this.serverThread.getClients().get(clientID).writeObject(o);
		} catch (IOException e) {}
	}
	
	public ServerThread getServerThread() {
		return serverThread;
	}
	

}
