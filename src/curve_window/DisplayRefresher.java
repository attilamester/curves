package curve_window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import generals.GameController;
import generals.Main;

public class DisplayRefresher extends Thread {
	
	private PlayGround playGround;
	private int delay;
	private Timer timer;
	
	public DisplayRefresher(PlayGround playGround) {
		this.playGround = playGround;
		
		delay = 5;
		
		if (Main.getGameClient() != null) {
			timer = new Timer(delay, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					if (GameController.finished) {
						timer.stop();
						return;
					}
					//DisplayRefresher.this.playGround.sendPlayersToServer();
					DisplayRefresher.this.playGround.repaint();
					
				}
			}); 
		} else {
			if (Main.getGameServer() != null) {
				timer = new Timer(delay, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						
						if (GameController.finished) {
							timer.stop();
							return;
						}
						//DisplayRefresher.this.playGround.sendPlayersToClients();
						DisplayRefresher.this.playGround.repaint();
					}
				}); 
			} else {
				timer = new Timer(delay, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						
						if (GameController.finished) {
							timer.stop();
							return;
						}
						DisplayRefresher.this.playGround.repaint();				
						
					}
				}); 
			}
		}
		
	}	
	
	@Override
	public void run() {
		timer.start();
		/*while (!GameController.finished) {
			/*
			playGround.repaint();
			
			try {
				Thread.sleep(5);
			} catch(InterruptedException e) {}
			
			timer.start();
		}*/
	}
	
	public void stopRefresher() {
		timer.stop();
	}
	
	public void restartRefresher() {
		timer.restart();
	}
}
