package networking;

import java.io.IOException;

import curve.Player;
import generals.GameController;
import network_packages.PlayInfoPlayers;
import network_packages.PreGameInfo;
import network_packages.ReadyRequest;
import network_packages.SignalTurn;
import network_packages.SocketPackage;
import networking.ServerThread.ClientHandler;

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
		System.out.println("SERVER GOT TYPE:" + ((SocketPackage)obj).getType());
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
				break;
			
			case SocketPackage.PACKAGE_SIGNAL_PAUSE_GAME:
				this.gameController.getCurveWindow().getPlayGround().stopEvent();
				break;
			case SocketPackage.PACKAGE_SIGNAL_RESUME_GAME:
				this.gameController.getCurveWindow().getPlayGround().resumeEvent();
				break;
				
			case SocketPackage.PACKAGE_SIGNAL_TURN:
				SignalTurn sign = (SignalTurn)obj;
				Player player = null;
				for (Player p : this.gameController.getCurveWindow().getPlayGround().getAllPlayers()) {
					if (p.getColor().equals(sign.getPlayer().getColor())) {
						player = p;
					}
				}
				
				switch (sign.getSignalType()) {
				case SignalTurn.SIGNAL_LEFT_TRIGGERED:
					this.gameController.getCurveWindow().getPlayGround().leftTurnTriggered(player); break;
				case SignalTurn.SIGNAL_RIGHT_TRIGGERED:
					this.gameController.getCurveWindow().getPlayGround().rightTurnTriggered(player); break;
				case SignalTurn.SIGNAL_LEFT_STOPPED:
					this.gameController.getCurveWindow().getPlayGround().leftTurnStopped(player); break;
				case SignalTurn.SIGNAL_RIGHT_STOPPED:
					this.gameController.getCurveWindow().getPlayGround().rightTurnStopped(player); break;
				}
				
				break;
				
			case SocketPackage.PACKAGE_PLAY_INFO_PLAYERS:
				PlayInfoPlayers info = (PlayInfoPlayers)obj;				
				if (info.isPreGame()) {
					this.gameController.getCurveWindow().getPlayGround().arrivedPreGamePlayerList(clientID, info.getPlayers());
					for (ClientHandler clientHandler : this.serverThread.getClients().values()) {
						if (clientHandler.getClientID() != clientID) {
							try {
								clientHandler.writeToClient(new PlayInfoPlayers(0, info.getPlayers(), true));
							} catch (IOException ex) {
							}
						}
						
					}
				} else {
					this.gameController.getCurveWindow().getPlayGround().arrivedPlayerList(clientID, info);
				}
				
				break;
				
		}
	}

	private void respondToClient(int clientID, Object o) {
		try {
			this.serverThread.getClients().get(clientID).writeToClient(o);
		} catch (IOException e) {}
	}
	
	public void writeToAllClients(Object o) {
		for (ClientHandler clientHandler : this.serverThread.getClients().values()) {
			try {
				clientHandler.writeToClient(o);
			} catch (IOException ex) {
				System.out.println("COULD NOT WRITE TO CLIENT - AT BROADCASTING");
			}
		}
	}
	
	public ServerThread getServerThread() {
		return serverThread;
	}
	
	public void shutDown() {
		this.serverThread.stop();
	}
}
