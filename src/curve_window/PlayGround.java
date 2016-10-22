package curve_window;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import curve.Curve;
import curve.Direction;
import curve.Player;
import generals.GameController;
import generals.Main;
import modals.CountDownModal;
import modals.EndGameModal;
import network_packages.PlayInfo;
import power_up.PowerUp;
import power_up.PowerUpLoader;

public class PlayGround extends JPanel {

	private static final long serialVersionUID = 1L;

	private Random rnd = new Random();

	private CurveWindow curveWindow; // JUST for countDown needed
	private PowerUpLoader powerUpLoader;

	private volatile ImageLayer backgroundLayer;
	private ImageLayer curvesLayer;

	private ImageLayer compressedLayer;
	private ImageLayer timeLayer;

	private int PADDING;
	private int shrinkedX = 0;
	private int shrinkedY = 0;
	private volatile boolean isShrinking = false;
	/********************************
	 * Explicite playground
	 ********************************/
	private int playGroundSizeX;
	private int playGroundSizeY;
	
	private final List<String> ALL_NAMES;
	private List<Player> localPlayers;
	private List<Player> remotePlayers;
	private List<Player> allPlayers;
	private final int ALL_PLAYER_COUNT;
	private int deadPLayerCount;
	
	private int round;

	private boolean playgroundLoading;

	private boolean noBorder = false;
	
	
	public PlayGround(CurveWindow curveWindow,
			List<String> localNames, List<Color> localColors,
			List<String> remoteNames, List<Color> remoteColors,
			int playGroundSizeX, int playGroundSizeY) {

		this.playGroundSizeX = playGroundSizeX;
		this.playGroundSizeY = playGroundSizeY;
		this.PADDING = this.playGroundSizeX / 5;
		
		this.curveWindow = curveWindow;
		this.round = 1;

		
		this.ALL_NAMES = new ArrayList<>();
		this.ALL_NAMES.addAll(localNames);
		this.ALL_NAMES.addAll(remoteNames);
		this.localPlayers = new ArrayList<>();
		this.remotePlayers = new ArrayList<>();
		this.allPlayers = new ArrayList<>();
		this.ALL_PLAYER_COUNT = this.ALL_NAMES.size();
		this.deadPLayerCount = 0;

		
		createPlayers(this.localPlayers, localNames, localColors);
		createPlayers(this.remotePlayers, remoteNames, remoteColors);

		this.allPlayers.addAll(this.localPlayers);
		this.allPlayers.addAll(this.remotePlayers);
		
		setBorder(GameController.PLAYGROUND_BORDER_FACTORY);
		this.setBounds(0, 0, playGroundSizeX, playGroundSizeY);
		this.playgroundLoading = true;
	}

	private void createPlayers(List<Player> players, List<String> names, List<Color> colors) {
		Direction dir = new Direction();
		byte multiplier = 1;
		for (int i = 0; i < names.size(); ++i) {
			if (rnd.nextBoolean())
				multiplier *= -1;
			dir.setI(rnd.nextDouble() * GameController.DEFAULT_CURVE_SPEED * multiplier);

			if (rnd.nextBoolean())
				multiplier *= -1;
			dir.setJ(Math.sqrt(Math.pow(GameController.DEFAULT_CURVE_SPEED, 2) - Math.pow(dir.getI(), 2)) * multiplier);
			
			players.add(new Player(names.get(i), colors.get(i),
				new Curve(createCoordinate_X(), createCoordinate_Y(), GameController.DEFAULT_THICK,
					GameController.DEFAULT_CURVE_ANGLE, colors.get(i), dir), this.curveWindow.getCtrlsToListen().get(i)));
		}
		
	}
	
