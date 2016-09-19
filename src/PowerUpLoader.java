import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;
import javax.swing.Timer;

public class PowerUpLoader {
	
	private ImageLayer backgroundLayer;
	private Timer timer;
	private int delay;
	private int frequency; /// max milli sec. between powerups
	private int minDelay;
	private Random rnd;
	
	private List<PowerUp> powerUps;
	private List<PowerUpTask> powerUpTasks;
	
	private static final String[] POWERUP_NAMES = {
		"more_extra.png",
		"no_border.png",
		"shrink_border.png",
		"erase.png",
		"own_fly.png",
		"own_slow.png",
		"own_speed.png",
		"other_slow.png",
		"other_speed.png",
		"other_swap_control.png",
		"other_thick.png"
	};
	private static final int POWERUP_COUNT = 11;
	private static final int MAX_POWERUPS = 15;	
	
	private Timer borderShrinker = null;
	
	public PowerUpLoader(ImageLayer backgroundLayer) {
		this.backgroundLayer = backgroundLayer;
		
		this.powerUps = new ArrayList<PowerUp>();
		
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
		
		this.powerUpTasks = new ArrayList<PowerUpTask>();
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
		int x = rnd.nextInt(this.backgroundLayer.getImg().getWidth() - PowerUp.POWERUP_SIZE) + PowerUp.POWERUP_SIZE / 2;
		int y = rnd.nextInt(this.backgroundLayer.getImg().getHeight() - PowerUp.POWERUP_SIZE)+ PowerUp.POWERUP_SIZE / 2;
		
		int index = 0;
		if (index == 0)
			index = rnd.nextInt(POWERUP_COUNT);
		return new PowerUp(POWERUP_NAMES[index], x, y);
	}
		
	public List<PowerUp> getPowerUps() {
		return powerUps;
	}

	public List<PowerUpTask> getPowerUpTasks() {
		return powerUpTasks;
	}
	
	public void finishAllTasks() {
		int i = 1;
		for (ListIterator<PowerUpTask> iter = this.powerUpTasks.listIterator(); iter.hasNext();) {
			PowerUpTask ref = iter.next();
			if (ref.getState() == PowerUpTask.PROGRESS) {
				try {
					ref.finish();
				} catch (ConcurrentModificationException e) {}
			}
		}
		this.powerUpTasks.clear();
		
		if (this.borderShrinker != null)
			this.borderShrinker.stop();
	}
	
	public void clearPowerUps() {
		this.powerUps.clear();
	}

