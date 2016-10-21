package landing_pages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import curve.Control;
import curve_window.CurveWindow;
import generals.Colors;
import generals.GameController;
import generals.Main;
import landing_pages.PlayerConfigRow.TextFieldPlaceholder;
import modals.CountDownModal;
import modals.ErrorDialog;
import network_packages.PreGameInfo;
import network_packages.SignalStartGame;
import networking.GameServer;
import networking.ServerThread.ClientHandler;

public class LanGameConfigPanel extends LocalGameConfigPanel {
	private static final long serialVersionUID = 1;

	private JPanel allPlayersPane;
	private JPanel remotePlayersPane;
	private JPanel remotePlayersPaneHeader;
	private JPanel remotePlayersPaneContent;
	private JScrollPane remotePlayersPaneContentScrollPane;
	private Map<Integer, List<TextFieldPlaceholder>> remotePlayers;
	private Map<Integer, Boolean> remoteClientsReadyState;

	private JLabel readyClientsNumber;
	private JLabel allClientsNumber;
	
	public LanGameConfigPanel(int width, int height) {
		super(width, height, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				if (Main.getGameServer() != null) {
					Main.getGameServer().shutDown();
				}
				return null;
			}
		});

		remotePlayers = new HashMap<>();
		remoteClientsReadyState = new HashMap<>();

		customizePanel();

		try {
			Main.setGameServer(new GameServer(Main.getGameController(), GameServer.DEFAULT_SERVER_PORT));
			Main.getGameServer().startServer();
		} catch (Exception e) {
				System.out.println("cannot");
		}

	}

	private void customizePanel() {

		setTitleActions();
		alterPlayerConfigsContent();
		addComponentListener();

	}

	private void setTitleActions() {
		JPanel topPane = this.getTopConfigPane();
		topPane.setLayout(new GridLayout(3, 2));

		JLabel roomLabel = new JLabel("Room IP:");
		roomLabel.setOpaque(true);
		roomLabel.setBorder(new EmptyBorder(5, 10, 5, 0));
		roomLabel.setBackground(Colors.MAIN_COLORS[4]);
		roomLabel.setForeground(Color.WHITE);

		String ip = "not found";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
		}

		JTextField roomTextBox = new JTextField(ip);
		roomTextBox.setOpaque(true);
		roomTextBox.setEditable(false);
		roomTextBox.setBorder(new EmptyBorder(5, 10, 5, 0));
		roomTextBox.setBackground(Colors.MAIN_COLORS[4]);
		roomTextBox.setForeground(Color.LIGHT_GRAY);
		roomTextBox.setFont(new Font("Calibri", Font.BOLD, 15));

		topPane.add(roomLabel, 0);
		topPane.add(roomTextBox, 1);
	}

	private void alterPlayerConfigsContent() {
		this.remotePlayersPaneHeader = new JPanel(new GridLayout(1, 2));
		
		JLabel title = new JLabel("Remote players:");
		title.setOpaque(true);
		title.setBorder(new EmptyBorder(5, 10, 5, 0));
		title.setBackground(Colors.MAIN_COLORS[1]);
		title.setForeground(Color.WHITE);
		
		JPanel clientsPanel = new JPanel(new GridLayout(1, 2));
		clientsPanel.setBackground(Colors.MAIN_COLORS[1]);
		JLabel clients = new JLabel("Clients: ", JLabel.RIGHT);
		clients.setOpaque(true);
		clients.setBorder(new EmptyBorder(5, 10, 5, 0));
		clients.setBackground(Colors.MAIN_COLORS[1]);
		clients.setForeground(Color.WHITE);
		
		JPanel readyPanel = new JPanel(new GridLayout(1, 3));
		readyPanel.setBackground(Colors.MAIN_COLORS[1]);
		JLabel per = new JLabel("/", JLabel.CENTER);
		per.setFont(new Font("Calibri", Font.BOLD, 20));
		per.setForeground(Color.WHITE);
		per.setOpaque(false);
		this.readyClientsNumber = new JLabel("0", JLabel.RIGHT);
		this.readyClientsNumber.setOpaque(false);
		this.readyClientsNumber.setBorder(new EmptyBorder(5, 10, 5, 0));
		this.readyClientsNumber.setForeground(Color.GREEN);
		this.allClientsNumber = new JLabel("0", JLabel.LEFT);
		this.allClientsNumber.setOpaque(false);
		this.allClientsNumber.setBorder(new EmptyBorder(5, 10, 5, 0));
		this.allClientsNumber.setForeground(Color.WHITE);
		readyPanel.add(this.readyClientsNumber);
		readyPanel.add(per);
		readyPanel.add(this.allClientsNumber);
		
		clientsPanel.add(clients);
		clientsPanel.add(readyPanel);
		
		this.remotePlayersPaneHeader.add(title);
		this.remotePlayersPaneHeader.add(clientsPanel);
		
		this.remotePlayersPaneContent = new JPanel(new GridLayout(1, 1));
		this.remotePlayersPaneContent.setBackground(GameController.PLAYGROUND_BACKGROUND);

		this.remotePlayersPaneContentScrollPane = new JScrollPane(this.remotePlayersPaneContent);
		this.remotePlayersPaneContentScrollPane.getViewport().setBackground(GameController.PLAYGROUND_BACKGROUND);
		this.remotePlayersPaneContentScrollPane.setBorder(BorderFactory.createEmptyBorder());

		this.remotePlayersPane = new JPanel(new BorderLayout());
		this.remotePlayersPane.add(this.remotePlayersPaneHeader, BorderLayout.NORTH);
		this.remotePlayersPane.add(this.remotePlayersPaneContentScrollPane, BorderLayout.CENTER);

		allPlayersPane = new JPanel(new GridLayout(2, 1));
		allPlayersPane.add(this.getLocalPlayersPane());
		allPlayersPane.add(remotePlayersPane);

		this.getContentPane().add(allPlayersPane, BorderLayout.CENTER);

	}

	private void addComponentListener() {
		this.getLocalPlayersPaneContent().addContainerListener(new ContainerAdapter() {
			@Override
			public void componentAdded(ContainerEvent e) {
				/**
				 * CHECK IF SERVER EXISTS!!!
				 */
				shareServerPlayersToClients();
			}

			@Override
			public void componentRemoved(ContainerEvent e) {
				shareServerPlayersToClients();
			}
		});
		
		this.start.removeMouseListener(this.start.getMouseListeners()[0]);
		this.start.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {										
				start.setBackground(new Color(0,0,0,0));
				start.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				start.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				start.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
				int ready = Integer.parseInt(readyClientsNumber.getText());
				int all   = Integer.parseInt(allClientsNumber.getText());
				
				if (ready != all) {
					new ErrorDialog("Clients are not ready");
					return;
				}
				
				List<Control> ctrl = new ArrayList<Control>();
				List<String> localNames = new ArrayList<String>();
				List<Color> localColors = new ArrayList<Color>();
				
				if (!checkPlayersCorrectness(localPlayerConfigRows, ctrl, localNames, localColors)) {
					return;
				}
				
				List<String> remoteNames = new ArrayList<String>();
				List<Color> remoteColors = new ArrayList<Color>();
				for (Component comp : remotePlayersPaneContent.getComponents()) {
					Color color = ((TextFieldPlaceholder)(((JPanel)comp).getComponent(0))).getColor();
					String name = ((TextFieldPlaceholder)(((JPanel)comp).getComponent(0))).getText();
					if (localColors.contains(color) || localNames.contains(name)) {
						new ErrorDialog("Colors and Names must be unique!");
						return;
					}
					remoteNames.add(name);
					remoteColors.add(color);
				}
				
				
				GameController.finished = false;				
				GameController.DEFAULT_CURVE_ANGLE = angleSlider.getValue() / 10;
				GameController.DEFAULT_CURVE_SPEED = speedSlider.getValue() / 100;
				
				for (ClientHandler clientHandler : Main.getGameServer().getServerThread().getClients().values()) {
					try {
						System.out.println("SERVER   START!!!!!!!!!!!!!!");
						clientHandler.writeToClient(new SignalStartGame(
							GameController.DEFAULT_CURVE_ANGLE, GameController.DEFAULT_CURVE_SPEED, localNames, localColors, remoteNames, remoteColors));
					} catch (IOException ex) {
						System.out.println("cound write start signal");
					}
				}
				
				CurveWindow curveWindow = new CurveWindow(ctrl, localNames, localColors, remoteNames, remoteColors);
				new CountDownModal(curveWindow, 1, null, null);
			}
		});

	}

	public void shareServerPlayersToClients() {
		for (ClientHandler clientHandler : Main.getGameServer().getServerThread().getClients().values()) {
			try {
				clientHandler.writeToClient(new PreGameInfo(0, collectTextFields()));
			} catch (IOException ex) {
			}
		}
	}

	public void arrivedNewPlayerConfigs(int clientID, List<TextFieldPlaceholder> players) {
		synchronized (new Object()) {
			
			System.out.println("[S] Got list of " + players.size() + " from " + clientID);
			for (TextFieldPlaceholder textBox : players) {
				System.out.println(textBox.getText() + textBox.getColor());
			}

			if (!this.remotePlayers.containsKey(clientID)) {
				System.out.println("itt ");
				List<TextFieldPlaceholder> remotePlayersInfos = new ArrayList<>();
				for (TextFieldPlaceholder textBox : players) {
					remotePlayersInfos.add(textBox);
				}
				this.remotePlayers.put(clientID, remotePlayersInfos);
			} else {
				this.remotePlayers.get(clientID).clear();
				for (TextFieldPlaceholder textBox : players) {
					this.remotePlayers.get(clientID).add(textBox);
				}
			}

			this.allClientsNumber.setText(Integer.toString(this.remotePlayers.size()));
			/******************
			 * BROADCAST TO OTHERS
			 */
			this.broadCastClientsTOClients();

			/*****************
			 * ADD TO LOCAL PANEL
			 */
			this.refreshRemotePlayersPanelList();
			
		}
	}
	
	public void newReadyRequest(int clientID, boolean ready) {
		this.remoteClientsReadyState.put(clientID, ready);
		
		if (ready) {
			this.incrementReadyClientsCount();
		} else {
			this.decrementReadyClientsCount();
		}
	}
	
	private void incrementReadyClientsCount() {
		this.readyClientsNumber.setText(Integer.toString(
				Integer.parseInt(this.readyClientsNumber.getText()) + 1));
	}
	
	private void decrementReadyClientsCount() {
		this.readyClientsNumber.setText(Integer.toString(
				Integer.parseInt(this.readyClientsNumber.getText()) - 1));
	}
	

	public void clientQuit(int clientID) {
		synchronized (new Object()) {
			
			if (this.remoteClientsReadyState.get(clientID)) { // client was READY
				this.decrementReadyClientsCount();
			}
			this.remoteClientsReadyState.remove(clientID);
			this.remotePlayers.remove(clientID);
			
			this.broadCastClientsTOClients();
			this.refreshRemotePlayersPanelList();
			
			this.allClientsNumber.setText(Integer.toString(this.remotePlayers.size()));
		}

	}
	
	private void broadCastClientsTOClients() {
		List<TextFieldPlaceholder> allOtherClientsPlayers = new ArrayList<>();
		for (Iterator<Integer> iter = this.remotePlayers.keySet().iterator(); iter.hasNext();) {
			int currentClient = iter.next();
			if (!Main.getGameServer().getServerThread().getClients().containsKey(currentClient)) {
				continue;
			}
			allOtherClientsPlayers.clear();

			for (Map.Entry<Integer, List<TextFieldPlaceholder>> entry : this.remotePlayers.entrySet()) {
				if (entry.getKey() == currentClient) {
					continue;
				}
				allOtherClientsPlayers.addAll(entry.getValue());

				try {
					Main.getGameServer().getServerThread().getClients().get(currentClient)
							.writeToClient(new PreGameInfo(42, allOtherClientsPlayers));
				} catch (IOException ex) {
				}
			}
		}
	}
	
	private void refreshRemotePlayersPanelList() {
		this.remotePlayersPaneContent.removeAll();
		
		List<TextFieldPlaceholder> allPlayers = new ArrayList<>();
		for (Map.Entry<Integer, List<TextFieldPlaceholder>> entry : this.remotePlayers.entrySet()) {
			allPlayers.addAll(entry.getValue());
		}
		this.addTextFieldListToPanel(this.remotePlayersPaneContent, allPlayers);

	}

}
