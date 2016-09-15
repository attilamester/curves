import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Timer;

public class PowerUpLoader {
	
	private ImageLayer backgroundLayer;
	private Timer timer;
	private int delay;
	private int frequency; /// max milli sec. between powerups
	private int minDelay;
	private Random rnd;
	private List<PowerUp> powerUps;
	
	private static final String[] POWERUP_NAMES = {
		"more_extra.png",
		"no_border.png",
		"erase.png",
		"own_fly.png",
		"own_slow.png",
		"own_speed.png",
		"other_slow.png",
		"other_speed.png",
		"other_swap_control.png",
		"other_thick.png"
	};
	private static final int POWERUP_COUNT = 10;
	private static final int MAX_POWERUPS = 15;	
	
	public PowerUpLoader(ImageLayer backgroundLayer, List<PowerUp> powerUps) {
		this.backgroundLayer = backgroundLayer;
		
		this.rnd = new Random();
		this.frequency = 1000;
		this.minDelay = 1000;
		this.delay = rnd.nextInt(this.frequency) + this.minDelay;
		this.timer = new Timer(delay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (powerUps.size() == PowerUpLoader.MAX_POWERUPS) {
					return;
				}
				
				delay = rnd.nextInt(frequency) + minDelay;
				timer.setDelay(delay);
				
				PowerUp newPower = createPowerUp();
				powerUps.add(newPower);
				
				drawPowerUpIcon(newPower);
			}
		});
		//timer.setRepeats(false);
	}
	
	public void start() {
		this.timer.start();
	}
	
	public void drawPowerUpIcon(PowerUp powerup) {		
		BufferedImage icon =  null;		
		try {
			icon = ImageIO.read(new File("images\\powerups\\" + powerup.getName()));						
		} catch (IOException e) { return; }
		
		this.backgroundLayer.getGr().drawImage(icon, powerup.getX() - PowerUp.POWERUP_RADIUS, powerup.getY() - PowerUp.POWERUP_RADIUS, null);
	}
	
	private PowerUp createPowerUp() {
		int x = rnd.nextInt(this.backgroundLayer.getImg().getWidth() - PowerUp.POWERUP_SIZE / 2) + PowerUp.POWERUP_SIZE / 2;
		int y = rnd.nextInt(this.backgroundLayer.getImg().getHeight() - PowerUp.POWERUP_SIZE / 2)+ PowerUp.POWERUP_SIZE / 2;
		
		return new PowerUp(POWERUP_NAMES[rnd.nextInt(POWERUP_COUNT)], x, y);
	}
	
	/*************************************************************************************************************
	 * 
	 * CUSTOM POWERUP ACTIONS
	 * 
	 *************************************************************************************************************/
	
	public void action_moreExtra() {
		int _minDelay = minDelay;
		int _frequency = frequency;
		this.minDelay = 0;
		this.frequency = 500;
		Timer timer = new Timer(2000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				minDelay = _minDelay;
				frequency = _frequency;
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
	
	public static void action_noBorder(PlayGround pl) {
		pl.setBorder(BorderFactory.createLineBorder(new Color(75,75,75), GameController.PLAYGROUND_BORDER_WIDTH));
		pl.setNoBorder(true);
		
		PowerUpTask task = new PowerUpTask(5000, false, pl.getCurveWindow().getGeneralProgressPane(), new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				pl.setBorder(BorderFactory.createLineBorder(Color.WHITE, GameController.PLAYGROUND_BORDER_WIDTH));
				pl.setNoBorder(false);
				return null;
			}
		});
		task.start();
		pl.getPowerUpTasks().add(task);
	}
	
	public static void action_erase(PlayGround pl) {
		int w = pl.getBackgroundLayer().getImg().getWidth();
		int h = pl.getBackgroundLayer().getImg().getHeight();
		
		pl.getBackgroundLayer().getGr().setColor(GameController.PLAYGROUND_BACKGROUND);
		pl.getBackgroundLayer().getGr().fillRect(0, 0, w, h);
		
		pl.getCompressedLayer().getGr().setColor(GameController.PLAYGROUND_BACKGROUND);
		pl.getCompressedLayer().getGr().fillRect(0, 0, w, h);
		
		Graphics2D gr = pl.getCurvesLayer().getImg().createGraphics();
		gr.setBackground(new Color(0,0,0,0));
		gr.clearRect(0, 0, w, h);
		gr.dispose();
		
		pl.getPowerUps().clear();				
	}
	
	public static void action_ownFly(PlayGround pl, Curve curve, int index) {
		curve.setPaused(true);
		curve.setFlyCount(curve.getFlyCount() + 1);
		
		PowerUpTask task = new PowerUpTask(5000, true, pl.getCurveWindow().getPlayerStatusPanes().get(index), new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				if (curve.getFlyCount() == 1)
					curve.setPaused(false);
				curve.setFlyCount(curve.getFlyCount() - 1);
				return null;
			}
		});
		task.start();
		pl.getPowerUpTasks().add(task);
	}
	
	public static void action_ownSlow(PlayGround pl, Curve curve, int index) {
		curve.slowDown();		
		
		PowerUpTask task = new PowerUpTask(5000, true, pl.getCurveWindow().getPlayerStatusPanes().get(index), new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				curve.speedUp();
				return null;
			}
		});
		task.start();
		pl.getPowerUpTasks().add(task);
	}

	public static void action_ownSpeed(PlayGround pl, Curve curve, int index) {
		curve.speedUp();		
		
		PowerUpTask task = new PowerUpTask(5000, true, pl.getCurveWindow().getPlayerStatusPanes().get(index), new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				curve.slowDown();
				return null;
			}
		});
		task.start();
		pl.getPowerUpTasks().add(task);
	}
	
	public static void action_otherSlow(PlayGround pl, int index) {
		for (int i = 0; i < pl.getPlayers(); ++i) {
			if (i == index)
				continue;
			PowerUpLoader.action_ownSlow(pl, pl.getCurves()[i], i);
		}
	}
	
	public static void action_otherSpeed(PlayGround pl, int index) {
		for (int i = 0; i < pl.getPlayers(); ++i) {
			if (i == index)
				continue;
			PowerUpLoader.action_ownSpeed(pl, pl.getCurves()[i], i);
		}	
	}
	
	public static void action_otherSwapControl(PlayGround pl, int index) {
		for (int i = 0; i < pl.getPlayers(); ++i) {
			if (i == index)
				continue;
			PowerUpLoader.action_ownSwapControl(pl, pl.getCurves()[i], i);
		}
	}
	
	public static void action_ownSwapControl(PlayGround pl, Curve curve, int index) {
		pl.getCurveWindow().getCtrl().get(index).swap();
		
		PowerUpTask task = new PowerUpTask(5000, true, pl.getCurveWindow().getPlayerStatusPanes().get(index), new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				pl.getCurveWindow().getCtrl().get(index).swap();
				return null;
			}
		});
		task.start();
		pl.getPowerUpTasks().add(task);
	}	
	
	public static void action_otherThick(PlayGround pl, int index) {
		for (int i = 0; i < pl.getPlayers(); ++i) {
			if (i == index)
				continue;
			PowerUpLoader.action_ownThick(pl, pl.getCurves()[i], i);
		}
	}
	
	public static void action_ownThick(PlayGround pl, Curve curve, int index) {
		curve.thickUp();		
		
		PowerUpTask task = new PowerUpTask(5000, true, pl.getCurveWindow().getPlayerStatusPanes().get(index), new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				curve.thickDown();
				return null;
			}
		});
		task.start();
		pl.getPowerUpTasks().add(task);
	}
	
}
