package networking;

import java.io.IOException;
import java.net.Socket;

import generals.GameController;
import network_packages.PreGameInfo;
import network_packages.SocketPackage;

public class GameClient {

	GameController gameController;
	
	ClientThread clientThread;
	
	
	public GameClient(GameController gameController, Socket socket) throws IOException {
		this.gameController = gameController;
		this.clientThread = new ClientThread(socket);
	}

	public void startClient() {
		
		this.clientThread.start();

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
	
	public void respondToServer(Object obj) {
		try {
			this.clientThread.writeObject(obj);
		} catch (IOException e) {}
	}
	
	public ClientThread getClientThread() {
		return this.clientThread;
	}
	
	public int getClientID() {
		return this.clientThread.getClientID();
	}
}
