import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

public class LandingWindow extends JFrame {
	
	private Container contentPane;
	private Container defaultContent;
	
	private LandingButton newGame;
	private LandingButton highScores;
	private LandingButton settings;
	
	/*************************************************************
	 * 
	 * OUTER REFERENCES
	 * 
	 *************************************************************/
	private ConfigPanel configPanel;
	
	
	public LandingWindow(ConfigPanel configPanel) {
		
		this.configPanel = configPanel;
		
		contentPane = this.getContentPane();
		contentPane.setLayout(new GridLayout(3, 1));
		contentPane.setBackground(new Color(30, 30, 30));
		
		newGame = new LandingButton("New Game", Color.green);
		highScores = new LandingButton("High scores", new Color(150, 187, 160));
		settings = new LandingButton("Settings", Color.CYAN);
		
		contentPane.add(newGame);
		contentPane.add(highScores);
		contentPane.add(settings);
		
		addActions();
		
		defaultContent = contentPane;
		
		this.setSize(Main.LANDING_WIDTH, Main.LANDING_HEIGHT);		
		this.setResizable(false);
		this.setBounds(Main.SCREEN_WIDTH / 2 - this.getWidth() / 2, Main.SCREEN_HEIGHT / 2 - this.getHeight() / 2, this.getWidth(), this.getHeight());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);		
		//getRootPane().setBorder(new LineBorder(new Color(30, 30, 30), 5));
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
		newGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {				
				LandingWindow.this.setContentPane(configPanel);							 
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