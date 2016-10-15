package LandingPages;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import Generals.Colors;
import Networking.GameServer;

public class LanGamePanel extends ConfigPanel {

	public LanGamePanel(int width, int height) {
		super(width, height);

		customizePanel();
		
		GameServer gameServer = null;
		try {
			gameServer = new GameServer(GameServer.DEFAULT_SERVER_PORT);
			gameServer.startServer();
		} catch(Exception e) {
			
		}

	}
	
	
	private void customizePanel() {
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
}
