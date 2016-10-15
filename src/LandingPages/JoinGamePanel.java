package LandingPages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Generals.Colors;
import Generals.GameController;
import Generals.Main;
import LandingPages.ConfigPanel.PlayerConfigRow;
import Networking.ClientThread;

public class JoinGamePanel extends JPanel {
	private static final long serialVersionUID = 1;

	private Random rnd;

	private JPanel allPlayersPane;

	private JPanel otherPlayersPane;
	private JScrollPane otherScrollPane;

	private JPanel myPlayersPane;
	private JPanel myPlayersPaneHeader;
	private JPanel myPlayersPaneContent;
	private JScrollPane myPlayersPaneContentScrollPane;

	private JSpinner myPlayerCount;
	private List<PlayerConfigRow> myPlayers;

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

	//	checkHosts("192.168.0");
	}

	public void checkHosts(String subnet) throws Exception {
		int timeout = 1000;
		for (int i = 1; i < 255; i++) {
			String host = subnet + "." + i;
			if (InetAddress.getByName(host).isReachable(timeout)) {
				System.out.println(host + " is reachable");
			}
		}
	}

	private void prepareGui() {
		allPlayersPane = new JPanel(new GridLayout(2, 1));

		addRemotePlayers();
		addLocalPlayers();

		this.add(allPlayersPane);
	}

	private void addRemotePlayers() {
		otherPlayersPane = new JPanel(new BorderLayout());

		JLabel title = new JLabel("Remote room:");
		title.setOpaque(true);
		title.setBorder(new EmptyBorder(5, 10, 5, 0));
		title.setBackground(Colors.MAIN_COLORS[1]);
		title.setForeground(Color.WHITE);

		otherPlayersPane.add(title, BorderLayout.NORTH);

		otherScrollPane = new JScrollPane(otherPlayersPane);
		otherPlayersPane.setBackground(GameController.PLAYGROUND_BACKGROUND);
		this.otherScrollPane.getViewport().setBackground(GameController.PLAYGROUND_BACKGROUND);
		this.otherScrollPane.setBorder(BorderFactory.createEmptyBorder());

		allPlayersPane.add(otherScrollPane);
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
