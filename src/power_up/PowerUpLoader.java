package power_up;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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

import curve.Player;
import curve_window.ImageLayer;
import curve_window.PlayGround;
import generals.GameController;
import generals.Main;
import network_packages.PlayInfoPowerUp;
import networking.ServerThread.ClientHandler;

public class PowerUpLoader {
	
	private PlayGround pl;
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
	private static final int POWERUP_COUNT = POWERUP_NAMES.length;
	private static final int MAX_POWERUPS = 15;	
	
	private Timer borderShrinker = null;
	
	public PowerUpLoader(PlayGround pl) {
		this.pl = pl;
		this.backgroundLayer = pl.getBackgroundLayer();
		
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
				PowerUpLoader.drawPowerUpIcon(backgroundLayer, newPower);
				
				broadCastPowerUp(newPower);
			}
		});
		
		this.powerUpTasks = new ArrayList<PowerUpTask>();
	}
	
	public void start() {
		this.timer.start();
	}
	
	public static void drawPowerUpIcon(ImageLayer backgroundLayer, PowerUp powerup) {		
		BufferedImage icon =  null;		
		try {
			icon = ImageIO.read(PowerUpLoader.class.getResource("/powerups/" + powerup.getName()));						
		} catch (IOException e) { return; }
		
		backgroundLayer.getGr().drawImage(icon, powerup.getX() - PowerUp.POWERUP_RADIUS, powerup.getY() - PowerUp.POWERUP_RADIUS, null);
	}
	
	private PowerUp createPowerUp() {
		int shrinkX = pl.getShrinkedX() >> 1;
		int shrinkY = pl.getShrinkedY() >> 1;
		int x = rnd.nextInt(this.backgroundLayer.getImg().getWidth() - PowerUp.POWERUP_SIZE - shrinkX) + PowerUp.POWERUP_SIZE / 2 + shrinkX;
		int y = rnd.nextInt(this.backgroundLayer.getImg().getHeight() - PowerUp.POWERUP_SIZE - shrinkY)+ PowerUp.POWERUP_SIZE / 2 + shrinkY;
		
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
			PowerUpLoader.drawPowerUpIcon(backgroundLayer, p);
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
		if (pl.isShrinking())
			return;
		pl.setShrinking(true);
		int deltaX = 2;
		int originalX = (int)pl.getSize().getWidth();
		
		int plOriginalX = pl.getCurveWindow().getCurveWindowSizeX();
		int plOriginalY = pl.getCurveWindow().getCurveWindowSizeY() - GameController.MENU_HEIGHT - GameController.PLAYER_STATUS_PANE_HEIGHT;;
		borderShrinker = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int newX = pl.getWidth() - deltaX;
				if (newX <= originalX * 75/100) {
					borderShrinker.stop();
					pl.setShrinking(false);
					return;
				}
				int newY = (int)(pl.getHeight() * (pl.getWidth() - deltaX)) / pl.getWidth();
				int deltaY = pl.getHeight() - newY;
				pl.setShrinkedX(pl.getShrinkedX() + deltaX);
				pl.setShrinkedY(pl.getShrinkedY() + deltaY);
				pl.setBounds((plOriginalX - newX) >> 1, (plOriginalY - newY) >> 1, newX, newY);
			}
		});
		borderShrinker.start();		
	}
	
	public void action_bulldozer(PlayGround pl, List<Player> players) {
		
		for (Player player : players) {			
			player.getCurve().setBulldozerCount(player.getCurve().getBulldozerCount() + 1);			
		}
		
		PowerUpTask task = new PowerUpTask(pl.getCurveWindow(), 5000, true, players);
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				for (Player player : players) {			
					player.getCurve().setBulldozerCount(player.getCurve().getBulldozerCount() - 1);			
				}
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
	}
	
	
	public void action_swapCurves(PlayGround pl) {
		
		List<Player> currentPlayers = new ArrayList<>();
		List<Player> newPlayers = new ArrayList<>();
		
		for (Player p : pl.getAllPlayers()) {
			if (p.isDead())
				continue;
			currentPlayers.add(p);
			newPlayers.add(new Player(p));
		}
		
		action_fly(pl, currentPlayers);
		Collections.shuffle(newPlayers);
		
		int i = 0;
		for (Player p : pl.getAllPlayers()) {
			if (p.isDead())
				continue;
			p.getCurve().setDirection(newPlayers.get(i).getCurve().getDirection());
			p.getCurve().setX(newPlayers.get(i).getCurve().getX());
			p.getCurve().setY(newPlayers.get(i).getCurve().getY());
			++i;
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
	
	public void action_fly(PlayGround pl, List<Player> players) {
		
		for (Player i : players) {
			i.getCurve().setPaused(true);
			i.getCurve().setFlyCount(i.getCurve().getFlyCount() + 1);
		}
		
		PowerUpTask task = new PowerUpTask(pl.getCurveWindow(), 5000, true, players);
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				
				for (Player i : players) {
					if (i.getCurve().getFlyCount() == 1) {
						i.getCurve().setPaused(false);
					}
					i.getCurve().setFlyCount(i.getCurve().getFlyCount() - 1);
				}
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
	}
	
	public void action_slow(PlayGround pl, List<Player> players) {
		
		for (Player i : players) {
			i.getCurve().slowDown();
		}
		
		PowerUpTask task = new PowerUpTask(pl.getCurveWindow(), 5000, true, players);
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				for (Player i : players) {
					i.getCurve().speedUp();
				}
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
	}

	public void action_speed(PlayGround pl, List<Player> players) {
		
		for (Player i : players) {
			i.getCurve().speedUp();
		}
		
		PowerUpTask task = new PowerUpTask(pl.getCurveWindow(), 5000, true, players);
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				for (Player i : players) {
					i.getCurve().slowDown();
				}
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
	}

	public void action_swapControl(PlayGround pl, List<Player> players) {
		
		for (Player i : players) {
			i.getCurve().swapControl(true);
			i.getControl().swap();
		}
		
		PowerUpTask task = new PowerUpTask(pl.getCurveWindow(), 5000, true, players);
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				
				for (Player i : players) {
					i.getControl().swap();
					i.getCurve().swapControl(false);
					
				}
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
	}	
	
	public void action_thick(PlayGround pl, List<Player> players) {
		
		for (Player i : players) {
			i.getCurve().thickUp();
		}
		
		PowerUpTask task = new PowerUpTask(pl.getCurveWindow(), 5000, true, players);
		task.setCallback(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				for (Player i : players) {
					i.getCurve().thickDown();
				}
				return null;
			}
		});
		task.start();
		this.powerUpTasks.add(task);
	}
	
	private void broadCastPowerUp(PowerUp newPowerUp) {
		if (Main.getGameServer() == null) {
			return;
		}
		System.out.println("server casting powerup");
		for (ClientHandler clientHandler : Main.getGameServer().getServerThread().getClients().values()) {
			try {
				System.out.println("server casting powerup to ONE");
				clientHandler.writeToClient(new PlayInfoPowerUp(newPowerUp));
			} catch (IOException ex) {
				System.out.println("Could not broadcast powerup");
			}
		}
	}
	
}
