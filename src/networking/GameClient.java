package networking;

import java.io.IOException;
import java.net.Socket;

import curve.Player;
import generals.GameController;
import network_packages.PlayInfoPlayers;
import network_packages.PlayInfoPowerUp;
import network_packages.PreGameInfo;
import network_packages.SignalStartGame;
import network_packages.SignalTurn;
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
	public void receivedFromServer(Object obj) {
		switch (((SocketPackage) obj).getType()) {
		case SocketPackage.PACKAGE_HAND_SHAKE:
			// this.respondToClient(clientID, new SocketPackage(clientID,
			// SocketPackage.PACKAGE_HAND_SHAKE));
			break;

		case SocketPackage.PACKAGE_BREAK_UP:
			this.clientThread.stop();
			this.gameController.getLandingWindow().getJoinGameConfigPanel().serverWasClosed();
			break;

		case SocketPackage.PACKAGE_PRE_GAME:
			PreGameInfo packet = (PreGameInfo) obj;
			this.gameController.getLandingWindow().getJoinGameConfigPanel()
					.arrivedNewPlayerConfigs(packet.getClientID(), packet.getPlayers());
			break;

		case SocketPackage.PACKAGE_SIGNAL_START_GAME:
			SignalStartGame signal = (SignalStartGame) obj;
			this.gameController.getLandingWindow().getJoinGameConfigPanel().startGame(signal.getDefaultCurveAngle(),
					signal.getDefaultCurveSpeed(), signal.getPlayers());
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
				this.gameController.getCurveWindow().getPlayGround().arrivedPreGamePlayerList(0, info.getPlayers());
			} else {
				this.gameController.getCurveWindow().getPlayGround().arrivedPlayerList(0, (PlayInfoPlayers) obj);
			}
			
			break;
		
		case SocketPackage.PACKAGE_PLAY_INFO_POWER_UP:
			PlayInfoPowerUp powerUpWrapper = (PlayInfoPowerUp)obj;
			this.gameController.getCurveWindow().getPlayGround().arrivedPowerUp(powerUpWrapper.getNewPowerUp());
			
			break;
		}

	}

	public void respondToServer(Object obj) {
		try {
			this.clientThread.writeToServer(obj);
		} catch (IOException e) {
			System.out.println("Could not write to server:" + ((SocketPackage) obj).getType());
			e.printStackTrace();
			System.exit(0);
		}
	}

	public ClientThread getClientThread() {
		return this.clientThread;
	}

	public int getClientID() {
		return this.clientThread.getClientID();
	}

	public void shutDown() {
		this.clientThread.stop();
	}
}
