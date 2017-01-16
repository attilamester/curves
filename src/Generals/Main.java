package generals;
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
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import networking.GameClient;
import networking.GameServer;

public class Main {
	
	/***********************************************************************
	 * 
	 * GENERAL CONSTANTS
	 * 
	 ************************************************************************/
	
	public static final int SCREEN_WIDTH  = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	
	public static final int LANDING_WIDTH  = 400;
	public static final int LANDING_HEIGHT = 550;
	
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
	
	public static void addBackPane(JPanel panel, Callable<Void> func) {
		JLabel back = new JLabel(new ImageIcon(Main.class.getResource("/back.png")));		
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
				game.getLandingWindow().setContentPane(game.getLandingWindow().getDefaultContent());
				game.getLandingWindow().revalidate();
				game.getLandingWindow().repaint();
				
				try {
					func.call();
				} catch (Exception ex) {}
			}
		});
		
		JPanel backPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		backPane.setBackground(Colors.BACK_PANE);
		backPane.setSize(panel.getWidth(), 50);
		backPane.add(back);
		
		//panel.remove(((BorderLayout)panel.getLayout()).getLayoutComponent(BorderLayout.NORTH));
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
	
	/***********************************************************************
	 * 
	 * SOUND STUFF
	 * 
	 ************************************************************************/
	
	public static synchronized void playSound(final String url) {
		
		  new Thread(new Runnable() {
		    public void run() {
		      /*
		    	try {/*
		    	  File audioFile = new File("//sounds//a.mp3");
		    	  
		    	  AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
		    	  AudioFormat format = audioStream.getFormat();
		    	  
		    	  DataLine.Info info = new DataLine.Info(Clip.class, format);
		    	  Clip audioClip = (Clip) AudioSystem.getLine(info);
		    	  
		    	  audioClip.open(audioStream);
		    	  audioClip.start();
		    	  
		    	  
		    	  Clip clip = AudioSystem.getClip();
		          AudioInputStream inputStream = AudioSystem.getAudioInputStream(
		          Main.class.getResourceAsStream("/a.mp3"));
		          clip.open(inputStream);
		          clip.start();
		          
		    	  
		    	  URL url = this.getClass().getClassLoader().getResource("/impact.wav");
		          AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
		          // Get a sound clip resource.
		          Clip clip = AudioSystem.getClip();
		          // Open audio clip and load samples from the audio input stream.
		          clip.open(audioIn);
		          clip.start();
		      } catch (UnsupportedAudioFileException e) {
		          e.printStackTrace();
		      } catch (IOException e) {
		         e.printStackTrace();
		      } catch (LineUnavailableException e) {
		         e.printStackTrace();
		      }
		      */
		    }
		  }).start();
		}
	
	private static GameController game = null;
	private static GameServer gameServer = null;
	private static GameClient gameClient = null;
	
	public static void main(String[] args) {
		
		game = new GameController();
		
	}

	public static GameController getGameController() {
		return game;
	}

	public static GameServer getGameServer() {
		return gameServer;
	}

	public static void setGameServer(GameServer gameServer) {
		Main.gameServer = gameServer;
	}

	public static GameClient getGameClient() {
		return gameClient;
	}

	public static void setGameClient(GameClient gameClient) {
		Main.gameClient = gameClient;
	}
	
	
	
	

}
