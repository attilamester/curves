import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.Timer;

public class CurveController extends Thread{
	
	private Curve curve;
	private Random rnd;
	private int startWhole;
	private int endWhole;
	private int count;
	private int time;
	private Color original;
	
	private int delay;
	private Timer timer;
	private Timer dashStarter;
	private Timer dashStopper;
	
	public CurveController(Curve curve) {
		this.curve = curve;
		original = curve.getColor();
		count = 0;
		rnd = new Random();
		
		this.setDashStart();
		this.setDashStop();
		
		delay = 5;
		timer = new Timer(delay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (GameController.finished) {
					timer.stop();
					return;
				}
				
				if(curve.isLeftPressed())
					curve.turnLeft();
				else if(curve.isRightPressed())
					curve.turnRight();
				
				curve.setOldX(curve.getX());
				curve.setOldY(curve.getY());
				curve.setX(curve.getX() + curve.getDirection().getI());
				curve.setY(curve.getY() + curve.getDirection().getJ());
			}
		});
		
		dashStopper = new Timer(0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curve.setPaused(false);
			}
		});
		dashStopper.setRepeats(false);
		
		dashStarter = new Timer(rnd.nextInt(10000)+3000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curve.setPaused(true);
				curve.setPausedX(curve.getX());
				curve.setPausedY(curve.getY());
				
				double v = curve.calcSpeed() / delay; // speed in: <px / ms>
				double d = 6 * curve.getRadius(); // 3 * circle diameter
				double t = d / v;
				
				System.out.println("\t\tv: " + v + " d: " +  d  + " t: " + t );
				
				int dashStop = rnd.nextInt((int)t) + (int)(t);
				dashStopper.setInitialDelay(dashStop);							
				dashStopper.start();
				
				dashStarter.setDelay(dashStop + rnd.nextInt(1000) + 500);
			}
		});
	}
		
	@Override
	public void run() {
		
		timer.start();
		dashStarter.start();
		/*while(!GameController.finished ) {
			
			/*
			if(curve.isLeftPressed())
				curve.turnLeft();
			else if(curve.isRightPressed())
				curve.turnRight();
			
			curve.setX(curve.getX() + curve.getDirection().getI());
			curve.setY(curve.getY() + curve.getDirection().getJ());
			
			if (count == startWhole){
				curve.setColor(GameController.PLAYGROUND_BACKGROUND);
				startWhole = rnd.nextInt(500) + 200;
			} else if (count == endWhole){
				curve.setColor(original);
				endWhole = rnd.nextInt(10) + startWhole + 10;
				count = 0;
			}
			try {
				Thread.sleep(10);
			} catch(InterruptedException e) {}
			
			++count;
			
			
		}*/
	}
	
	private void setDashStart() {
		startWhole = rnd.nextInt(400) + 100;
	}
	
	private void setDashStop() {
		endWhole = rnd.nextInt(10) + startWhole + 10;
	}
	
	
}
