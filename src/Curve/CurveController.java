package curve;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.Timer;

import generals.GameController;

public class CurveController implements Runnable {
	
	private volatile Thread control;
	private volatile boolean suspended;
	
	
	private Curve curve;
	//private BaseImageLayer baseImg;
	
	private Random rnd;
	private int startWhole;
	private int endWhole;
	private int count;
	private int time;
	
	private int delay;
	private Timer timer;
	private Timer dashStarter;
	private Timer dashStopper;
	
	public CurveController(Curve curve) {
		this.curve = curve;
		
		count = 0;
		rnd = new Random();
		
		this.setDashStart();
		this.setDashStop();
		
		delay = 10;
		timer = new Timer(delay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
												
				if (GameController.finished || control == null) {
					timer.stop();
					return;
				}
				
				if(CurveController.this.curve.isLeftPressed())
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
		
		dashStarter = new Timer(rnd.nextInt(10000) + 1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (curve.isPaused())
					return;
				curve.setPaused(true);
				
				double v = curve.calcSpeed() / delay; 	// <px / ms>
				double d = 6 * curve.getRadius(); 	 	// <px>   (3 * circle diameter)
				double t = d / v; 					  	// <ms>
				
				//System.out.println("\t\tv: " + v + " d: " +  d  + " t: " + t );
				
				int dashStop = rnd.nextInt((int)t) + (int)(t);
				dashStopper.setInitialDelay(dashStop);
				dashStopper.start();
				
				dashStarter.setDelay(dashStop + rnd.nextInt(10000) + 500);
			}
		});
	}
	
	public void start() {
		this.control = new Thread(this);
		this.suspended = false;
		control.start();
	}
	
	public void stop() {
		/*Thread tmp = control;
		tmp.interrupt();
		control = null;*/
		timer.stop();
	}
	
	public void restart() {
		timer.restart();		
	}
	
	public void suspend() {
		this.suspended = true;
	}
	
	public void resume() {
		synchronized(this) {
			this.suspended = false;
			notifyAll();
		}
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
	
	/*public void setBaseImg(BaseImageLayer baseImg) {
		this.baseImg = baseImg;
	}/*/

}
