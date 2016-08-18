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
import java.util.ListIterator;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ConfigPanel extends JFrame {

	private CurveWindow curveWindow;
	
	private Container contentPane;
	
	private JPanel topPane;
	private JPanel middlePane;
	private JPanel bottomPane;
	
	private JScrollPane scrollPane;
	
	private JSpinner playerCount;
	private List<PlayerConfigRow> players;
	
	private JButton start;
	private JButton cancel;
	
	public ConfigPanel(CurveWindow curveWindow) {
		super("New Game");
		this.curveWindow = curveWindow;
		
		contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		this.setSize(300, 250);
		this.setBounds(Main.screenSize.width / 2 - this.getWidth() / 2, Main.screenSize.height / 2 - this.getHeight() / 2, this.getWidth(), this.getHeight());
		this.setUndecorated(true);
		this.setAlwaysOnTop(true);
		getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.CYAN));
		
		addItems();		
		
		this.setVisible(true);
	}
	
	private void addItems() {
		
		addNumberSpinner();
		addMiddleContent();
		addBottomButtons();
				
		add(topPane, BorderLayout.NORTH);	
		add(scrollPane, BorderLayout.CENTER);
		add(bottomPane, BorderLayout.SOUTH);
	}
	
	private void addNumberSpinner() {
		SpinnerModel model = new SpinnerNumberModel(1, 1, 10, 1);
		this.playerCount = new JSpinner(model);
		((JSpinner.DefaultEditor) playerCount.getEditor()).getTextField().setEditable(false);
		playerCount.setPreferredSize(new Dimension(50, 25));
		playerCount.setBorder(BorderFactory.createEmptyBorder());
		
		Component c = playerCount.getEditor().getComponent(0);
		c.setFont(new Font("Calibri", Font.BOLD, 18));
		c.setBackground(Color.CYAN);
		
		playerCount.addChangeListener(new ChangeListener() {
			@Override
		    public void stateChanged(ChangeEvent e) {			
				int nr = (int)playerCount.getValue();				
				if (nr > players.size()) {
					middlePane.setLayout(new GridLayout(nr, 1));
					PlayerConfigRow row = new PlayerConfigRow();
					players.add(row);
					middlePane.add(row);					
					middlePane.revalidate();
				} else {
					middlePane.setLayout(new GridLayout(players.size() - 1, 1));
					PlayerConfigRow ref = players.get(players.size() - 1);
					players.remove(ref);					
					middlePane.remove(ref);					
					middlePane.revalidate();
				}
			}
		});
		
		JLabel nrLabel = new JLabel("<html><span style='font-weight:800;'>Number of players:</span></html>");
		nrLabel.setLabelFor(playerCount);
		
		this.topPane = new JPanel();
		this.topPane.setBackground(Color.CYAN);
		this.topPane.add(nrLabel);
		this.topPane.add(playerCount);
		
	}
	
	private void addMiddleContent() {
		this.middlePane = new JPanel();
		this.middlePane.setBackground(GameController.PLAYGROUND_BACKGROUND);
		int nr = (int)playerCount.getValue();
		this.middlePane.setLayout(new GridLayout(nr, 1));
		this.players = new ArrayList<>();
		for (int i = 0; i < nr; ++i) {
			PlayerConfigRow row = new PlayerConfigRow();
			this.players.add(row);
			this.middlePane.add(row);
		}
		this.scrollPane = new JScrollPane(this.middlePane);
	}
	
	private void addBottomButtons() {
		this.bottomPane = new JPanel();
		this.start = new JButton("Start");
		this.cancel = new JButton("Cancel");
		this.bottomPane.add(start);
		this.bottomPane.add(cancel);
		
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
				for (Component c : middlePane.getComponents()) {
					PlayerConfigRow ref = (PlayerConfigRow) c;
//					ctrl.add(new Control(ref.getLeft(), ref.getRight()));
					ctrl.add(new Control(65,83));
				}
				setVisible(false);
				dispose();
				
				ConfigPanel.this.curveWindow.createPlayGround((int)playerCount.getValue(), ctrl);		
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
				
				this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
				this.setPreferredSize(new Dimension(150, 25));
				this.setBackground(Color.WHITE);
				this.setForeground(placeHolderColor);				
				this.setFont(new Font("Calibri", Font.ITALIC, 18));
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
				if (this.getText().isEmpty()) {
					super.setText("");
					this.placeHolderShown = false;
				}
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
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

			public DetectControlButton(String label) {
				super(label);
				super.setEditable(false);
				this.setPreferredSize(new Dimension(50, 25));		
				this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
				this.setFont(new Font("Calibri", Font.BOLD, 15));
				
				this.label = label;
				this.listening = false;
				this.hasSpecChar = false;
								
				addFocusListener(this);
				addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {
						if (listening) {
							code = e.getKeyCode();							
							setText(Character.toString((char)code));
						}
					}
				});
			}

			@Override
			public void focusGained(FocusEvent e) {
				this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
				listening = true;
			}

			@Override
			public void focusLost(FocusEvent e) {
				this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
				System.out.println("lost");
				listening = false;
			}

			public int getCode() {
				return this.code;
			}
			
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				BufferedImage img = null;
				try {
					String name = "images\\key_notfound.png";
					if (code >= (int)'A' && code <= (int)'Z') {
						name = "images\\key_" + (char)code + ".png";
					} else if (code >= 96 && code <= 105) {
						name = "images\\key_" + (code - 96) + ".png";
					} else if (code >= 48 && code <= 57) {
						name = "images\\key_" + (code - 48) + ".png";
					} else {
						switch (code) {
							case 13: name = "images\\key_enter.png"; break;
							case 16: name = "images\\key_shift.png"; break;
							case 17: name = "images\\key_ctrl.png"; break;
							case 18: name = "images\\key_alt.png"; break;
							case 32: name = "images\\key_space.png"; break;
							
							case 37: name = "images\\key_left.png"; break;
							case 38: name = "images\\key_up.png"; break;
							case 39: name = "images\\key_right.png"; break;
							case 40: name = "images\\key_down.png"; break;
							
							case 111: name = "images\\key_divide.png"; break;
							case 106: name = "images\\key_multiply.png"; break;
							case 107: name = "images\\key_plus.png"; break;
							case 109: name = "images\\key_minus.png"; break;
							
							default: name = "images\\key_notfound.png"; break;
						}
					}
					
					System.out.println(name);
					img = ImageIO.read(new File(name));
					
				} catch (IOException ex) {}
				g.drawImage(img, 0, 0, 50, 25,  null);			

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