import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class DisplayRefresher extends Thread {
	
	private PlayGround playGround;
	private int delay;
	private Timer timer;
	
	public DisplayRefresher(PlayGround playGround) {
		this.playGround = playGround;
		
		delay = 5;
		timer = new Timer(delay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (GameController.finished) {
					timer.stop();
					return;
				}
				
				playGround.repaint();				
				
			}
		});
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
	
	public void restart() {
		timer.restart();
	}
}
