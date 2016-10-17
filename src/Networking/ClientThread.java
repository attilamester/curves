package networking;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.net.Socket;

import generals.Main;

public class ClientThread implements Runnable {

	private volatile Thread control;
	private boolean serverSuspended;

	private String hostName;
	private int port;
	private Socket clientSocket;
	private int clientID;

	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;

	public ClientThread(/*String hostName, int port*/Socket socket) throws IOException {
		this.hostName = hostName;
		this.port = port;
		this.clientSocket = socket;
		this.clientID = -1;
		
		this.initStreams();
		
		this.start();
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

	private void initStreams() throws IOException {
	
		objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
		objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
		
	}

	@Override
	public void run() {

		Thread thisThread = Thread.currentThread();
		while (thisThread == this.control) {
			
			try {
				Object o = null;
				o = this.readObject();
				Main.getGameClient().receivedFromServer(o);
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
				break;
			}
			
		}
		
		try {
			this.clientSocket.close();
		} catch (IOException e) {
		}

	}

	
	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	public void writeObject(Object obj) throws IOException {
		this.objectOutputStream.writeObject(obj);
	}
	
	public Object readObject() throws IOException, ClassNotFoundException {
		return this.objectInputStream.readObject(); 
	}

	public int getClientID() {
		return clientID;
	}

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}
}
