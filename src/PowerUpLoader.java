import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.Timer;

public class PowerUpLoader {
	
	private ImageLayer backgroundLayer;
	private Timer timer;
	private int delay;
	private int frequency; /// max milli sec. between powerups
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
		"other_thick"
	};
	private static final int POWERUP_COUNT = 10;
	private static final int MAX_POWERUPS = 15;	
	
	public PowerUpLoader(ImageLayer backgroundLayer, List<PowerUp> powerUps) {
		this.backgroundLayer = backgroundLayer;
		
		this.rnd = new Random();
		this.frequency = 3000;
		this.delay = rnd.nextInt(this.frequency) + 500;
		this.timer = new Timer(delay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (powerUps.size() == PowerUpLoader.MAX_POWERUPS) {
					return;
				}
				
				delay = rnd.nextInt(frequency) + 500;
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
	
	private void drawPowerUpIcon(PowerUp powerup) {		
		BufferedImage icon =  null;
		int iconHeight = 0;
		try {
			icon = ImageIO.read(new File("images\\powerups\\" + powerup.getName()));
			iconHeight = icon.getHeight();			
		} catch (IOException e) { return; }
		
		this.backgroundLayer.getGr().drawImage(icon, powerup.getX() - PowerUp.POWERUP_RADIUS, powerup.getY() - PowerUp.POWERUP_RADIUS, null);
	}
	
	private PowerUp createPowerUp() {
		int x = rnd.nextInt(this.backgroundLayer.getImg().getWidth() - PowerUp.POWERUP_SIZE / 2) + PowerUp.POWERUP_SIZE / 2;
		int y = rnd.nextInt(this.backgroundLayer.getImg().getHeight() - PowerUp.POWERUP_SIZE / 2)+ PowerUp.POWERUP_SIZE / 2;
		
		return new PowerUp(POWERUP_NAMES[rnd.nextInt(POWERUP_COUNT - 1)], x, y);
	}
	
	/*************************************************************************************************************
	 * 
	 * CUSTOM POWERUP ACTIONS
	 * 
	 *************************************************************************************************************/
	
	public static void action_moreExtra() {
		
	}
	
	public static void action_noBorder() {
		
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
	
	public static void action_ownFly(Curve curve) {
		curve.setPaused(true);
		Timer timer = new Timer(5000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curve.setPaused(false);
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
	
	public static void action_ownSlow(Curve curve) {
		curve.slowDown();		
		Timer timer = new Timer(5000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curve.speedUp();
			}
		});
		timer.setRepeats(false);
		timer.start();
	}

	public static void action_ownSpeed(Curve curve) {
		curve.speedUp();		
		Timer timer = new Timer(5000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curve.slowDown();
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
	
	public static void action_otherSlow() {
		
	}
	
	public static void action_otherSpeed() {
		
	}
	
	public static void action_otherSwapControl() {
		
	}
	
	public static void action_otherThick() {
		
	}
	
}
