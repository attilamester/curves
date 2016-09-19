import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

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
				back.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e) {				
				back.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			@Override
			public void mouseClicked(MouseEvent e) {				
				game.landingWindow.setContentPane(game.landingWindow.getDefaultContent());
				game.landingWindow.revalidate();
				game.landingWindow.repaint();
			}
		});
		
		JPanel backPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		backPane.setBackground(Colors.BACK_PANE);
		backPane.setSize(panel.getWidth(), 50);
		backPane.add(back);
		
		panel.add(backPane, BorderLayout.NORTH);
	}
	
	/***********************************************************************
	 * 
	 * Coloring stuff
	 * 
	 ************************************************************************/
	
	public static BufferedImage toBufferedImage(Image image) { 
		if (image instanceof BufferedImage) return (BufferedImage) image;

		image = new ImageIcon(image).getImage(); 
		BufferedImage bimage = new BufferedImage(image.getWidth(null),image.getHeight(null), BufferedImage.TYPE_INT_ARGB); 
		Graphics g = bimage.createGraphics(); 
		g.drawImage(image,0,0,null); 
		g.dispose(); 
		return bimage; 
	}
	
	public static String getCssColor(int pixel) {
		return "rgb (" + Integer.toString(Main.getRed_fromInt(pixel)) + ", " + Integer.toString(Main.getGreen_fromInt(pixel)) + ", " + Integer.toString(Main.getBlue_fromInt(pixel)) + ")"; 
	}
	
	public static int getRed_fromInt(int pixel) {
		return (pixel & 0x00ff0000) >> 16;
	}
	
	public static int getGreen_fromInt(int pixel) {
		 return (pixel & 0x0000ff00) >> 8;		 
	}
	
	public static int getBlue_fromInt(int pixel) {
		return (pixel & 0x000000ff);
	}
	
	private static GameController game;
	
	public static void main(String[] args) {
		
		game = new GameController();
	}

}
