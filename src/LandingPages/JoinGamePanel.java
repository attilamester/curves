package LandingPages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Generals.Colors;
import Generals.GameController;
import Generals.Main;
import LandingPages.ConfigPanel.PlayerConfigRow;
import Modals.ErrorDialog;
import Networking.ClientThread;
import Networking.GameServer;

public class JoinGamePanel extends JPanel {
	private static final long serialVersionUID = 1;

	private Random rnd;

	private JPanel allPlayersPane;
	private JPanel connectPane;
	private JPanel loadingPane;
	
	private JPanel otherPlayersPane;
	private JPanel otherPlayersPaneHeader;
	private JPanel otherPlayersPaneContent;
	private JScrollPane otherPlayersPaneContentScrollPane;

	private JPanel myPlayersPane;
	private JPanel myPlayersPaneHeader;
	private JPanel myPlayersPaneContent;
	private JScrollPane myPlayersPaneContentScrollPane;

	private JSpinner myPlayerCount;
	private List<PlayerConfigRow> myPlayers;
	
	private JSpinner subnetSpinner;

	public JoinGamePanel(int width, int height) {

		this.setLayout(new BorderLayout());
		this.setSize(width, height);

		addItems();
	}

	private void addItems() {
		Main.addBackPane(this);

		rnd = new Random();

		prepareGui();

		//ClientThread client = new ClientThread("127.0.0.1", port);
		//client.start();

		/*new Thread(
			new LanIpCollector(this))
		.start();*/
	}

	
	public void searchDoneForHost(List<String> hosts) {
		otherPlayersPaneContent.removeAll();
		otherPlayersPaneContent.revalidate();
		for (String s : hosts) {
			System.out.println(s);
		}
	}
	
	private void connectToHost() {
		
		String host = "192.168.0." + Integer.toString((Integer)this.subnetSpinner.getValue());
		
		
		try {
			Socket socket = new Socket(host, GameServer.DEFAULT_SERVER_PORT);
		} catch (IOException e) {
			addConnectPane();
			new ErrorDialog("Could not connect to peer");
		}
		
		otherPlayersPaneContent.removeAll();
		otherPlayersPaneContent.revalidate();
		
		ClientThread client = null;
		
		try {
			client = new ClientThread(host, GameServer.DEFAULT_SERVER_PORT);
			//client.start();
		} catch (IOException e) {
			
		}
		
		
	}
	
	/***********************************************************************************
	 *  GUI
	 ************************************************************************************/
	private void prepareGui() {
		allPlayersPane = new JPanel(new GridLayout(2, 1));

		addRemotePlayers();
		addLocalPlayers();

		this.add(allPlayersPane);
	}

	private void addRemotePlayers() {
		otherPlayersPane = new JPanel(new BorderLayout());
		otherPlayersPaneHeader = new JPanel(new GridLayout(1, 2));
		otherPlayersPaneContent = new JPanel(new BorderLayout());
		otherPlayersPaneContent.setBackground(Colors.ANIMATION_BACKGROUND);
		
		JLabel title = new JLabel("Remote IP:");
		title.setOpaque(true);
		title.setBorder(new EmptyBorder(5, 10, 5, 0));
		title.setBackground(Colors.MAIN_COLORS[1]);
		title.setForeground(Color.WHITE);

		JPanel ipAddressPane = new JPanel(new GridLayout(1, 2));
		ipAddressPane.setBackground(Colors.MAIN_COLORS[1]);
		JLabel ip_ = new JLabel("192.168.0.", JLabel.RIGHT);
		ip_.setBackground(Colors.MAIN_COLORS[1]);
		ip_.setForeground(Color.WHITE);
		
		SpinnerModel subnetModel = new SpinnerNumberModel(100, 1, 255, 1);
		this.subnetSpinner = new JSpinner(subnetModel);
		((JSpinner.DefaultEditor) subnetSpinner.getEditor()).getTextField().setEditable(false);
		((JSpinner.DefaultEditor) subnetSpinner.getEditor()).getTextField().setForeground(Color.WHITE);
		subnetSpinner.setBorder(BorderFactory.createEmptyBorder());
		subnetSpinner.setPreferredSize(new Dimension(50, 25));
		Component c = subnetSpinner.getEditor().getComponent(0);
		c.setFont(new Font("Calibri", Font.BOLD, 18));
		c.setBackground(Colors.MAIN_COLORS[1]);
		
		ipAddressPane.add(ip_);
		ipAddressPane.add(subnetSpinner);
		
		
		
		otherPlayersPaneHeader.add(title);
		otherPlayersPaneHeader.add(ipAddressPane);

		otherPlayersPaneContentScrollPane = new JScrollPane(otherPlayersPaneContent);
		otherPlayersPaneContentScrollPane.setBackground(GameController.PLAYGROUND_BACKGROUND);
		otherPlayersPaneContentScrollPane.getViewport().setBackground(GameController.PLAYGROUND_BACKGROUND);
		otherPlayersPaneContentScrollPane.setBorder(BorderFactory.createEmptyBorder());

		otherPlayersPane.add(otherPlayersPaneHeader, BorderLayout.NORTH);
		otherPlayersPane.add(otherPlayersPaneContentScrollPane, BorderLayout.CENTER);
		
		allPlayersPane.add(otherPlayersPane);
		
		/****************************************
		 * LOADING ? CONNECTING
		 *****************************************/
		
		createConnectingPane();
		createLoadingPane();
		
		addConnectPane();
		
	}
	
