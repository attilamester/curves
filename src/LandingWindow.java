import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class LandingWindow extends JFrame{
	
	Container contentPane;
	
	LandingButton newGame;
	LandingButton highScores;
	LandingButton settings;
	
	public LandingWindow() {
		
		contentPane = this.getContentPane();
		contentPane.setLayout(new GridLayout(3, 1));
		contentPane.setBackground(new Color(30, 30, 30));
		
		newGame = new LandingButton("New Game", Color.green);
		highScores = new LandingButton("High scores", new Color(150, 187, 160));
		settings = new LandingButton("Settings", Color.CYAN);
		
		contentPane.add(newGame);
		contentPane.add(highScores);
		contentPane.add(settings);
		
		this.setSize(400, 450);		
		this.setResizable(false);
		this.setBounds(Main.screenSize.width / 2 - this.getWidth() / 2, Main.screenSize.height / 2 - this.getHeight() / 2, this.getWidth(), this.getHeight());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);		
		getRootPane().setBorder(new LineBorder(new Color(30, 30, 30), 5));
		this.setVisible(true);
		Main.setCloseOnEsc(this);
	}
	
	/*************************************************************
	 * 
	 * NESTED CLASSES
	 * 
	 *************************************************************/
	private class LandingButton extends JButton {
		
		public LandingButton(String label, Color bg) {
			super(label);
			setFocusPainted(false);			
			setBackground(bg);
			setFont(new Font("Caibri", Font.PLAIN, 30));
			setForeground(Color.WHITE);
			setMargin(new Insets(20, 50, 200, 20));
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
				public void mousePressed(MouseEvent e) {					
					setBorder(BorderFactory.createMatteBorder(0, 10, 0, 0, Color.red));					
				}
			});
		}
	}
}