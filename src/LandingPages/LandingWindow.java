package LandingPages;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import Generals.Colors;
import Generals.Main;

public class LandingWindow extends JFrame {
	
	private static final long serialVersionUID = 1;
	
	private Container contentPane;
	private Container defaultContent;
	
	private LandingButton newLocalGame;
	private LandingButton newLanGame;
	private LandingButton joinLanGame;
	private LandingButton highScores;
	private LandingButton settings;
	
	/*************************************************************
	 * 
	 * OUTER REFERENCES
	 * 
	 *************************************************************/
	private ConfigPanel configPanel;
	private LanGamePanel lanGamePanel;
	private JoinGamePanel joinGamePanel;
	private HighScoresPanel highScoresPanel;
	
	
	public LandingWindow() {
		super("Get the hang of it!");
		
		contentPane = this.getContentPane();
		contentPane.setLayout(new GridLayout(5, 1));
		contentPane.setBackground(new Color(30, 30, 30));
		
		newLocalGame = new LandingButton("New Game", Colors.MAIN_COLORS[0]);
		newLanGame   = new LandingButton("New LAN Game", Colors.MAIN_COLORS[1]);
		joinLanGame  = new LandingButton("Join Game", Colors.MAIN_COLORS[2]);
		highScores   = new LandingButton("High scores", Colors.MAIN_COLORS[3]);
		settings     = new LandingButton("Settings", Colors.MAIN_COLORS[4]);
		
		contentPane.add(newLocalGame);
		contentPane.add(newLanGame);
		contentPane.add(joinLanGame);
		contentPane.add(highScores);
		contentPane.add(settings);
		
		addActions();
		
		defaultContent = contentPane;
		
		this.setSize(Main.LANDING_WIDTH, Main.LANDING_HEIGHT);
		this.setResizable(false);
		this.setBounds(Main.SCREEN_WIDTH / 2 - this.getWidth() / 2, Main.SCREEN_HEIGHT / 2 - this.getHeight() / 2, this.getWidth(), this.getHeight());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setIconImage(new ImageIcon("images\\icon.png").getImage());
		this.setVisible(true);
		Main.setCloseOnEsc(this);
	}
	
	public Container getDefaultContent() {
		return this.defaultContent;
	}
	/*************************************************************
	 * 
	 * MAIN MENU ACTIONS
	 * 
	 *************************************************************/
	private void addActions() {
		this.newLocalGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (configPanel == null) {
					configPanel = new ConfigPanel(contentPane.getWidth(), contentPane.getHeight());
				}
				LandingWindow.this.setContentPane(configPanel);
				contentPane.revalidate();
				contentPane.repaint();
			}
		});

		this.newLanGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (lanGamePanel == null) {
					lanGamePanel = new LanGamePanel(contentPane.getWidth(), contentPane.getHeight());
				}
				LandingWindow.this.setContentPane(lanGamePanel);
				contentPane.revalidate();
				contentPane.repaint();
			}
		});
		
		this.joinLanGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (joinGamePanel == null) {
					joinGamePanel = new JoinGamePanel(contentPane.getWidth(), contentPane.getHeight());
				}
				LandingWindow.this.setContentPane(joinGamePanel);
				contentPane.revalidate();
				contentPane.repaint();
			}
		});
		
		this.highScores.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				highScoresPanel = new HighScoresPanel(contentPane.getWidth(), contentPane.getHeight());
				
				LandingWindow.this.setContentPane(highScoresPanel);
				contentPane.revalidate();
				contentPane.repaint();
			}
		});
	}
	
	
	/*************************************************************
	 * 
	 * NESTED CLASSES
	 * 
	 *************************************************************/
	private class LandingButton extends JLabel {
		private static final long serialVersionUID = 1;		
		
		public LandingButton(String label, Color bg) {
			super(label, JLabel.CENTER);
			setOpaque(true);
			setBackground(bg);
			setFont(new Font("Caibri", Font.PLAIN, 30));
			setForeground(Color.WHITE);
			//setMargin(new Insets(20, 50, 200, 20));
			setBorder(new LineBorder(new Color(30, 30, 30), 10));
			
			
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {										
					setBackground(getBackground().darker());
					setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
				@Override
				public void mouseExited(MouseEvent e) {					
					setBackground(getBackground().brighter());
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
				@Override
				public void mouseClicked(MouseEvent e) {
					setBackground(getBackground().brighter());
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			});
		}
	}
}