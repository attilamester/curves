package networking;

import java.io.IOException;

import generals.GameController;
import network_packages.PreGameInfo;
import network_packages.ReadyRequest;
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
	public synchronized void receivedFromClient(int clientID, Object obj) {
		
		switch (((SocketPackage)obj).getType()) {
			case SocketPackage.PACKAGE_HAND_SHAKE:
				this.respondToClient(clientID, new SocketPackage(clientID, SocketPackage.PACKAGE_HAND_SHAKE));
				this.respondToClient(clientID, new PreGameInfo(0,
					this.gameController.getLandingWindow().getLanGameConfigPanel().collectTextFields()));
				break;
			
			case SocketPackage.PACKAGE_BREAK_UP:
				this.serverThread.closeClient(clientID);
				this.gameController.getLandingWindow().getLanGameConfigPanel()
					.clientQuit(clientID);
				break;	
			
			case SocketPackage.PACKAGE_PRE_GAME:
				PreGameInfo packet = (PreGameInfo)obj;
				this.gameController.getLandingWindow().getLanGameConfigPanel().arrivedNewPlayerConfigs(
					clientID, packet.getPlayers());
				break;
			
			case SocketPackage.PACKAGE_READY_REQUEST:
				ReadyRequest request = (ReadyRequest)obj;
				this.gameController.getLandingWindow().getLanGameConfigPanel().newReadyRequest(clientID, request.isReady());
		}
	}

	private void respondToClient(int clientID, Object o) {
		try {
			this.serverThread.getClients().get(clientID).writeToClient(o);
		} catch (IOException e) {}
	}
	
	public ServerThread getServerThread() {
		return serverThread;
	}
	
	public void shutDown() {
		this.serverThread.stop();
	}
}
