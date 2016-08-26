import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Main {
	
	/***********************************************************************
	 * 
	 * GENERAL CONSTANTS
	 * 
	 ************************************************************************/
	
	public static final int SCREEN_WIDTH  = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	
	public static final int LANDING_WIDTH  = 400;
	public static final int LANDING_HEIGHT = 450;
	
	public static void setCloseOnEsc(JFrame c) {		
		c.addKeyListener(new KeyAdapter() {
	    	@Override
	    	public void keyPressed(KeyEvent e) {	    		
	    		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	    			System.exit(0);
	    		}
	    	}
		});		
	}
	
	public static void addBackPane(JPanel panel) {		
		JLabel back = new JLabel(new ImageIcon("images\\back.png"));		
		back.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				
			}
			@Override
			public void mouseExited(MouseEvent e) {				
				
			}
			@Override
			public void mouseClicked(MouseEvent e) {				
				game.landingWindow.setContentPane(game.landingWindow.getDefaultContent());
				game.landingWindow.revalidate();
				game.landingWindow.repaint();
			}
		});
		
		JPanel backPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		backPane.setBackground(Color.CYAN);
		backPane.setSize(panel.getWidth(), 50);
		backPane.add(back);
		
		panel.add(backPane, BorderLayout.NORTH);
	}
	
	private static GameController game;
	
	public static void main(String[] args) {
		
		game = new GameController();
		
	}

}
