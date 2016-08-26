import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

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
	
	public static void main(String[] args) {
		
		GameController game = new GameController();
		
	}

}
