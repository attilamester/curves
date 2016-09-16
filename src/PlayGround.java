import java.awt.Color;
import java.awt.Dimension;
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
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class PlayGround extends JPanel {

	private static final long serialVersionUID = 1L;

	private Random rnd = new Random();

	private CurveWindow curveWindow; // JUST for countDown needed
	private PowerUpLoader powerUpLoader;

	private volatile ImageLayer backgroundLayer;
	private ImageLayer curvesLayer;

	private ImageLayer compressedLayer;
	private ImageLayer timeLayer;

	private int defaultLayerColor;
	private final int PADDING = GameController.FRAME_SIZE_X / 5;

	/********************************
	 * Explicite playground
	 ********************************/
	private int players;
	private List<String> names;
	private Curve[] curves;
	private CurveController[] curveControllers;
	private List<Control> controls;
	private List<Color> colors;
	private List<Integer> playersStillAlive;
	private List<Integer> playersDead;
	private int round;

	private boolean playgroundLoading;

	private List<PowerUp> powerUps;

	private int noBorder = 0;
	
	private final int playerBits = 4;
	
	public PlayGround(CurveWindow curveWindow, int players, List<String> names, List<Control> controls,
			List<Color> colors) {

		this.curveWindow = curveWindow;
		this.controls = controls;
		this.round = 1;

		this.names = new ArrayList<String>(names);
		this.players = players;

		/** List of player numbers */
		this.playersStillAlive = new ArrayList<Integer>();
		for (int i = 0; i < players; ++i)
			playersStillAlive.add(new Integer(i));
		this.playersDead = new ArrayList<Integer>();

		this.colors = new ArrayList<Color>(colors);

		curves = new Curve[players];
		curveControllers = new CurveController[players];

		Direction dir = new Direction();
		byte multiplier = 1;

		for (int i = 0; i < players; ++i) {
			if (rnd.nextBoolean())
				multiplier *= -1;
			dir.setI(rnd.nextDouble() * GameController.DEFAULT_CURVE_SPEED * multiplier);

			if (rnd.nextBoolean())
				multiplier *= -1;
			dir.setJ(Math.sqrt(Math.pow(GameController.DEFAULT_CURVE_SPEED, 2) - Math.pow(dir.getI(), 2)) * multiplier);
			curves[i] = new Curve(createCoordinate_X(), createCoordinate_Y(), GameController.DEFAULT_THICK,
					GameController.DEFAULT_CURVE_ANGLE, colors.get(i), dir);
			curveControllers[i] = new CurveController(curves[i]);
		}

		setBorder(GameController.PLAYGROUND_BORDER_FACTORY);
		this.playgroundLoading = true;
	}

	/*
	 * private void resizeImage() { BufferedImage tmp = new
	 * BufferedImage(this.getWidth(),this.getHeight(),BufferedImage.
	 * TYPE_INT_ARGB); tmp.getGraphics().drawImage(img, 0, 0, null); img = tmp;
	 * gr = img.getGraphics(); }
	 */

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

				this.defaultLayerColor = GameController.PLAYGROUND_BACKGROUND.getRGB();

				this.powerUps = new ArrayList<>();
				this.powerUpLoader = new PowerUpLoader(this.backgroundLayer, this.powerUps);
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
			g.drawImage(this.backgroundLayer.getImg(), 0, 0, null);

			for (int i = 0; i < players; ++i) {

				int x = (int) curves[i].getX();
				int y = (int) curves[i].getY();
				int r = curves[i].getRadius();
				int padding = r + GameController.PLAYGROUND_BORDER_WIDTH;

				if (outOfBorderBounds(curves[i], i, x, y, padding)) {
					g.drawImage(curvesLayer.getImg(), 0, 0, null);
					managePlayerDeath(i);
					return;
				}

				if (crashedToSomething(curves[i], i)) {
					g.drawImage(curvesLayer.getImg(), 0, 0, null);
					managePlayerDeath(i);
					return;
				}

				if (curves[i].isPaused()) {
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
					g.setColor(curves[i].getColor());
					g.fillOval(x - r, y - r, 2 * r, 2 * r);

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
					
					curvesLayer.getGr().setColor(curves[i].getColor());
					curvesLayer.getGr().fillOval(x - r, y - r, 2 * r, 2 * r);
					
					
					/**
					 * COLLISION PREPARATION
					 *  
					 * ******** ******** ******** ********
					 *    | -> circle count, 28 bits          
					 */	
					int c = curves[i].getCircleNumber();
					int col = (i << 16) + c;
					//System.out.println(col);
					this.timeLayer.getGr().setColor(new Color(col));
					this.timeLayer.getGr().fillOval(x - r, y - r, 2 * r, 2 * r);
					curves[i].setCircleNumber(c + 1);
					
				}
				// g.drawImage(curves[i].getCurveLayer().getImg(), 0, 0, null);
				// compressedLayer.getGr().drawImage(curves[i].getCurveLayer().getImg(),
				// 0, 0, null);
				checkForPowerUp(curves[i], i);
			}

			g.drawImage(curvesLayer.getImg(), 0, 0, null);
			this.compressedLayer.getGr().drawImage(curvesLayer.getImg(), 0, 0, null);
			// compressedLayer.getGr().drawImage(this.curvesLayer.getImg(), 0,
			// 0, null);
			// compressedLayer.getGr().drawImage(this.extrasLayer.getImg(), 0,
			// 0, null);

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

		g.drawImage(this.backgroundLayer.getImg(), 0, 0, null);
		System.out.println("initPaint");
		for (int i = 0; i < players; ++i) {
			int r = curves[i].getRadius();
			int color = curves[i].getColor().getRGB();

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

			String text = (this.names.get(i).length() > 0) ? Character.toString(this.names.get(i).charAt(0)) : "?";
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
			double rotationRequired = Math.acos(curves[i].getDirection().getI() / curves[i].calcSpeed())
					* Math.signum(curves[i].getDirection().getJ()) + Math.PI / 2;
			AffineTransform tx = new AffineTransform();
			double scale = 50.0 / direction.getWidth();
			tx.translate(curves[i].getX() - direction.getWidth() * scale / 2,
					curves[i].getY() - direction.getHeight() * scale);
			tx.rotate(rotationRequired, direction.getWidth() * scale / 2, direction.getHeight() * scale);
			tx.scale(scale, scale);

			g2d.drawImage(finalImg, tx, null);
			this.curvesLayer.getGr().setColor(curves[i].getColor());
			this.curvesLayer.getGr().fillOval((int) curves[i].getX() - r, (int) curves[i].getY() - r, 2 * r, 2 * r);

		}

		g.drawImage(this.curvesLayer.getImg(), 0, 0, null);

	}

	public void eraseArrows() {
		this.playgroundLoading = false;		
	}

	/***************************************************************************************************************************
	 * 
	 * DEATH + next ROUND
	 * 
	 **************************************************************************************************************************/
	private void managePlayerDeath(int player) {

		// System.out.println("DEATH DEATH DEATH DEATHDEATH DEATH");
		// System.out.println(System.nanoTime());

		if (this.playersDead.contains(new Integer(player))) {
			// System.out.println("ezert");
			return;
		}

		this.playersDead.add(new Integer(player));

		this.curveControllers[player].stop();
		this.playersStillAlive.remove(new Integer(player));
		for (Integer i : this.playersStillAlive) {
			this.curveWindow.getPlayerStatusPanes().get((int) i).increaseScore();
		}

		if (this.playersStillAlive.size() <= 1) {
			int alive = (this.playersStillAlive.size() == 1) ? (int) this.playersStillAlive.get(0) : 0;
			System.out.println("VEGEEEEE");
			this.curveWindow.getDisplayRefresher().stopRefresher();
			GameController.finished = true;
			this.curveControllers[alive].stop();

			String winner = this.names.get(alive);
			if (winner.isEmpty())
				winner = "#noName";

			if (this.round == GameController.ROUND_COUNT) {
				/**
				 * end game statistics window
				 */
			} else {
				CountDownModal endRound = new CountDownModal(this.curveWindow, ++round, winner, this.colors.get(alive));
				this.playersStillAlive.clear();

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

		Direction dir = new Direction();
		byte multiplier = 1;

		for (int i = 0; i < players; ++i) {
			if (rnd.nextBoolean())
				multiplier *= -1;
			dir.setI(rnd.nextDouble() * GameController.DEFAULT_CURVE_SPEED * multiplier);

			if (rnd.nextBoolean())
				multiplier *= -1;
			dir.setJ(Math.sqrt(Math.pow(GameController.DEFAULT_CURVE_SPEED, 2) - Math.pow(dir.getI(), 2)) * multiplier);

			curves[i].initData(createCoordinate_X(), createCoordinate_Y(), GameController.DEFAULT_THICK,
					GameController.DEFAULT_CURVE_ANGLE, colors.get(i), dir);

		}

		this.powerUps.clear();
		this.powerUpLoader.finishAllTasks();
	}

	/***************************************************************************************************************************************************************
	 * 
	 * COLLISION and BORDER
	 * 
	 ****************************************************************************************************************************************************************/

	private boolean outOfBorderBounds(Curve curve, int index, int x, int y, int padding) {
		
		if (x <= padding) {
			if (this.noBorder > 0)
				curve.setX(this.backgroundLayer.getImg().getWidth() - padding - 1);
			else
				return (curve.isPaused() || this.playersDead.contains(new Integer(index))) ? false : true;
		} else if (x >= this.backgroundLayer.getImg().getWidth() - padding - 1) {
			if (this.noBorder > 0)
				curve.setX(padding + 1);
			else
				return (curve.isPaused() || this.playersDead.contains(new Integer(index))) ? false : true;
		}

		if (y <= padding) {
			if (this.noBorder > 0)
				curve.setY(this.backgroundLayer.getImg().getHeight() - padding - 1);
			else
				return (curve.isPaused() || this.playersDead.contains(new Integer(index))) ? false : true;
		} else if (y >= this.backgroundLayer.getImg().getHeight() - padding - 1) {
			if (this.noBorder > 0)
				curve.setY(padding + 1);
			else
				return (curve.isPaused() || this.playersDead.contains(new Integer(index))) ? false : true;
		}

		return false;

		// return (x < padding || y < padding || x >
		// this.backgroundLayer.getImg().getWidth() - padding || y >
		// this.backgroundLayer.getImg().getHeight() - padding);
	}

	private boolean crashedToSomething(Curve curve, int index) {

		if (curve.isPaused() || this.playersDead.contains(new Integer(index))) {
			return false;
		}

		int r = curve.getRadius();
		double i = curve.getDirection().getI();
		double j = curve.getDirection().getJ();
		double k = r / Math.hypot(curve.getX() - curve.getOldX(), curve.getY() - curve.getOldY());

		Point2D.Double center = new Point2D.Double(curve.getX(), curve.getY());
		Point2D.Double startPoint = new Point2D.Double(center.getX() + (curve.getX() - curve.getOldX()) * k,
				center.getY() + (curve.getY() - curve.getOldY()) * k);
		// System.out.println("\tChecking: " + startPoint.getX() + " ; " +
		// startPoint.getY());
		if (!pointIsOk(startPoint, curve, index)) {
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

	private boolean pointIsOk(Point2D.Double point, Curve curve, int index) {
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
		for (int i = 0; i < players; ++i) {
			if (i == index)
				continue;
			if (paintedColor == curves[i].getColor().getRGB()) {
				// death because of i. player. Congrats i. EVIL :))				
				this.curveWindow.getPlayerStatusPanes().get((int) i).increaseScore();
				return false;
			}
		}
		// cannot check default color, layer is uninitialized, alpha channel,
		// etc
		if (paintedColor == curve.getColor().getRGB()) {
			
			int col = this.timeLayer.getImg().getRGB((int) point.getX(), (int) point.getY());			
			int playerIndex = (col >> 16) & 0xF;			
			int circleCount = col & 0x0000FFFF;
			System.out.println(col + " PLAYER:" + playerIndex + " CIRCLE: " + circleCount);
			
			if (curve.getCircleNumber() - circleCount <= 3)
				return true;
			
			return false;			
			/*
			// TRIAL WITH NANO-TIME CHECKING BETWEEN LAST COLLISIONS - not bad, but not good
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

	private void checkForPowerUp(Curve curve, int index) {
		for (ListIterator<PowerUp> iter = this.powerUps.listIterator(); iter.hasNext();) {
			PowerUp p = iter.next();
			double x = curve.getX();
			double y = curve.getY();
			int r = curve.getRadius();
			double dist = point_distance(curve.getX(), curve.getY(), p.getX(), p.getY());
			if (dist <= PowerUp.POWERUP_RADIUS) {
				switch (p.getName()) {
				case "more_extra.png":
					this.powerUpLoader.action_moreExtra();
					break;
				case "no_border.png":
					this.powerUpLoader.action_noBorder(this);
					break;
				case "erase.png":
					this.powerUpLoader.action_erase(this);
					return;
				case "own_fly.png":
					this.powerUpLoader.action_ownFly(this, curve, index);
					break;
				case "own_slow.png":
					this.powerUpLoader.action_ownSlow(this, curve, index);
					break;
				case "own_speed.png":
					this.powerUpLoader.action_ownSpeed(this, curve, index);
					break;
				case "other_slow.png":
					this.powerUpLoader.action_otherSlow(this, index);
					break;
				case "other_speed.png":
					this.powerUpLoader.action_otherSpeed(this, index);
					break;
				case "other_swap_control.png":
					this.powerUpLoader.action_otherSwapControl(this, index);
					break;
				case "other_thick.png":
					this.powerUpLoader.action_otherThick(this, index);
					break;
				}

				this.backgroundLayer.getGr().setColor(GameController.PLAYGROUND_BACKGROUND);
				this.backgroundLayer.getGr().fillRect(0, 0, this.backgroundLayer.getImg().getWidth(),
						this.backgroundLayer.getImg().getHeight());

				try {
					iter.remove();
				} catch (java.util.ConcurrentModificationException e) {
				}

				for (ListIterator<PowerUp> iter2 = this.powerUps.listIterator(); iter2.hasNext();) {
					PowerUp p2 = iter2.next();
					this.powerUpLoader.drawPowerUpIcon(p2);
				}
				/*
				 * this.backgroundLayer.getGr().fillOval( p.getX() -
				 * PowerUp.POWERUP_RADIUS - 1, p.getY() - PowerUp.POWERUP_RADIUS
				 * - 1, 2 * PowerUp.POWERUP_RADIUS + 1, 2 *
				 * PowerUp.POWERUP_RADIUS + 1);
				 */

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
		for (int i = 0; i < players; ++i) {
			curveControllers[i].start();
		}
	}

	public void restartGame() {
		for (int i = 0; i < players; ++i)
			playersStillAlive.add(new Integer(i));

		this.playersDead.clear();

		for (int i = 0; i < players; ++i) {
			curveControllers[i].restart();
		}
	}

	public void leftTurnTriggered(int curve) {
		curves[curve].setLeftPressed(true);
	}

	public void rightTurnTriggered(int curve) {
		curves[curve].setRightPressed(true);
	}

	public void leftTurnStopped(int curve) {
		curves[curve].setLeftPressed(false);
	}

	public void rightTurnStopped(int curve) {
		curves[curve].setRightPressed(false);
	}

	public int randBetween(int a, int b) {
		return this.rnd.nextInt(b - a) + a;
	}

	private double createCoordinate_X() {
		int marginFromCountDown = 20;
		double x = randBetween(PADDING, GameController.FRAME_SIZE_X - PADDING);
		if (x >= (GameController.FRAME_SIZE_X - GameController.COUNT_DOWN_WIDTH) / 2 - marginFromCountDown
				&& x <= (GameController.FRAME_SIZE_X + GameController.COUNT_DOWN_WIDTH) / 2 + marginFromCountDown) {
			x += GameController.COUNT_DOWN_WIDTH;
		}
		return x;
	}

	private double createCoordinate_Y() {
		double y = randBetween(PADDING, GameController.FRAME_SIZE_Y - PADDING);
		int marginFromCountDown = 20;
		if (y >= (GameController.FRAME_SIZE_Y - GameController.COUNT_DOWN_HEIGHT) / 2 - marginFromCountDown
				&& y <= (GameController.FRAME_SIZE_Y + GameController.COUNT_DOWN_HEIGHT) / 2 + marginFromCountDown) {
			y += GameController.COUNT_DOWN_HEIGHT;
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

	public List<PowerUp> getPowerUps() {
		return powerUps;
	}

	public void incNoBorder() {
		++this.noBorder;
	}
	
	public void decNoBorder() {
		--this.noBorder;
	}
	
	public int getNoBorder() {
		return this.noBorder;
	}
	

	public CurveWindow getCurveWindow() {
		return curveWindow;
	}

	public int getPlayers() {
		return players;
	}

	public Curve[] getCurves() {
		return curves;
	}
}