	private void createConnectingPane() {
		connectPane = new JPanel(new GridBagLayout());
		connectPane.setOpaque(false);
		JLabel connect = new JLabel("Connect", JLabel.CENTER);
		connect.setPreferredSize(new Dimension(100, 30));
		connect.setBackground(Colors.MAIN_COLORS[1]);
		connect.setForeground(Color.GREEN);
		connect.setBorder(new LineBorder(Color.GREEN, 2));
		connect.setFont(new Font("Calibri", Font.BOLD, 18));
		connectPane.add(connect);
		otherPlayersPaneContent.add(connectPane);
		
		connect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				addLoadingPane();
				connectToHost();
			}
		});
	}
	
	private void addConnectPane() {
		otherPlayersPaneContent.removeAll();
		otherPlayersPaneContent.add(this.connectPane);	
		otherPlayersPaneContent.revalidate();
	}
	
	private void createLoadingPane() {
		loadingPane = new JPanel(new BorderLayout());
		loadingPane.setOpaque(false);
		
		ImageIcon icon = new ImageIcon("images\\loading.gif");
		JLabel loading = new JLabel(new ImageIcon( icon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT) ));
		
		JLabel loadingLabel = new JLabel("Searching for host ...", JLabel.CENTER);
		loadingLabel.setBorder(new EmptyBorder(5, 10, 5, 0));
		loadingLabel.setForeground(Color.WHITE);
		
		loadingPane.add(loading, BorderLayout.CENTER);
		loadingPane.add(loadingLabel, BorderLayout.NORTH);
		
	}
	
	private void addLoadingPane() {
		otherPlayersPaneContent.removeAll();
		otherPlayersPaneContent.add(this.loadingPane);
		otherPlayersPaneContent.revalidate();
		otherPlayersPaneContent.repaint();
	}


	private void addLocalPlayers() {
		myPlayersPane = new JPanel(new BorderLayout());
		myPlayersPaneHeader = new JPanel(new GridLayout(1, 2));
		myPlayersPaneContent = new JPanel(new GridLayout(1, 1));
		myPlayersPaneContentScrollPane = new JScrollPane(myPlayersPaneContent);
		myPlayersPane.add(myPlayersPaneHeader, BorderLayout.NORTH);
		myPlayersPane.add(myPlayersPaneContentScrollPane, BorderLayout.CENTER);

		addNumberSpinner();
		addMyPlayers();

		allPlayersPane.add(myPlayersPane);
	}

	private void addNumberSpinner() {
		Color bg = Colors.MAIN_COLORS[1];

		SpinnerModel model = new SpinnerNumberModel(1, 1, 10, 1);
		this.myPlayerCount = new JSpinner(model);
		((JSpinner.DefaultEditor) myPlayerCount.getEditor()).getTextField().setEditable(false);
		((JSpinner.DefaultEditor) myPlayerCount.getEditor()).getTextField().setForeground(Color.WHITE);
		myPlayerCount.setPreferredSize(new Dimension(50, 25));
		myPlayerCount.setBorder(BorderFactory.createEmptyBorder());

		Component c = myPlayerCount.getEditor().getComponent(0);
		c.setFont(new Font("Calibri", Font.BOLD, 18));
		c.setBackground(bg);

		myPlayerCount.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int nr = (int) myPlayerCount.getValue();
				if (nr > myPlayers.size()) {
					myPlayersPaneContent.setLayout(new GridLayout(nr, 1));
					PlayerConfigRow row = new PlayerConfigRow(rnd);
					myPlayers.add(row);
					myPlayersPaneContent.add(row);
					myPlayersPaneContent.revalidate();
				} else {
					myPlayersPaneContent.setLayout(new GridLayout(myPlayers.size() - 1, 1));
					PlayerConfigRow ref = myPlayers.get(myPlayers.size() - 1);
					myPlayers.remove(ref);
					myPlayersPaneContent.remove(ref);
					myPlayersPaneContent.revalidate();
				}
			}
		});

		JLabel playerNrLabel = new JLabel("Number of players:");
		playerNrLabel.setOpaque(true);
		playerNrLabel.setBorder(new EmptyBorder(5, 10, 5, 0));
		playerNrLabel.setBackground(bg);
		playerNrLabel.setForeground(Color.WHITE);
		playerNrLabel.setLabelFor(myPlayerCount);

		myPlayersPaneHeader.add(playerNrLabel);
		myPlayersPaneHeader.add(myPlayerCount);

		myPlayersPane.add(myPlayersPaneHeader, BorderLayout.NORTH);

	}

	private void addMyPlayers() {

		int nr = (int) myPlayerCount.getValue();
		this.myPlayersPaneContent.setLayout(new GridLayout(nr, 1));
		this.myPlayers = new ArrayList<>();
		for (int i = 0; i < nr; ++i) {
			PlayerConfigRow row = new PlayerConfigRow(rnd);
			this.myPlayers.add(row);
			this.myPlayersPaneContent.add(row);
		}
		this.myPlayersPaneContent.setBackground(GameController.PLAYGROUND_BACKGROUND);
		this.myPlayersPaneContentScrollPane.getViewport().setBackground(GameController.PLAYGROUND_BACKGROUND);
		this.myPlayersPaneContentScrollPane.setBorder(BorderFactory.createEmptyBorder());
	}

}
