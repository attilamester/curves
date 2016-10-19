package landing_pages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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

import generals.Colors;
import generals.GameController;
import generals.Main;
import landing_pages.PlayerConfigRow.TextFieldPlaceholder;
import modals.ErrorDialog;
import network_packages.PreGameInfo;
import network_packages.SocketPackage;
import networking.GameClient;
import networking.LanIpTester;

public class JoinGameConfigPanel extends LocalGameConfigPanel {
	private static final long serialVersionUID = 1;

	private JPanel allPlayersPane;
	private JPanel remotePlayersPane;
	private JPanel remotePlayersPaneHeader;
	private JPanel remotePlayersPaneContent;
	private JScrollPane remotePlayersPaneContentScrollPane;

	private JPanel connectPane;
	private JPanel loadingPane;
	
	private List<TextFieldPlaceholder> serverPlayers;
	private List<TextFieldPlaceholder> otherClientsPlayers;
	
	private JSpinner subnetSpinner;

	public JoinGameConfigPanel(int width, int height) {
		super(width, height);
		
		serverPlayers = new ArrayList<>();
		otherClientsPlayers = new ArrayList<>();
		
		
		customizePanel();
		
		addComponentListener();
		
	}
	
	public void searchDoneForHost(List<String> hosts) {
		remotePlayersPaneContent.removeAll();
		remotePlayersPaneContent.revalidate();
		for (String s : hosts) {
			System.out.println(s);
		}
	}

	private void tryConnectToHost() {

		String host = "192.168.0." + Integer.toString((Integer) this.subnetSpinner.getValue());

		LanIpTester ipTester = new LanIpTester(this, host);
		new Thread(ipTester).start();

	}

	public void connectingError(String message) {
		addConnectPane();
		new ErrorDialog(message);
	}

	public void connectingSuccess(Socket createdSocket) {
		remotePlayersPaneContent.removeAll();
		remotePlayersPaneContent.revalidate();
		remotePlayersPaneContent.repaint();
		remotePlayersPaneHeader.setBackground(Color.GREEN);

		try {
			Main.setGameClient(new GameClient(Main.getGameController(), createdSocket));
			Main.getGameClient().respondToServer(new SocketPackage(-1, SocketPackage.PACKAGE_HAND_SHAKE));
			// SocketPackage pack = (SocketPackage)
			// Main.getGameClient().getClientThread().readObject();
			// Main.getGameClient().getClientThread().setClientID(pack.getClientID());
			Main.getGameClient().respondToServer(new PreGameInfo(Main.getGameClient().getClientID(), collectTextFields()));
		} catch (IOException e) {
			System.out.println("NOT Wrote to S");
		} /*
			 * catch (ClassNotFoundException e) { }
			 */

	}

	/***********************************************************************************
	 * GUI
	 ************************************************************************************/
	private void customizePanel() {
		setTitleActions();
		addRemotePlayers();
	}

	private void setTitleActions() {
		JPanel topConfigsPane = this.getTopConfigPane();
		topConfigsPane.remove(this.speedLabel);
		topConfigsPane.remove(this.speedSlider);
		topConfigsPane.remove(this.angleLabel);
		topConfigsPane.remove(this.angleSlider);
	}
	
	private void addRemotePlayers() {
		
		remotePlayersPane = new JPanel(new BorderLayout());
		remotePlayersPaneHeader = new JPanel(new GridLayout(1, 2));
		remotePlayersPaneContent = new JPanel(new BorderLayout());
		remotePlayersPaneContent.setBackground(Colors.ANIMATION_BACKGROUND);

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

		remotePlayersPaneHeader.add(title);
		remotePlayersPaneHeader.add(ipAddressPane);

		remotePlayersPaneContentScrollPane = new JScrollPane(remotePlayersPaneContent);
		remotePlayersPaneContentScrollPane.setBackground(GameController.PLAYGROUND_BACKGROUND);
		remotePlayersPaneContentScrollPane.getViewport().setBackground(GameController.PLAYGROUND_BACKGROUND);
		remotePlayersPaneContentScrollPane.setBorder(BorderFactory.createEmptyBorder());

		remotePlayersPane.add(remotePlayersPaneHeader, BorderLayout.NORTH);
		remotePlayersPane.add(remotePlayersPaneContentScrollPane, BorderLayout.CENTER);

		
		allPlayersPane = new JPanel(new GridLayout(2, 1));
		allPlayersPane.add(remotePlayersPane);
		allPlayersPane.add(this.getLocalPlayersPane());
		
		this.getContentPane().add(allPlayersPane, BorderLayout.CENTER);
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
		remotePlayersPaneContent.add(connectPane);

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
				tryConnectToHost();
			}
		});
	}

	private void addConnectPane() {
		remotePlayersPaneContent.removeAll();
		subnetSpinner.setEnabled(true);
		remotePlayersPaneContent.add(this.connectPane);
		remotePlayersPaneContent.revalidate();
		remotePlayersPaneContent.repaint();
	}

	private void createLoadingPane() {
		loadingPane = new JPanel(new BorderLayout());
		loadingPane.setOpaque(false);

		ImageIcon icon = new ImageIcon("images\\loading.gif");
		JLabel loading = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT)));

		JLabel loadingLabel = new JLabel("Searching for host ...", JLabel.CENTER);
		loadingLabel.setBorder(new EmptyBorder(5, 10, 5, 0));
		loadingLabel.setForeground(Color.WHITE);

		loadingPane.add(loading, BorderLayout.CENTER);
		loadingPane.add(loadingLabel, BorderLayout.NORTH);

	}

	private void addLoadingPane() {
		subnetSpinner.setEnabled(false);
		remotePlayersPaneContent.removeAll();
		remotePlayersPaneContent.add(this.loadingPane);
		remotePlayersPaneContent.revalidate();
		remotePlayersPaneContent.repaint();
	}

	private void addComponentListener() {
		this.localPlayersPaneContent.addContainerListener(new ContainerAdapter() {
			@Override
			public void componentAdded(ContainerEvent e) {
				if (Main.getGameClient() != null)
					Main.getGameClient().respondToServer(new PreGameInfo(0, collectTextFields()));
			}

			@Override
			public void componentRemoved(ContainerEvent e) {
				if (Main.getGameClient() != null)
					Main.getGameClient().respondToServer(new PreGameInfo(0, collectTextFields()));
			}
		});

	}

	public synchronized void arrivedNewPlayerConfigs(int clientID, List<TextFieldPlaceholder> players) {
		
		this.remotePlayersPaneContent.removeAll();
		
		List<TextFieldPlaceholder> allOtherPlayers = new ArrayList<>();
		
		if (clientID == 0) {
			//packet arrived from server => contains server-players
			this.serverPlayers.clear();
			this.serverPlayers.addAll(players);
		} else {
			this.otherClientsPlayers.clear();
			this.otherClientsPlayers.addAll(players);
		}
		allOtherPlayers.addAll(serverPlayers);
		allOtherPlayers.addAll(otherClientsPlayers);
		this.addRemotePlayersToPanel(remotePlayersPaneContent, allOtherPlayers);

		this.remotePlayersPaneContent.revalidate();
		this.remotePlayersPaneContent.repaint();
	}

}
