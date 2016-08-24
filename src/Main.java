import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

public class Main {
	
	public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
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
