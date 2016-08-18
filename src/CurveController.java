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
				
				/*
				if (count == startWhole){
					
					int delay = rnd.nextInt(3000) + 1000;
					timer.setInitialDelay(delay);
					timer.restart();
					System.out.println(delay);
					//curve.setColor(GameController.PLAYGROUND_BACKGROUND);
					//setDashStart();
					
				} else if (count == endWhole){
					curve.setColor(original);
					setDashStop();					
					count = 0;
				}
				
				++count;
				*/
			}
		});
		
		dashStopper = new Timer(0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curve.setPaused(false);
			}
		});
		dashStopper.setRepeats(false);
		
		dashStarter = new Timer(rnd.nextInt(10000) + 3000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curve.setPaused(true);
				curve.setPausedX(curve.getX());
				curve.setPausedY(curve.getY());
				
				int dashStop = rnd.nextInt(500) + 50;
				dashStopper.setInitialDelay(dashStop);							
				dashStopper.start();
				
				dashStarter.setDelay(dashStop + rnd.nextInt(10000) + 500);
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
