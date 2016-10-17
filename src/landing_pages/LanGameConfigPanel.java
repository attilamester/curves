package landing_pages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import generals.Colors;
import generals.GameController;
import generals.Main;
import landing_pages.LocalGameConfigPanel.PlayerConfigRow.TextFieldPlaceholder;
import network_packages.PreGameInfo;
import networking.GameServer;
import networking.ServerThread.ClientHandler;

public class LanGameConfigPanel extends LocalGameConfigPanel {
	private static final long serialVersionUID = 1;
	
	private JPanel remotePlayersPane;
	private JLabel remotePlayersPaneHeader;
	private JPanel remotePlayersPaneContent;
	
	private Map<Integer, List<TextFieldPlaceholder>> remotePlayers;
	
	public LanGameConfigPanel(int width, int height) {
		super(width, height);

		remotePlayers = new HashMap<>();
		
		customizePanel();
		
		try {
			Main.setGameServer(new GameServer(Main.getGameController(), GameServer.DEFAULT_SERVER_PORT));
			Main.getGameServer().startServer();
		} catch(Exception e) {
			
		}

	}
	
	
	private void customizePanel() {
		
		setTitleActions();
		alterPlayerConfigsContent();
		
		addComponentListener();
		
	}
	
	private void setTitleActions() {
		JPanel topPane = this.getTopPane();
		topPane.setLayout(new GridLayout(4, 2));

		JLabel roomLabel = new JLabel("Room IP:");
		roomLabel.setOpaque(true);
		roomLabel.setBorder(new EmptyBorder(5, 10, 5, 0));
		roomLabel.setBackground(Colors.MAIN_COLORS[4]);
		roomLabel.setForeground(Color.WHITE);

		String ip = "not found";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {}
		
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
		this.remotePlayersPaneHeader = new JLabel("Remote players:");
		this.remotePlayersPaneHeader.setOpaque(true);
		this.remotePlayersPaneHeader.setBorder(new EmptyBorder(5, 10, 5, 0));
		this.remotePlayersPaneHeader.setBackground(Colors.MAIN_COLORS[1]);
		this.remotePlayersPaneHeader.setForeground(Color.WHITE);
		
		this.remotePlayersPaneContent = new JPanel(new GridLayout(1, 1));
		this.remotePlayersPaneContent.setBackground(GameController.PLAYGROUND_BACKGROUND);
		
		JScrollPane remoteScroll = new JScrollPane(this.remotePlayersPaneContent);
		remoteScroll.getViewport().setBackground(GameController.PLAYGROUND_BACKGROUND);
		remoteScroll.setBorder(BorderFactory.createEmptyBorder());
		
		this.remotePlayersPane = new JPanel(new BorderLayout());
		this.remotePlayersPane.add(this.remotePlayersPaneHeader, BorderLayout.NORTH);
		this.remotePlayersPane.add(remoteScroll, BorderLayout.CENTER);
		
		JPanel playersPane = new JPanel(new GridLayout(2, 1));
		playersPane.add(this.getScrollPane());
		playersPane.add(remotePlayersPane);
		
		this.getContentPane().add(playersPane, BorderLayout.CENTER);
		
	}
	
	private void addComponentListener(){
		this.getPlayersPane().addContainerListener(new ContainerAdapter() {
			@Override
			public void componentAdded(ContainerEvent e) {
				System.out.println(getPlayers().size());
				for (ClientHandler client : Main.getGameServer().getServerThread().getClients().values()) {
					try {
						client.writeObject(new PreGameInfo(0, LanGameConfigPanel.this.getPlayers()));
					} catch (IOException ex) {}
				}
			}
			@Override
			public void componentRemoved(ContainerEvent e) {
				System.out.println(getPlayers().size());
				for (ClientHandler client : Main.getGameServer().getServerThread().getClients().values()) {
					try {
						client.writeObject(new PreGameInfo(0, getPlayers()));
					} catch (IOException ex) {}
				}
			}
		});
		
	}
	
	public synchronized void arrivedNewPlayerConfigs(int clientID, List<PlayerConfigRow> players) {
		
		if (!this.remotePlayers.containsKey(clientID)) {
			List<TextFieldPlaceholder> remotePlayersInfos = new ArrayList<>();
			for (PlayerConfigRow row : players) {
				TextFieldPlaceholder box = row.getTextFieldPlaceholder();
				box.setEditable(false);
				box.setFocusable(false);
				box.setPreferredSize(new Dimension(150, 25));
				
				remotePlayersInfos.add(box);
			}
			this.remotePlayers.put(clientID, remotePlayersInfos);
		} else {
			this.remotePlayers.get(clientID).clear();
			for (PlayerConfigRow row : players) {
				TextFieldPlaceholder box = row.getTextFieldPlaceholder();
				box.setEditable(false);
				box.setPreferredSize(new Dimension(150, 25));
				box.setFocusable(false);
				
				this.remotePlayers.get(clientID).add(box);
			}
		}
		int size = 0;
		for (Map.Entry<Integer, List<TextFieldPlaceholder>> entry : this.remotePlayers.entrySet()) {
			size += entry.getValue().size();
		}
		
		
		this.remotePlayersPaneContent.removeAll();
		this.remotePlayersPaneContent.setLayout(new GridLayout(size, 1));
		
		for (Map.Entry<Integer, List<TextFieldPlaceholder>> entry : this.remotePlayers.entrySet()) {
			
			for (TextFieldPlaceholder box : entry.getValue()) {
				JPanel panel = new JPanel();
				panel.setBackground(GameController.PLAYGROUND_BACKGROUND);
				panel.add(box);
				this.remotePlayersPaneContent.add(panel);
			}
			
		}
		
		
		this.remotePlayersPaneContent.revalidate();
		this.remotePlayersPaneContent.repaint();
	}
	
}