	public void reDrawPowerUps() {
		for (ListIterator<PowerUp> iter = this.powerUps.listIterator(); iter.hasNext();) {
			PowerUp p = iter.next();
			this.drawPowerUpIcon(p);
		}		
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
		this.frequency = 100;
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
	
	public void action_noBorder(PlayGround pl) {
		pl.setBorder(GameController.PLAYGROUND_NO_BORDER_FACTORY);
		pl.setNoBorder(true);
		
		if (PowerUpTask.generalStarted) {
			PowerUpTask.generalProgressValue = 0;
			return;
		}
		PowerUpTask task = new PowerUpTask(GameController.GENERAL_TASK_TIMER_LENGTH, false, pl.getCurveWindow().getGeneralProgressPane());
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				pl.setBorder(GameController.PLAYGROUND_BORDER_FACTORY);
				pl.setNoBorder(false);
				//powerUpTasks.remove(task);
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
	}
	
	public void action_shrinkBorder(PlayGround pl) {
		if (pl.getShrinkedX() > 0)
			return;
		int deltaX = 2;
		borderShrinker = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int newX = pl.getWidth() - deltaX;
				if (newX <= GameController.FRAME_SIZE_X * 75/100) {
					borderShrinker.stop();
					return;
				}
				int newY = (int)(pl.getHeight() * (pl.getWidth() - deltaX)) / pl.getWidth();
				int deltaY = pl.getHeight() - newY;
				pl.setShrinkedX(pl.getShrinkedX() + deltaX);
				pl.setShrinkedY(pl.getShrinkedY() + deltaY);
				pl.setBounds((GameController.FRAME_SIZE_X - newX) >> 1, (GameController.FRAME_SIZE_Y - newY) >> 1, newX, newY);
			}
		});
		borderShrinker.start();
		
		/*
		if (PowerUpTask.generalStarted) {
			PowerUpTask.generalProgressValue = 0;
			return;
		}
		PowerUpTask task = new PowerUpTask(GameController.GENERAL_TASK_TIMER_LENGTH, false, pl.getCurveWindow().getGeneralProgressPane());
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				pl.setBorder(GameController.PLAYGROUND_BORDER_FACTORY);
				pl.setNoBorder(false);
				//powerUpTasks.remove(task);
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
		*/
	}
	
	public void action_erase(PlayGround pl) {
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
		
		this.clearPowerUps();
	}
	
	public void action_ownFly(PlayGround pl, Curve curve, int index) {
		curve.setPaused(true);
		curve.setFlyCount(curve.getFlyCount() + 1);
		
		PowerUpTask task = new PowerUpTask(5000, true, pl.getCurveWindow().getPlayerStatusPanes().get(index));
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				if (curve.getFlyCount() == 1)
					curve.setPaused(false);
				curve.setFlyCount(curve.getFlyCount() - 1);
				//powerUpTasks.remove(task);
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
	}
	
	public void action_ownSlow(PlayGround pl, Curve curve, int index) {
		curve.slowDown();		
		
		PowerUpTask task = new PowerUpTask(5000, true, pl.getCurveWindow().getPlayerStatusPanes().get(index));
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				curve.speedUp();
				//powerUpTasks.remove(task);
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
	}

	public void action_ownSpeed(PlayGround pl, Curve curve, int index) {
		curve.speedUp();		
		
		PowerUpTask task = new PowerUpTask(5000, true, pl.getCurveWindow().getPlayerStatusPanes().get(index));
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				curve.slowDown();
				//powerUpTasks.remove(task);
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
	}
	
	public void action_otherSlow(PlayGround pl, int index) {
		for (int i = 0; i < pl.getPlayers(); ++i) {
			if (i == index)
				continue;
			action_ownSlow(pl, pl.getCurves()[i], i);
		}
	}
	
	public void action_otherSpeed(PlayGround pl, int index) {
		for (int i = 0; i < pl.getPlayers(); ++i) {
			if (i == index)
				continue;
			action_ownSpeed(pl, pl.getCurves()[i], i);
		}	
	}
	
	public void action_otherSwapControl(PlayGround pl, int index) {
		for (int i = 0; i < pl.getPlayers(); ++i) {
			if (i == index)
				continue;
			action_ownSwapControl(pl, pl.getCurves()[i], i);
		}
	}
	
	public void action_ownSwapControl(PlayGround pl, Curve curve, int index) {
		curve.setSwapCount(curve.getSwapCount() + 1);
		pl.getCurveWindow().getCtrl().get(index).swap();
		
		PowerUpTask task = new PowerUpTask(5000, true, pl.getCurveWindow().getPlayerStatusPanes().get(index));
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				pl.getCurveWindow().getCtrl().get(index).swap();
				curve.setSwapCount(curve.getSwapCount() - 1);
				//powerUpTasks.remove(task);
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
	}	
	
	public void action_otherThick(PlayGround pl, int index) {
		for (int i = 0; i < pl.getPlayers(); ++i) {
			if (i == index)
				continue;
			action_ownThick(pl, pl.getCurves()[i], i);
		}
	}
	
	public void action_ownThick(PlayGround pl, Curve curve, int index) {
		curve.thickUp();		
		
		PowerUpTask task = new PowerUpTask(5000, true, pl.getCurveWindow().getPlayerStatusPanes().get(index));
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				curve.thickDown();
				//powerUpTasks.remove(task);
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
	}
	
}
