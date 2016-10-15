package Networking;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread implements Runnable {

	private volatile Thread control;
	private boolean serverSuspended;

	private String hostName;
	private int port;
	private Socket clientSocket;

	private PrintWriter out;
	private BufferedReader in;

	public ClientThread(String hostName, int port) throws IOException {
		this.hostName = hostName;
		this.port = port;

		this.connectToHost();

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

	private void connectToHost() throws IOException {
		this.clientSocket = new Socket(hostName, port);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		
	}

	@Override
	public void run() {

		Thread thisThread = Thread.currentThread();

		int i = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		while (thisThread == this.control) {

			try {
				if (this.serverSuspended) {
					synchronized (this) {
						while (this.serverSuspended)
							wait();
					}
				}
				System.out.println("[C] WAITING TO CONSOLE INPUT:");
				try {
					line = br.readLine();
				} catch (IOException e) {
				}
				this.writeToServer(line);
				System.out.println("[C] SENT: ~" + line);
				System.out.println("[C] READING:");
				line = this.readFromServer();
				System.out.println("[C] Got from S: " + line);

			} catch (InterruptedException e) {
				break;
			}
		}

	}

	private String readFromServer() {
		String line;
		try {
			line = in.readLine();
		} catch (IOException e) {
			line = "Could not read from S";
		}
		return line;
	}

	private void writeToServer(String line) {
		out.println(line);
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
}
