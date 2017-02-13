package networking;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

import generals.Main;
import network_packages.EndOfRelationship;

public class ClientThread implements Runnable {

	private volatile Thread control;
	private boolean clientSuspended;

	private Socket clientSocket;
	private int clientID;

	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;

	public ClientThread(Socket socket) throws IOException {
		this.clientSocket = socket;
		this.clientID = -1;
		
		this.initStreams();
		
		this.start();
	}

	public void start() {
		control = new Thread(this);
		clientSuspended = false;
		control.start();
	}

	public void stop() {
		Thread tmp = control;
		control = null;
		tmp.interrupt();
		try {
			this.writeToServer(new EndOfRelationship(this.clientID));
			
			this.objectInputStream.close();
			this.objectOutputStream.close();
			this.clientSocket.close();
		} catch (IOException e) {} 
	}

	public void suspend() {
		this.clientSuspended = true;
	}

	public void resume() {
		synchronized (this) {
			this.clientSuspended = false;
			notifyAll();
		}
	}

	private void initStreams() throws IOException {
	
		objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
		objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
		
	}

	@Override
	public void run() {
		Object obj = null;
		Thread thisThread = Thread.currentThread();
		while (thisThread == this.control) {
			
			try {
				
				if (this.clientSuspended) {
					synchronized (this) {
						while (this.clientSuspended)
							wait();
					}
				}
				
				obj = this.readFromServer();
				Main.getGameClient().receivedFromServer(obj);
				
			} catch (InterruptedException e) {
				break;
			} catch (ClassNotFoundException e) {
				System.out.println("eerr1");
			} catch (InvalidClassException e) {
				System.out.println("eerr2");
			} catch (StreamCorruptedException e) {
				try {
					this.clientSocket.close();
				} catch (IOException ee) {}
				break;
			} catch (IOException e) {
				try {
					this.clientSocket.close();
				} catch (IOException ee) {}
				break;
			}
			
		}
		
		try {
			this.clientSocket.close();
		} catch (IOException e) {}

	}

	
	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	public void writeToServer(Object obj) throws IOException {
		this.objectOutputStream.writeObject(obj);
		this.objectOutputStream.flush();
		this.objectOutputStream.reset();
	}
	
	public Object readFromServer() throws IOException, ClassNotFoundException {
		return this.objectInputStream.readObject(); 
	}

	public int getClientID() {
		return clientID;
	}

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}
}
