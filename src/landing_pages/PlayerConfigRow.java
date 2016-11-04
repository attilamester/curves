package landing_pages;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
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
import java.io.Serializable;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import generals.Colors;
import generals.GameController;
import generals.Main;


public class PlayerConfigRow extends JPanel implements Serializable {

	private static final long serialVersionUID = 1;
	
	public class TextFieldPlaceholder extends JTextField implements FocusListener, Serializable {
		private static final long serialVersionUID = 1;
		
		private String value;
		private String placeHolder;
		private Color color;
		private boolean placeHolderShown;
		
		public TextFieldPlaceholder(String placeHolder, Color c) {
			super(placeHolder);
			this.placeHolder = placeHolder;
			this.placeHolderShown = true;
			this.addFocusListener(this);
			this.color = c;				
			this.value = new String("");
			
			this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1, true));
			this.setPreferredSize(new Dimension(150, 25));
			this.setOpaque(false);
			this.setForeground(color);
			this.setDisabledTextColor(color);
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
			if (this.getText() != this.value) {
				if (Main.getGameController().getLandingWindow().getLanGameConfigPanel() != null) {
					Main.getGameController().getLandingWindow().getLanGameConfigPanel().triggerTextFieldChange();
				} else if (Main.getGameController().getLandingWindow().getJoinGameConfigPanel() != null) {
					Main.getGameController().getLandingWindow().getJoinGameConfigPanel().triggerTextFieldChange();
				}
				this.value = this.getText();
			}
			
		}
		@Override
		public String getText() {				
			return this.placeHolderShown ? "" : super.getText();
		}
		
		public void setColor(Color color) {
			this.color = color;
			this.setForeground(color);
			this.setDisabledTextColor(color);
		}

		public Color getColor() {
			return color;
		}
	}
	
	private class DetectControlButton extends JTextField implements FocusListener {
		private static final long serialVersionUID = 1;

		private boolean listening;
		//private boolean hasSpecChar;
		private int code;
		//private Color color;

		public DetectControlButton(String label, Color col) {
			super(label);
			super.setEditable(false);
			this.setPreferredSize(new Dimension(50, 25));
			this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));				
			this.setOpaque(false);
			this.setForeground(col);
			
			this.listening = false;
			//this.hasSpecChar = false;
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
			String baseDir = "/keys/";
			String name = null;
			try {
				name = "key_" + code + ".png";
				img = ImageIO.read(this.getClass().getResource(baseDir + name));
				
				g.drawImage(img, 0, 0, 50, 25,  null);					
			} catch (IOException ex) {
				try {
					img = ImageIO.read(this.getClass().getResource(baseDir + "key_notfound.png"));
					g.drawImage(img, 0, 0, 50, 25,  null);
				} catch (IOException e) {}
			}
		}
		
		public void setColor(Color color) {
			//this.color = color;
			this.setForeground(color);
		}
	}
	
	private class CurveColorChooser extends JComboBox<Color> {
		private static final long serialVersionUID = 1;
		
		private Color color;
			
		public CurveColorChooser(PlayerConfigRow row) {
			super(Colors.DEFAULT_CURVE_COLORS);
			
			this.setPreferredSize(new Dimension(50, 25));
			this.setSelectedIndex(-1);
			/***************************************************************
			 * COMBO BOX BORDER BUG WORKAROUND
			 * http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4515838
			 ***************************************************************/
			for (int i = 0; i < this.getComponentCount(); i++) 
			{
			    if (this.getComponent(i) instanceof JComponent) {
			        ((JComponent) this.getComponent(i)).setBorder(new EmptyBorder(0, 0,0,0));
			    }


			    if (this.getComponent(i) instanceof AbstractButton) {
			        ((AbstractButton) this.getComponent(i)).setBorderPainted(false);
			    }
			}
			/**************************************************************/
			//this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
			this.setRenderer(new ComboBoxRenderer());
			
			this.addActionListener(new ActionListener() {
				@Override
				 public void actionPerformed(ActionEvent e) {
					Color selectedColor = (Color) getSelectedItem();
			        row.setColor(selectedColor);
			        
			        if (Main.getGameController().getLandingWindow().getLanGameConfigPanel() != null) {
						Main.getGameController().getLandingWindow().getLanGameConfigPanel().triggerTextFieldChange();
					} else if (Main.getGameController().getLandingWindow().getJoinGameConfigPanel() != null) {
						Main.getGameController().getLandingWindow().getJoinGameConfigPanel().triggerTextFieldChange();
					}
				}
			});
			this.getEditor().getEditorComponent().addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
				//	setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
				//	System.out.println("asd");
				}
			});
			
		}
		public Color getColor() {
			return color;
		}
				
		private class ComboBoxRenderer extends JPanel implements ListCellRenderer {
			private static final long serialVersionUID = 1;
			
			private Color optionColor;
			
			public ComboBoxRenderer() {
				super();
				this.setPreferredSize(new Dimension(50, 20));
				this.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
			}
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				if (value instanceof Color) {
					optionColor  = (Color) value;
				}
	            return this;
			}
			@Override
			public void paint(Graphics g) {
				setBackground(optionColor);
				super.paintComponent(g);					
			}
			
		}
	}
	
	private TextFieldPlaceholder nameTextBox;
	private transient DetectControlButton leftCtrl;
	private transient DetectControlButton rightCtrl;
	private Color color;
	private transient CurveColorChooser colorChooser;
	
	public PlayerConfigRow(Random rnd) {
		setLayout(new FlowLayout());
		
		color = new Color(rnd.nextInt(200) + 50, rnd.nextInt(200) + 50, rnd.nextInt(200) + 50);
		
		nameTextBox = new TextFieldPlaceholder("Player's name", color);			
		leftCtrl = new DetectControlButton(" LEFT", color);
		rightCtrl = new DetectControlButton(" RIGHT", color);
		
		colorChooser = new CurveColorChooser(this);
		
		setBackground(GameController.PLAYGROUND_BACKGROUND);
		
		add(nameTextBox);
		add(leftCtrl);
		add(rightCtrl);
		add(colorChooser);
	}
	
	public String getName() {
		return nameTextBox.getText();
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
	
	public void setColor(Color color) {
		this.color = color;
		this.nameTextBox.setColor(color);
		this.leftCtrl.setColor(color);
		this.rightCtrl.setColor(color);
	}
	
	public TextFieldPlaceholder getTextFieldPlaceholder() {
		return this.nameTextBox;
	}
	
	public void setEnabled(boolean state) {
		this.nameTextBox.setEnabled(state);
		this.leftCtrl.setEnabled(state);
		this.rightCtrl.setEnabled(state);
		this.colorChooser.setEnabled(state);
	}	
}
