package Networking;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements Runnable {

	private volatile Thread control;
	private boolean serverSuspended;
	
	boolean error = false;
	int serverPort;
	ServerSocket serverSocket;
	Socket clientSocket;

	public ServerThread(int port) throws Exception {
		this.serverPort = port;
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
		int i = 1;
		while (thisThread == this.control) {
			
			try {
				if (this.serverSuspended) {
					synchronized (this) {
						while (this.serverSuspended)
							wait();
					}
				}
				this.clientSocket = serverSocket.accept();
	
				ClientHandler clientHandler = new ClientHandler(clientSocket, i++);
				clientHandler.start();
				

			} catch (InterruptedException e) {
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		System.out.println("\tEND OF SERVER THREAD");
	}

	public int getPort() {
		return serverPort;
	}

	private class ClientHandler implements Runnable {

		private volatile Thread control;
		private boolean clientHandlerSuspended;

		private Socket clientSocket;
		private PrintWriter out;
		private BufferedReader in;
		
		private int id;

		public ClientHandler(Socket socket, int id) {
			this.id = id;
			this.clientSocket = socket;
			try {
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				out = new PrintWriter(clientSocket.getOutputStream(), true);
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
			int i;
			while (thisThread == this.control) {

				String line = this.readFromClient();
				System.out.println("[S"+id+"] Got from C: " + line);
				System.out.println("[S"+id+"] SENDING:" + line);
				this.writeToClient(line);
			}
		}

		private String readFromClient() {
			String line;
			try {
				line = in.readLine();
			} catch (IOException e) {
				line = "Could not read from C";
			}
			return line;
		}

		private void writeToClient(String line) {
			out.println(line);
		}

	}

}
