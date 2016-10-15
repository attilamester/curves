package Networking;
import java.net.Socket;

public class GameServer {

	public static final int DEFAULT_SERVER_PORT = 5555;
	
	ServerThread serverThread;
	
	public GameServer(int serverPort) throws Exception {
		this.serverThread = new ServerThread(serverPort);
	}

	public void startServer() {
		
		this.serverThread.start();

	}


		/**

		GameServer gameServer = null;
		try {
			gameServer = new GameServer(port);
			gameServer.startServer();
		} catch(Exception e) {
			
			System.out.println("Game server is already occupied!\nCreating client.");
			
			ClientThread client = new ClientThread("127.0.0.1", port);
			client.start();
		}

		*/
		

	

}