	/************************************************************************************************************************
	 * 
	 * PAINT PAINT PAINT PAINT PAINT PAINT PAINT PAINT
	 * 
	 ************************************************************************************************************************/

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.compressedLayer == null || this.playgroundLoading) {

			if (this.compressedLayer == null) {
				this.compressedLayer = new ImageLayer(this.getWidth(), this.getHeight(),
						GameController.PLAYGROUND_BACKGROUND, BufferedImage.TYPE_INT_ARGB);
				this.backgroundLayer = new ImageLayer(this.getWidth(), this.getHeight(),
						GameController.PLAYGROUND_BACKGROUND, BufferedImage.TYPE_INT_ARGB);
				this.curvesLayer = new ImageLayer(this.getWidth(), this.getHeight(), null, BufferedImage.TYPE_INT_ARGB);
				// this.extrasLayer = new ImageLayer(this.getWidth(),
				// this.getHeight(), null);
				this.timeLayer = new ImageLayer(this.getWidth(), this.getHeight(), null, BufferedImage.TYPE_INT_RGB);
				
				this.powerUpLoader = new PowerUpLoader(this);
			} else {
				this.backgroundLayer.getGr().setColor(GameController.PLAYGROUND_BACKGROUND);
				this.backgroundLayer.getGr().fillRect(0, 0, this.backgroundLayer.getImg().getWidth(),
						this.backgroundLayer.getImg().getHeight());
				this.compressedLayer.getGr().setColor(GameController.PLAYGROUND_BACKGROUND);
				this.compressedLayer.getGr().fillRect(0, 0, this.backgroundLayer.getImg().getWidth(),
						this.backgroundLayer.getImg().getHeight());

				this.curvesLayer = new ImageLayer(this.getWidth(), this.getHeight(), null, BufferedImage.TYPE_INT_ARGB);
				this.timeLayer = new ImageLayer(this.getWidth(), this.getHeight(), null, BufferedImage.TYPE_INT_RGB);
				// this.curvesLayer.getGr().setColor(GameController.PLAYGROUND_BACKGROUND);
				// this.curvesLayer.getGr().fillRect(0, 0,
				// this.backgroundLayer.getImg().getWidth(),
				// this.backgroundLayer.getImg().getHeight());
			}

			this.initPaint(g);

		} else {
			
			g.drawImage(this.backgroundLayer.getImg(), -(this.shrinkedX >> 1), -(this.shrinkedY >> 1), null);
			
			for(Player player : this.localPlayers) {
				
				int x = (int) player.getCurve().getX();
				int y = (int) player.getCurve().getY();
				int r = player.getCurve().getRadius();
				int padding = r + GameController.PLAYGROUND_BORDER_WIDTH;

				if (outOfBorderBounds(player, x, y, padding)) {
					g.drawImage(curvesLayer.getImg(), -(this.shrinkedX >> 1), -(this.shrinkedY >> 1), null);
					managePlayerDeath(player);
					return;
				}
				
				if (player.getCurve().getBulldozerCount() == 0) {
					if (crashedToSomething(player)) {
						g.drawImage(curvesLayer.getImg(), -(this.shrinkedX >> 1), -(this.shrinkedY >> 1), null);
						managePlayerDeath(player);
						return;
					}
				}

				if (player.getCurve().isPaused()) {
					/**
					 * DELETE PREVIOUS CIRCLE
					 */
					// curves[i].getCurveLayer().getGr().setColor(GameController.PLAYGROUND_BACKGROUND);
					// curves[i].getCurveLayer().getGr().fillOval((int)curves[i].getOldX()
					// - r, (int)curves[i].getOldY() - r, 2 * r, 2 * r);
					///// OLD WAY

					/**
					 * New way: paint only to refreshed dash-layer
					 */
					// curves[i].resetDashLayer();
					// curves[i].getDashLayer().getGr().setColor(Color.RED);
					// curves[i].getDashLayer().getGr().fillOval(x - r, y - r, 2
					// * r, 2 * r);

					/**
					 * Actually working version :D
					 */
					g.setColor(player.getColor());
					g.fillOval(x - r -(this.shrinkedX >> 1), y - r -(this.shrinkedY >> 1), r << 1, r << 1);

				} else {
					// this.getGr().setColor(curve.getColor());
					// this.getGr().fillOval(x - r, y - r, 2 * r, 2 * r);
					/**
					 * GOOD but maybe no need for curvelayer. JUST compressed
					 * layer
					 */
					// curves[i].getCurveLayer().getGr().setColor(curves[i].getColor());
					// curves[i].getCurveLayer().getGr().fillOval(x - r, y - r,
					// 2 * r, 2 * r);

					// this.curvesLayer.getGr().drawImage(curves[i].getCurveLayer().getImg(),
					// 0, 0, null);
					
					curvesLayer.getGr().setColor(player.getColor());
					curvesLayer.getGr().fillOval(x - r, y - r, r << 1, r << 1);
					
					
					/**
					 * COLLISION PREPARATION
					 *  
					 * ******** ******** ******** ********
					 *    | -> circle count, 28 bits          
					 */	
					int c = player.getCurve().getCircleNumber();
					int col = (player.getId() << 16) + c;
					//System.out.println(col);
					this.timeLayer.getGr().setColor(new Color(col));
					this.timeLayer.getGr().fillOval(x - r, y - r, r << 1, r << 1);
					player.getCurve().setCircleNumber(c + 1);
					
				}
				
				// g.drawImage(curves[i].getCurveLayer().getImg(), 0, 0, null);
				// compressedLayer.getGr().drawImage(curves[i].getCurveLayer().getImg(),
				// 0, 0, null);
				checkForPowerUp(player);
			}
			
			/** Paint other curves' head */
			for (Player player : this.remotePlayers) {
				Curve curve = player.getCurve();
				int x = (int) curve.getX();
				int y = (int) curve.getY();
				int r = curve.getRadius();
				if (player.getCurve().isPaused()) {
					g.setColor(player.getColor());
					g.fillOval(x - r -(this.shrinkedX >> 1), y - r -(this.shrinkedY >> 1), r << 1, r << 1);
				} else {
					curvesLayer.getGr().setColor(player.getColor());
					curvesLayer.getGr().fillOval(x - r, y - r, r << 1, r << 1);
				}
			}
			
			g.drawImage(curvesLayer.getImg(), -(this.shrinkedX >> 1), -(this.shrinkedY >> 1), null);
			this.compressedLayer.getGr().drawImage(curvesLayer.getImg(), 0, 0, null);
			
			for (Player player : this.allPlayers) {
				Curve curve = player.getCurve();
				int x = (int) curve.getX();
				int y = (int) curve.getY();
				int r = curve.getRadius();
				if (curve.getSwapCount() % 2 == 1) {
					g.setColor(Color.BLACK);
					g.fillOval(x - r + 1 -(this.shrinkedX >> 1), y - r + 1 -(this.shrinkedY >> 1), (r << 1) - 2, (r << 1) - 2);
				}
			}
			
		}
	}

	/***********************************************************
	 * 
	 * INIT DIRECTION PAINTING
	 * 
	 ***********************************************************/

	public void initPaint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.drawImage(this.backgroundLayer.getImg(), -(this.shrinkedX >> 1), -(this.shrinkedY >> 1), null);
		System.out.println("initPaint");
		for (Player player : this.allPlayers) {
			int r = player.getCurve().getRadius();
			int color = player.getColor().getRGB();

			BufferedImage direction = null;
			BufferedImage finalImg = null;
			try {
				direction = ImageIO.read(new File("images\\direction.png"));
			} catch (IOException e) {
			}

			ImageFilter filter = new RGBImageFilter() {
				@Override
				public int filterRGB(final int x, final int y, final int rgb) {
					return (rgb >> 24 == 0x00) ? 0 : color;
				}
			};
			ImageProducer ip = new FilteredImageSource(direction.getSource(), filter);
			finalImg = Main.toBufferedImage(Toolkit.getDefaultToolkit().createImage(ip));

			/**************************************
			 * Draw Name Initials
			 *************************************/
			Graphics2D gr2D_initial = finalImg.createGraphics();
			gr2D_initial.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			String text = (player.getName().length() > 0) ? Character.toString(player.getName().charAt(0)) : "?";
			FontRenderContext context = gr2D_initial.getFontRenderContext();
			Font font = new Font("Arial", Font.BOLD, finalImg.getWidth() / 3);
			TextLayout txt = new TextLayout(text, font, context);
			Rectangle2D bounds = txt.getBounds();
			int textX = (int) ((finalImg.getWidth() - (int) bounds.getWidth()) / 2);
			int textY = (int) ((finalImg.getHeight() - (bounds.getHeight() - txt.getDescent())) / 2);
			textY += txt.getAscent() - txt.getDescent();

			gr2D_initial.setFont(font);
			gr2D_initial.setColor(Color.BLACK);
			gr2D_initial.drawString(text, textX, textY);
			gr2D_initial.setColor(Color.WHITE);
			gr2D_initial.drawString(text, textX - 20, textY - 20);

			gr2D_initial.dispose();

			/**
			 * ___________> oX v0 \alpha \ \ _\| v1
			 * 
			 * 
			 * v1 * v0 = |v1| * |v0| * cos alpha
			 * 
			 * =>
			 * 
			 * alpha = arccos(v1 * v0 / || ||)
			 */
			double rotationRequired = Math.acos(player.getCurve().getDirection().getI() / player.getCurve().calcSpeed())
					* Math.signum(player.getCurve().getDirection().getJ()) + Math.PI / 2;
			AffineTransform tx = new AffineTransform();
			double scale = 50.0 / direction.getWidth();
			tx.translate(player.getCurve().getX() - direction.getWidth() * scale / 2,
					player.getCurve().getY() - direction.getHeight() * scale);
			tx.rotate(rotationRequired, direction.getWidth() * scale / 2, direction.getHeight() * scale);
			tx.scale(scale, scale);

			g2d.drawImage(finalImg, tx, null);
			this.curvesLayer.getGr().setColor(player.getCurve().getColor());
			this.curvesLayer.getGr().fillOval((int) player.getCurve().getX() - r, (int) player.getCurve().getY() - r, 2 * r, 2 * r);

		}

		g.drawImage(this.curvesLayer.getImg(), -(this.shrinkedX >> 1), -(this.shrinkedY >> 1), null);

	}

	public void eraseArrows() {
		this.playgroundLoading = false;		
	}

	/***************************************************************************************************************************
	 * 
	 * DEATH + next ROUND
	 * 
	 **************************************************************************************************************************/
	private void managePlayerDeath(Player player) {

		if (player.isDead()) {
			return;
		}
		
		Main.playSound("impact.mp3");
		player.setAlive(false);

		player.getController().stop();

		for (Player other : this.localPlayers) {
			if (other != player) {
				other.increaseScore();
			}
		}
		++this.deadPLayerCount;
		
		if (this.ALL_PLAYER_COUNT - this.deadPLayerCount <= 1) {
			Player winner = null;
			for (Player p : this.localPlayers) {
				if (p.isAlive()) {
					winner = p;
					p.getController().stop();
					break;
				}
			}
			if (winner == null) {
				winner = this.localPlayers.get(0);
			}	 
			
			this.curveWindow.getDisplayRefresher().stopRefresher();
			GameController.finished = true;

			this.powerUpLoader.clearPowerUps();				
			this.powerUpLoader.finishAllTasks();
			
			if (this.round == GameController.ROUND_COUNT) {
				new EndGameModal(this.curveWindow, this.curveWindow.getNamesScores(), this.curveWindow.getPlayerStatusPanes());
			} else {
				new CountDownModal(this.curveWindow, ++round, winner.getName(), winner.getColor());
				
				this.deadPLayerCount = 0;

				Timer timer = new Timer(2000, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						PlayGround.this.startNewRound();
						PlayGround.this.repaint();
					}
				});
				timer.setRepeats(false);
				timer.start();
			}
		}
	}

	private void startNewRound() {

		// PowerUpLoader.action_erase(this.backgroundLayer);
		// PowerUpLoader.action_erase(this.compressedLayer);
		// this.backgroundLayer.getGr().setColor(GameController.PLAYGROUND_BACKGROUND);
		// this.backgroundLayer.getGr().fillRect(0, 0,
		// this.backgroundLayer.getImg().getWidth(),
		// this.backgroundLayer.getImg().getHeight());

		this.playgroundLoading = true;
		this.shrinkedX = 0;
		this.shrinkedY = 0;
		this.setBounds(0, 0, this.playGroundSizeX, this.playGroundSizeY);

		Direction dir = new Direction();
		byte multiplier = 1;

		for (Player p : this.localPlayers) {
			if (rnd.nextBoolean())
				multiplier *= -1;
			dir.setI(rnd.nextDouble() * GameController.DEFAULT_CURVE_SPEED * multiplier);

			if (rnd.nextBoolean())
				multiplier *= -1;
			dir.setJ(Math.sqrt(Math.pow(GameController.DEFAULT_CURVE_SPEED, 2) - Math.pow(dir.getI(), 2)) * multiplier);

			p.getCurve().initData(createCoordinate_X(), createCoordinate_Y(), GameController.DEFAULT_THICK,
					GameController.DEFAULT_CURVE_ANGLE, p.getColor(), dir);
			p.setAlive(true);

		}
	}

	/***************************************************************************************************************************************************************
	 * 
	 * COLLISION and BORDER
	 * 
	 ****************************************************************************************************************************************************************/

	private boolean outOfBorderBounds(Player player, int x, int y, int padding) {
		Curve curve = player.getCurve();
		
		if (x <= padding + (this.shrinkedX >> 1)) {
			if (this.noBorder)
				curve.setX(this.backgroundLayer.getImg().getWidth() - padding - 1 - (this.shrinkedX >> 1));
			else
				return (curve.isPaused() || !player.isAlive()) ? false : true;
		} else if (x >= this.backgroundLayer.getImg().getWidth() - padding - 1 - (this.shrinkedX >> 1)) {
			if (this.noBorder)
				curve.setX(padding + 1 + (this.shrinkedX >> 1));
			else
				return (curve.isPaused() || !player.isAlive()) ? false : true;
		}

		if (y <= padding + (this.shrinkedY >> 1)) {
			if (this.noBorder)
				curve.setY(this.backgroundLayer.getImg().getHeight() - padding - 1 - (this.shrinkedY >> 1));
			else
				return (curve.isPaused() || !player.isAlive()) ? false : true;
		} else if (y >= this.backgroundLayer.getImg().getHeight() - padding - 1 - (this.shrinkedY >> 1)) {
			if (this.noBorder)
				curve.setY(padding + 1 + (this.shrinkedY >> 1));
			else
				return (curve.isPaused() || !player.isAlive()) ? false : true;
		}

		return false;
	}

	private boolean crashedToSomething(Player player) {
		Curve curve = player.getCurve();
		
		if (curve.isPaused() || player.isDead()) {
			return false;
		}

		int r = curve.getRadius();
		double k = r / Math.hypot(curve.getX() - curve.getOldX(), curve.getY() - curve.getOldY());

		Point2D.Double center = new Point2D.Double(curve.getX(), curve.getY());
		Point2D.Double startPoint = new Point2D.Double(center.getX() + (curve.getX() - curve.getOldX()) * k,
				center.getY() + (curve.getY() - curve.getOldY()) * k);
		// System.out.println("\tChecking: " + startPoint.getX() + " ; " +
		// startPoint.getY());
		if (!pointIsOk(player, startPoint)) {
			return true;
		}

		return false;

		/*
		 * 
		 * int alpha = 30; int limit = 30; int nr = limit / alpha;
		 * AffineTransform rot =
		 * AffineTransform.getRotateInstance(Math.toRadians(alpha),
		 * curves[z].getX(), curves[z].getY()); Point2D.Double nextPoint = new
		 * Point2D.Double(startPoint.getX(), startPoint.getY());
		 * 
		 * ////// // // Clockwise direction check // //////
		 * 
		 * for (int ii = 0; ii < nr; ++ii) { rot.transform(nextPoint,
		 * nextPoint); System.out.println("\tChecking: " + nextPoint.getX() +
		 * " ; " + nextPoint.getY()); if (!pointIsOk(nextPoint, curves[z])) {
		 * return true; } } rot.setToRotation(-alpha, curves[z].getX(),
		 * curves[z].getY()); nextPoint.setLocation(startPoint);
		 * 
		 * ////// // // Counter - Clockwise direction check // //////
		 * 
		 * for (int ii = 0; ii < nr; ++ii) { rot.transform(nextPoint,
		 * nextPoint); System.out.println("\tChecking: " + nextPoint.getX() +
		 * " ; " + nextPoint.getY()); if (!pointIsOk(nextPoint, curves[z])) {
		 * return true; } }
		 * 
		 * return false;
		 * 
		 */
	}

	private boolean pointIsOk(Player player, Point2D.Double point) {
		/**
		 * INSIDE PREVIOUS PAINTED CIRCLE => IGNORE IT
		 * (needed JUST if checking more points around circle perimeter)
		 */
		/*
		if (this.point_distance(point.getX(), point.getY(), curve.getOldX(), curve.getOldY()) <= curve.getRadius()) {
			System.out.println("INSIDE OLDDD");
			return true;
		}*/

		int paintedColor;
		try {
			final int[] pixels = ((DataBufferInt) (this.compressedLayer.getImg().getRaster().getDataBuffer()))
					.getData();
			paintedColor = this.compressedLayer.getImg().getRGB((int) point.getX(), (int) point.getY());
			if (paintedColor != this.getRGB_fromByteArray(compressedLayer.getImg().getWidth(), pixels,
					(int) point.getX(), (int) point.getY())) {
				System.out.println(paintedColor + " Mine: " + this.getRGB_fromByteArray(
						compressedLayer.getImg().getWidth(), pixels, (int) point.getX(), (int) point.getY()));
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		/* COLLISION INTO OTHERS */
		for (Player p : this.allPlayers) {
			if (p == player)
				continue;
			if (paintedColor == p.getColor().getRGB()) {
				// death because of i. player. Congrats i. EVIL :))
				p.increaseScore();
				return false;
			}
		}
		// cannot check default color, layer is uninitialized, alpha channel,
		// etc
		if (paintedColor == player.getCurve().getColor().getRGB()) {
			
			int col = this.timeLayer.getImg().getRGB((int) point.getX(), (int) point.getY());			
			int playerIndex = (col >> 16) & 0xF;			
			int circleCount = col & 0x0000FFFF;
			//System.out.println(col + " PLAYER:" + playerIndex + " CIRCLE: " + circleCount);
			
			if (playerIndex == player.getId() && player.getCurve().getCircleNumber() - circleCount <= 10) {
				//System.out.println(curve.getCircleNumber());
				//System.out.println(circleCount);
				return true;
			}
				
			
			return false;			
			/*
			// TRIAL WITH NANO-TIME CHECKING BETWEEN LAST COLLISIONS - not bad, not good either
			// may be still some pixel - bug
			int collision = curve.getCollisionCount();
			long now = System.nanoTime();
			long past = curve.getLastCollidedAt();
			long elapse = now - past;
			System.out.println("\tELAPSE:" + (elapse / 1000000));

			curve.setLastCollidedAt(now);

			if (elapse < 50000000) { // again collision within 30 milliseconds,
										// weird
				if (collision == 5) // too many, means that no pixel-bug
					//return false;
					System.out.println("DETECTED  5   COLLISIONSS!!!!!!!!!!!!!!!!!!!");
				curve.setCollisionCount(++collision);

			} else {
				curve.setCollisionCount(0);
			}
*/
			/*
			 * System.out.println("already colored here: " +
			 * Main.getCssColor(paintedColor) + ", curve color: " +
			 * Main.getCssColor( curve.getColor().getRGB() ) );
			 * System.out.println("Current cir:" + curve.getX() + " " +
			 * curve.getY()); System.out.println("Prev circle:" +
			 * curve.getOldX() + " " + curve.getOldY()); System.out.println(
			 * "Point      :" + point.getX() + " " + point.getY());
			 * System.out.println("Dist: " +
			 * this.point_distance(curve.getOldX(), curve.getOldY(),
			 * point.getX(), point.getY()));
			 */
		}
		return true;
	}

	private void checkForPowerUp(Player player) {
		Curve curve = player.getCurve();
		List<Player> otherPlayers = new ArrayList<>();
		for (Player p : this.allPlayers) {
			if (p != player && p.isAlive()) {
				otherPlayers.add(p);
			}
		}
		
		for (ListIterator<PowerUp> iter = this.powerUpLoader.getPowerUps().listIterator(); iter.hasNext();) {
			PowerUp p = iter.next();
			double dist = point_distance(curve.getX(), curve.getY(), p.getX(), p.getY());
			if (dist <= PowerUp.POWERUP_RADIUS) {
				switch (p.getName()) {
					case "more_extra.png":
						this.powerUpLoader.action_moreExtra(); break;
					case "no_border.png":
						this.powerUpLoader.action_noBorder(this); break;
					case "shrink_border.png":
						this.powerUpLoader.action_shrinkBorder(this); break;
					case "erase.png":
						this.powerUpLoader.action_erase(this); return;
					case "bulldozer.png":
						this.powerUpLoader.action_bulldozer(this, this.allPlayers); break;
					case "swap.png":
						this.powerUpLoader.action_swapCurves(this); break;
					case "own_fly.png":
						this.powerUpLoader.action_fly(this, Arrays.asList(player)); break;
					case "own_slow.png":
						this.powerUpLoader.action_slow(this, Arrays.asList(player)); break;
					case "own_speed.png":
						this.powerUpLoader.action_speed(this, Arrays.asList(player)); break;
					case "other_slow.png":
						this.powerUpLoader.action_slow(this, otherPlayers); break;
					case "other_speed.png":
						this.powerUpLoader.action_speed(this, otherPlayers); break;
					case "other_swap_control.png":
						this.powerUpLoader.action_swapControl(this, otherPlayers); break;
					case "other_thick.png":
						this.powerUpLoader.action_thick(this, otherPlayers); break;
				}

				this.backgroundLayer.getGr().setColor(GameController.PLAYGROUND_BACKGROUND);
				this.backgroundLayer.getGr().fillRect(0, 0, this.backgroundLayer.getImg().getWidth(),
						this.backgroundLayer.getImg().getHeight());

				try {
					iter.remove();
				} catch (java.util.ConcurrentModificationException e) {}

				this.getPowerUpLoader().reDrawPowerUps();
			}
		}
	}
	
	private double point_distance(double x1, double y1, double x2, double y2) {
		return Math.hypot(x1 - x2, y1 - y2);
	}

	private int getRGB_fromByteArray(final int imageWidth, final int[] pixels, int i, int j) {
		return pixels[j * imageWidth + i];
	}

	public void startGame() {
		for (Player pl : this.localPlayers) {
			pl.getController().start();
		}
	}

	public void restartGame() {
		this.deadPLayerCount = 0;
		
		for (Player pl : this.localPlayers) {
			pl.getController().restart();
		}
	}
	
	public void stopEvent() {
		for (Player pl : this.localPlayers) {
			pl.getController().stop();
		}
		//this.curveWindow.getDisplayRefresher().stopRefresher();
	}
	
	public void resumeEvent() {
		for (Player pl : this.localPlayers) {
			if (pl.isAlive()) {
				pl.getController().restart();
			}
		}
			
		//this.curveWindow.getDisplayRefresher().restartRefresher();
	}

	public void leftTurnTriggered(Player player) {
		player.getCurve().setLeftPressed(true);
	}

	public void rightTurnTriggered(Player player) {
		player.getCurve().setRightPressed(true);
	}

	public void leftTurnStopped(Player player) {
		player.getCurve().setLeftPressed(false);
	}

	public void rightTurnStopped(Player player) {
		player.getCurve().setRightPressed(false);
	}

	public int randBetween(int a, int b) {
		return this.rnd.nextInt(b - a) + a;
	}
	
	private double createCoordinate_X() {
		int marginFromCountDown = 20;
		double x = randBetween(PADDING, this.playGroundSizeX - PADDING);
		if (x >= (this.playGroundSizeX - GameController.COUNT_DOWN_WIDTH) / 2 - marginFromCountDown
				&& x <= (this.playGroundSizeX + GameController.COUNT_DOWN_WIDTH) / 2 + marginFromCountDown) {
			//x += GameController.COUNT_DOWN_WIDTH;
		}
		return x;
	}

	private double createCoordinate_Y() {
		double y = randBetween(PADDING, this.playGroundSizeY - PADDING);
		int marginFromCountDown = 20;
		if (y >= (this.playGroundSizeY - GameController.COUNT_DOWN_HEIGHT) / 2 - marginFromCountDown
				&& y <= (this.playGroundSizeY + GameController.COUNT_DOWN_HEIGHT) / 2 + marginFromCountDown) {
			//y += GameController.COUNT_DOWN_HEIGHT;
		}
		return y;
	}

	public PowerUpLoader getPowerUpLoader() {
		return this.powerUpLoader;
	}

	public ImageLayer getBackgroundLayer() {
		return backgroundLayer;
	}

	public ImageLayer getCurvesLayer() {
		return curvesLayer;
	}

	public ImageLayer getCompressedLayer() {
		return compressedLayer;
	}
	
	public void setNoBorder(boolean noBorder) {
		this.noBorder = noBorder;
	}
	
	public boolean getNoBorder() {
		return this.noBorder;
	}

	public CurveWindow getCurveWindow() {
		return curveWindow;
	}


	public int getShrinkedX() {
		return shrinkedX;
	}

	public void setShrinkedX(int shrinkedX) {
		this.shrinkedX = shrinkedX;
	}

	public int getShrinkedY() {
		return shrinkedY;
	}

	public void setShrinkedY(int shrinkedY) {
		this.shrinkedY = shrinkedY;
	}

	public List<Player> getLocalPlayers() {
		return localPlayers;
	}

	public List<Player> getAllPlayers() {
		return allPlayers;
	}

	public int getDeadPLayerCount() {
		return deadPLayerCount;
	}

	public boolean isShrinking() {
		return isShrinking;
	}

	public void setShrinking(boolean isShrinking) {
		this.isShrinking = isShrinking;
	}
	
	
	public void arrivedPlayerList(int clientID, PlayInfo info) {
		synchronized (new Object()) {
			List<Player> clientsPlayers = info.getPlayers();
			for (ListIterator<Player> iter = this.remotePlayers.listIterator();
				iter.hasNext();) {
				Player p = iter.next();
				for (Player pp : clientsPlayers) {					if (p.equals(pp)) {
						p.updateState(pp);
					}
				}
			}
		}
	}
	
	public void sendPlayersToServer() {
		Main.getGameClient().respondToServer(new PlayInfo(Main.getGameClient().getClientID(), this.localPlayers));
	}

}