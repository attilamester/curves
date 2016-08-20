import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ConfigPanel extends JFrame {

	private CurveWindow curveWindow;
	
	private Container contentPane;
	
	
	private JLabel speedLabel;
	private JLabel angleLabel;
	private JLabel playerNrLabel;
	
	private JPanel topPane;
	private JPanel playersPane;
	private JPanel buttonsPane;
	
	private JScrollPane scrollPane;
	
	private JSlider speedSlider;
	private JSlider angleSlider;
	
	private JSpinner playerCount;
	private List<PlayerConfigRow> players;
	
	private JButton start;
	private JButton cancel;
	
	public ConfigPanel(CurveWindow curveWindow) {
		super("New Game");
		this.curveWindow = curveWindow;
		
		contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		this.setSize(300, 350);
		this.setBounds(Main.screenSize.width / 2 - this.getWidth() / 2, Main.screenSize.height / 2 - this.getHeight() / 2, this.getWidth(), this.getHeight());
		this.setUndecorated(true);
		this.setAlwaysOnTop(true);
		//getRootPane().setBorder(new EmptyBorder(10, 10, 10, 10)));
		
		addItems();
		
		this.setVisible(true);
	}
	
	private void addItems() {
		
		addSpeedSlider();
		addAngleSlider();
		addNumberSpinner();
		addTopContent();
		addMiddleContent();
		addBottomButtons();
		
		contentPane.add(topPane,    BorderLayout.NORTH);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(buttonsPane,BorderLayout.SOUTH);
	}
	
	private void addSpeedSlider() {
		Color bg = new Color(160, 30, 120);
		
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
		Color bg = new Color(20, 150, 120);
		
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
		Color bg = new Color(120, 30, 160);
		
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
					PlayerConfigRow row = new PlayerConfigRow();
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
		this.playersPane.setBackground(GameController.PLAYGROUND_BACKGROUND);
		int nr = (int)playerCount.getValue();
		this.playersPane.setLayout(new GridLayout(nr, 1));
		this.players = new ArrayList<>();
		for (int i = 0; i < nr; ++i) {
			PlayerConfigRow row = new PlayerConfigRow();
			this.players.add(row);
			this.playersPane.add(row);
		}
		this.scrollPane = new JScrollPane(this.playersPane);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
	}
	
	private void addBottomButtons() {
		this.buttonsPane = new JPanel();
		buttonsPane.setBackground(new Color(120, 190, 210));
		
		this.start = new JButton("START");
		start.setBackground(Color.WHITE);
		start.setForeground(Color.GREEN);
		
		this.cancel = new JButton("Cancel");
		cancel.setBackground(Color.WHITE);
		cancel.setForeground(Color.RED);
		
		this.buttonsPane.add(start);
		this.buttonsPane.add(cancel);
		
		this.cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		/*************************************************************************
		 * START GAME
		 * 
		 */
		this.start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<Control> ctrl = new ArrayList<Control>();
				List<String> names = new ArrayList<String>();
				for (Component c : playersPane.getComponents()) {
					PlayerConfigRow ref = (PlayerConfigRow) c;
//					ctrl.add(new Control(ref.getLeft(), ref.getRight()));
					ctrl.add(new Control(65,83));
					names.add(ref.getName());
				}
				setVisible(false);
				dispose();
				
				GameController.DEFAULT_CURVE_ANGLE = angleSlider.getValue() / 10;
				GameController.DEFAULT_CURVE_SPEED = speedSlider.getValue() / 100;
				
				ConfigPanel.this.curveWindow.createPlayGround((int)playerCount.getValue(), ctrl, names);		
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
				
				this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
				this.setPreferredSize(new Dimension(150, 25));
				this.setBackground(Color.WHITE);
				this.setForeground(placeHolderColor);				
				this.setFont(new Font("Calibri", Font.ITALIC, 18));
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
				this.setForeground(Color.BLACK);
				if (this.getText().isEmpty()) {
					super.setText("");
					this.placeHolderShown = false;
				}
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
				if (this.getText().isEmpty()) {
					this.setForeground(Color.GRAY);
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

			public DetectControlButton(String label) {
				super(label);
				super.setEditable(false);
				this.setPreferredSize(new Dimension(50, 25));		
				this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
				this.setFont(new Font("Calibri", Font.BOLD, 15));
				
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
				this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
				listening = true;
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (code == -1)
					this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
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
		public DetectControlButton leftCtrl;
		public DetectControlButton rightCtrl;
		
		public PlayerConfigRow() {
			setLayout(new FlowLayout());
			
			name = new TextFieldPlaceholder("Player's name", Color.GRAY);			
			leftCtrl = new DetectControlButton("LEFT");
			rightCtrl = new DetectControlButton("RIGHT");
			
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
	}
}