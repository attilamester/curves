import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
		"bulldozer.png",
		"swap.png",
		"own_fly.png",
		"own_slow.png",
		"own_speed.png",
		"other_slow.png",
		"other_speed.png",
		"other_swap_control.png",
		"other_thick.png"		
	};
	private static final int POWERUP_COUNT = 12;
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
		PowerUpTask task = new PowerUpTask(pl.getCurveWindow(), GameController.GENERAL_TASK_TIMER_LENGTH, false, null);
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
	}
	
	public void action_bulldozer(PlayGround pl, List<Integer> indexes) {
		
		for (Integer i : indexes) {			
			pl.getCurves()[i].setBulldozerCount(pl.getCurves()[i].getBulldozerCount() + 1);			
		}
		
		PowerUpTask task = new PowerUpTask(pl.getCurveWindow(), 5000, true, indexes);
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				for (Integer i : indexes) {
					pl.getCurves()[i].setBulldozerCount(pl.getCurves()[i].getBulldozerCount() - 1);
				}
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
	}
	
	
	public void action_swapCurves(PlayGround pl) {
		
		List<Integer> indexes = new ArrayList<>();
		
		List<Integer> players = new ArrayList<>();
		List<Direction> directions = new ArrayList<>();
		List<Double> oldX = new ArrayList<>();
		List<Double> oldY = new ArrayList<>();
		
		for (int i = 0; i < pl.getPlayers(); ++i) {
			indexes.add(i);
			players.add(i);
			directions.add(new Direction(pl.getCurves()[i].getDirection()));
			oldX.add(new Double(pl.getCurves()[i].getX()));
			oldY.add(new Double(pl.getCurves()[i].getY()));
		}
		
		Collections.shuffle(players);
		
		action_fly(pl, indexes);
		
		for (int i = 0; i < pl.getPlayers(); ++i) {						
			pl.getCurves()[i].setDirection(directions.get(players.get(i)));
			pl.getCurves()[i].setX(oldX.get(players.get(i)));
			pl.getCurves()[i].setY(oldY.get(players.get(i)));			
		}
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
	
	public void action_fly(PlayGround pl, List<Integer> indexes) {
		
		for (Integer i : indexes) {
			pl.getCurves()[i].setPaused(true);
			pl.getCurves()[i].setFlyCount(pl.getCurves()[i].getFlyCount() + 1);
		}
		
		PowerUpTask task = new PowerUpTask(pl.getCurveWindow(), 5000, true, indexes);
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				
				for (Integer i : indexes) {
					if (pl.getCurves()[i].getFlyCount() == 1)
						pl.getCurves()[i].setPaused(false);
					pl.getCurves()[i].setFlyCount(pl.getCurves()[i].getFlyCount() - 1);
				}				
				//powerUpTasks.remove(task);
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
	}
	
	public void action_slow(PlayGround pl, List<Integer> indexes) {
		
		for (Integer i : indexes) {
			pl.getCurves()[i].slowDown();
		}
		
		PowerUpTask task = new PowerUpTask(pl.getCurveWindow(), 5000, true, indexes);
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				for (Integer i : indexes) {
					pl.getCurves()[i].speedUp();
				}
				//powerUpTasks.remove(task);
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
	}

	public void action_speed(PlayGround pl, List<Integer> indexes) {
		
		for (Integer i : indexes) {
			pl.getCurves()[i].speedUp();
		}
		
		PowerUpTask task = new PowerUpTask(pl.getCurveWindow(), 5000, true, indexes);
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				for (Integer i : indexes) {
					pl.getCurves()[i].slowDown();
				}
				//powerUpTasks.remove(task);
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
	}
	
	/**
	 * 
	 * for (int i = 0; i < pl.getPlayers(); ++i) {
			if (i == index || !pl.getPlayersStillAlive().contains(new Integer(i)))
				continue;
	 * 
	 * 
	 */

	public void action_swapControl(PlayGround pl, List<Integer> indexes) {
		
		for (Integer i : indexes) {
			pl.getCurves()[i].setSwapCount(pl.getCurves()[i].getSwapCount() + 1);
			pl.getCurveWindow().getCtrl().get(i).swap();
		}
		
		PowerUpTask task = new PowerUpTask(pl.getCurveWindow(), 5000, true, indexes);
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				for (Integer i : indexes) {
					pl.getCurveWindow().getCtrl().get(i).swap();
					pl.getCurves()[i].setSwapCount(pl.getCurves()[i].getSwapCount() - 1);					
				}				
				//powerUpTasks.remove(task);
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
	}	
	
	public void action_thick(PlayGround pl, List<Integer> indexes) {
		
		for (Integer i : indexes) {
			pl.getCurves()[i].thickUp();
		}
		
		PowerUpTask task = new PowerUpTask(pl.getCurveWindow(), 5000, true, indexes);
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				for (Integer i : indexes) {
					pl.getCurves()[i].thickDown();
				}
				//powerUpTasks.remove(task);
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
	}
	
}
