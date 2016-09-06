import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ConfigPanel extends JPanel {
	
	private Random rnd;
	
	private JLabel speedLabel;
	private JLabel angleLabel;
	private JLabel playerNrLabel;
	
	/**
	 * Content - to CENTER of contentPane
	 */
	private JPanel contentPane;
	private JPanel topPane;
	private JPanel playersPane;
	private JPanel buttonsPane;
	
	private JScrollPane scrollPane;
	
	private JSlider speedSlider;
	private JSlider angleSlider;
	
	private JSpinner playerCount;
	private List<PlayerConfigRow> players;
	
	private JLabel start;	
	
	private LandingWindow landingWindow;
	
	
	public ConfigPanel() {
		
		this.rnd = new Random();
		
		this.setLayout(new BorderLayout());		
		this.setSize(Main.LANDING_WIDTH, 400);		
		
		addItems();
				
	}
	
	public void setLandingWindow(LandingWindow landingWindow) {
		this.landingWindow = landingWindow;
	}

	
	public Random getRnd() {
		return rnd;
	}

	private void addItems() {
		
		addSpeedSlider();
		addAngleSlider();
		addNumberSpinner();
		addTopContent();
		addMiddleContent();
		addBottomButtons();
		
		contentPane = new JPanel(new BorderLayout());
		contentPane.add(topPane,    BorderLayout.NORTH);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(buttonsPane,BorderLayout.SOUTH);
		
		Main.addBackPane(this);
		add(contentPane, BorderLayout.CENTER);
	}
	
	private void addSpeedSlider() {
		Color bg = Colors.MAIN_COLORS[0];
		
		this.speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 500, 100);
		speedSlider.setBackground(bg);
		
		speedLabel = new JLabel("Curve speed:");
		speedLabel.setOpaque(true);
		speedLabel.setBorder(new EmptyBorder(5, 10, 5, 0));
		speedLabel.setBackground(bg);
		speedLabel.setForeground(Color.WHITE);
		speedLabel.setLabelFor(speedSlider);
	}
	
	private void addAngleSlider() {
		Color bg = Colors.MAIN_COLORS[1];
		
		this.angleSlider = new JSlider(JSlider.HORIZONTAL, 10, 40, 20);
		angleSlider.setBackground(bg);
		
		angleLabel = new JLabel("Turn angle:");
		angleLabel.setOpaque(true);
		angleLabel.setBorder(new EmptyBorder(5, 10, 5, 0));
		angleLabel.setForeground(Color.WHITE);
		angleLabel.setBackground(bg);
		angleLabel.setLabelFor(angleSlider);
	}
	
	private void addNumberSpinner() {
		Color bg = Colors.MAIN_COLORS[2];
		
		SpinnerModel model = new SpinnerNumberModel(1, 1, 10, 1);
		this.playerCount = new JSpinner(model);
		((JSpinner.DefaultEditor) playerCount.getEditor()).getTextField().setEditable(false);
		((JSpinner.DefaultEditor) playerCount.getEditor()).getTextField().setForeground(Color.WHITE);
		playerCount.setPreferredSize(new Dimension(50, 25));
		playerCount.setBorder(BorderFactory.createEmptyBorder());
		
		
		Component c = playerCount.getEditor().getComponent(0);
		c.setFont(new Font("Calibri", Font.BOLD, 18));
		c.setBackground(bg);
		
		playerCount.addChangeListener(new ChangeListener() {
			@Override
		    public void stateChanged(ChangeEvent e) {			
				int nr = (int)playerCount.getValue();				
				if (nr > players.size()) {
					playersPane.setLayout(new GridLayout(nr, 1));
					PlayerConfigRow row = new PlayerConfigRow(rnd);
					players.add(row);
					playersPane.add(row);					
					playersPane.revalidate();
				} else {
					playersPane.setLayout(new GridLayout(players.size() - 1, 1));
					PlayerConfigRow ref = players.get(players.size() - 1);
					players.remove(ref);					
					playersPane.remove(ref);					
					playersPane.revalidate();
				}
			}
		});
		
		playerNrLabel = new JLabel("Number of players:");
		playerNrLabel.setOpaque(true);
		playerNrLabel.setBorder(new EmptyBorder(5, 10, 5, 0));
		playerNrLabel.setBackground(bg);
		playerNrLabel.setForeground(Color.WHITE);
		playerNrLabel.setLabelFor(playerCount);
		
	}
	
	private void addTopContent() {
		this.topPane = new JPanel();
		topPane.setLayout(new GridLayout(3, 2));
		topPane.add(this.speedLabel);
		topPane.add(this.speedSlider);
		topPane.add(this.angleLabel);
		topPane.add(this.angleSlider);
		topPane.add(this.playerNrLabel);
		topPane.add(this.playerCount);
	}
	
	private void addMiddleContent() {
		this.playersPane = new JPanel();		
		int nr = (int)playerCount.getValue();
		this.playersPane.setLayout(new GridLayout(nr, 1));
		this.players = new ArrayList<>();
		for (int i = 0; i < nr; ++i) {
			PlayerConfigRow row = new PlayerConfigRow(rnd);
			this.players.add(row);
			this.playersPane.add(row);
		}
		this.scrollPane = new JScrollPane(this.playersPane);
		this.playersPane.setBackground(GameController.PLAYGROUND_BACKGROUND);
		this.scrollPane.getViewport().setBackground(GameController.PLAYGROUND_BACKGROUND);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
	}
	
	private void addBottomButtons() {
		this.buttonsPane = new JPanel();
		buttonsPane.setBackground(Colors.BACK_PANE);
		
		this.start = new JLabel(new ImageIcon("images\\startBg.png"));
		start.setBackground(new Color(0,0,0,0));
		start.setOpaque(false);
		
		this.buttonsPane.add(start);
				
		/*************************************************************************
		 * 
		 * START GAME
		 * 
		 *************************************************************************/		
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
				
				List<Control> ctrl = new ArrayList<Control>();
				List<String> names = new ArrayList<String>();
				List<Color> colors = new ArrayList<Color>();
				for (Component c : playersPane.getComponents()) {
					PlayerConfigRow ref = (PlayerConfigRow) c;
					//ctrl.add(new Control(ref.getLeft(), ref.getRight()));
					ctrl.add(new Control(65,83));
					names.add(ref.getName());
					colors.add(ref.getColor());
				}
				
				GameController.DEFAULT_CURVE_ANGLE = angleSlider.getValue() / 10;
				GameController.DEFAULT_CURVE_SPEED = speedSlider.getValue() / 100;
				
				CurveWindow curveWindow = new CurveWindow((int)playerCount.getValue(), ctrl, names, colors);	
				CountDownModal cnt = new CountDownModal(curveWindow, 1, null);				
			}
		});
	    
	}
	
	static class PlayerConfigRow extends JPanel {
		
		static class TextFieldPlaceholder extends JTextField implements FocusListener {
			
			private String placeHolder;
			private Color placeHolderColor;
			private boolean placeHolderShown;
			
			public TextFieldPlaceholder(String placeHolder, Color c) {
				super(placeHolder);
				this.placeHolder = placeHolder;
				this.placeHolderShown = true;
				this.addFocusListener(this);
				this.placeHolderColor = c;				
				
				this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1, true));
				this.setPreferredSize(new Dimension(150, 25));
				this.setOpaque(false);
				this.setForeground(placeHolderColor);				
				this.setFont(new Font("Calibri", Font.ITALIC, 18));
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));				
				if (this.getText().isEmpty()) {
					super.setText("");
					this.placeHolderShown = false;
				}
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
				if (this.getText().isEmpty()) {					
					super.setText(placeHolder);
					this.placeHolderShown = true;
				}
			}
			@Override
			public String getText() {				
				return this.placeHolderShown ? "" : super.getText();
			}
		}
		
		static class DetectControlButton extends JTextField implements FocusListener {

			private String label;
			private boolean listening;
			private boolean hasSpecChar;
			private int code;

			public DetectControlButton(String label, Color col) {
				super(label);
				super.setEditable(false);
				this.setPreferredSize(new Dimension(50, 25));		
				this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));				
				this.setOpaque(false);
				this.setForeground(col);
				
				this.label = label;
				this.listening = false;
				this.hasSpecChar = false;
				this.code = -1;
								
				addFocusListener(this);
				addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {
						if (listening) {
							setText("");
							code = e.getKeyCode();
							repaint();
						}
					}
				});
			}

			@Override
			public void focusGained(FocusEvent e) {
				this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
				listening = true;
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (code == -1)
					this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
				else
					this.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,0)));				
				listening = false;
			}

			public int getCode() {
				return this.code;
			}
			
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				BufferedImage img = null;
				if (code == -1) {					
					return;
				}
				String baseDir = "images\\keys\\";
				String name = null;
				try {
					name = "key_" + code + ".png";
					img = ImageIO.read(new File(baseDir + name));
					
					g.drawImage(img, 0, 0, 50, 25,  null);					
				} catch (IOException ex) {
					try {
						img = ImageIO.read(new File(baseDir + "key_notfound.png"));
						g.drawImage(img, 0, 0, 50, 25,  null);
					} catch (IOException e) {}
				}

			}
		}
		
		public TextFieldPlaceholder name;
		public Color color;
		public DetectControlButton leftCtrl;
		public DetectControlButton rightCtrl;
		
		public PlayerConfigRow(Random rnd) {
			setLayout(new FlowLayout());
			
			color = new Color(rnd.nextInt(200) + 50, rnd.nextInt(200) + 50, rnd.nextInt(200) + 50);
			
			name = new TextFieldPlaceholder("Player's name", color);			
			leftCtrl = new DetectControlButton(" LEFT", color);
			rightCtrl = new DetectControlButton(" RIGHT", color);
			
			setBackground(GameController.PLAYGROUND_BACKGROUND);
			
			add(name);
			add(leftCtrl);
			add(rightCtrl);
		}
		
		public String getName() {
			return name.getText();
		}
		
		public int getLeft() {
			return leftCtrl.getCode();
		}
		
		public int getRight() {
			return rightCtrl.getCode();
		}
		
		public Color getColor() {
			return color;
		}
	}
}