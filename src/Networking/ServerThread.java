package networking;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerThread implements Runnable {

	public static int clientCount = 0;
	
	private GameServer gameServer;
	
	private volatile Thread control;
	private boolean serverSuspended;
	
	boolean error = false;
	int serverPort;
	ServerSocket serverSocket;
	Socket clientSocket;

	private HashMap<Integer, ClientHandler> clients;
	
	public ServerThread(GameServer gameServer, int port) throws Exception {
		this.gameServer = gameServer;
		this.serverPort = port;
		this.clients = new HashMap<>();
		
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			error = true;
			throw new Exception();
		}
	}

	public void start() {
		control = new Thread(this);
		serverSuspended = false;
		control.start();
	}

	public void stop() {
		Thread tmp = control;
		control = null;
		tmp.interrupt();
	}

	public void suspend() {
		this.serverSuspended = true;
	}

	public void resume() {
		synchronized (this) {
			this.serverSuspended = false;
			notifyAll();
		}
	}

	@Override
	public void run() {
		if (error)
			return;

		Thread thisThread = Thread.currentThread();
		while (thisThread == this.control) {
			
			try {
				if (this.serverSuspended) {
					synchronized (this) {
						while (this.serverSuspended)
							wait();
					}
				}
				this.clientSocket = serverSocket.accept();
	
				ClientHandler clientHandler = new ClientHandler(this.gameServer, clientSocket, ServerThread.clientCount);
				this.clients.put(ServerThread.clientCount++, clientHandler);
				clientHandler.start();
				

			} catch (InterruptedException e) {
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	public int getPort() {
		return serverPort;
	}
	
	public HashMap<Integer, ClientHandler> getClients() {
		return clients;
	}

	public class ClientHandler implements Runnable {

		private GameServer server;
		
		private volatile Thread control;
		private boolean clientHandlerSuspended;

		private Socket clientSocket;
		private PrintWriter out;
		private BufferedReader in;
		
		private ObjectOutputStream objectOutputStream;
		private ObjectInputStream objectInputStream;
		
		private int clientID;

		public ClientHandler(GameServer server, Socket socket, int id) {
			System.out.println("GOT C");
			
			this.server = server;
			this.clientID = id;
			this.clientSocket = socket;
			try {
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				
				objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
				objectOutputStream.flush();
				objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			} catch (IOException e) {
				System.out.println("ERROR - ClientHandler in-out");
			}
		}

		public void start() {
			control = new Thread(this);
			clientHandlerSuspended = false;
			control.start();
		}

		public void stop() {
			Thread tmp = control;
			control = null;
			tmp.interrupt();
		}

		public void suspend() {
			this.clientHandlerSuspended = true;
		}

		public void resume() {
			synchronized (this) {
				this.clientHandlerSuspended = false;
				notifyAll();
			}
		}

		@Override
		public void run() {

			Thread thisThread = Thread.currentThread();
			while (thisThread == this.control) {
				/*
				System.out.println(clientSocket.isConnected());
				System.out.println(clientSocket.isBound());
				System.out.println(clientSocket.isClosed());
				System.out.println(clientSocket.isInputShutdown());
				System.out.println(clientSocket.isOutputShutdown());
				System.out.println(out.checkError());
				*/
				
				try {
					//System.out.println("before server read available:");
					//System.out.println(objectInputStream.available());
					
					Object o = null;
					o = this.readObject();
					
					server.receivedFromClient(this.clientID, o);
				} catch (ClassNotFoundException e) {
					System.out.println("eerr1");
				} catch (InvalidClassException e) {
					System.out.println("eerr2");
				} catch (StreamCorruptedException e) {
					System.out.println("eerr");
				} catch (IOException e) {
					try {
						this.clientSocket.close();
					} catch (IOException ee) {}
					this.server.getServerThread().clients.remove(this);
					break;
				}
				
			}
		}
		
		public void writeObject(Object obj) throws IOException {
			this.objectOutputStream.writeObject(obj); 
		}
		
		public Object readObject() throws IOException, ClassNotFoundException {
			return this.objectInputStream.readObject(); 
		}

	}
	

}
