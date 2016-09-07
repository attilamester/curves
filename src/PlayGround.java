import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class PlayGround extends JPanel  {

	private static final long serialVersionUID = 1L;

	private Random rnd = new Random();
	
	private CurveWindow curveWindow; // JUST for countDown needed
	
	private ImageLayer backgroundLayer;
	private ImageLayer curvesLayer;
	private ImageLayer extrasLayer;
	
	private ImageLayer compressedLayer;
	
	private int defaultLayerColor;	
	
	/*
	 * Explicite playground
	 */
	private int players;
	private List<String> names;
	private Curve[] curves;
	private CurveController[] curveControllers;
	private List<Control> controls;
	
	private List<String> playersStillAlive;
	private int round;
	
	
	public PlayGround(CurveWindow curveWindow, int players, List<String> names, List<Control> controls, List<Color> colors) {
		
		this.curveWindow = curveWindow;
		
		this.players = players;
		this.controls = controls;
		this.names = new ArrayList<String>(names);
		this.playersStillAlive = new ArrayList<String>(names);
		this.round = 1;
		
		curves = new Curve[players];
		curveControllers = new CurveController[players];

		
		/*
		Color[] colors = new Color[players];
		for (int i = 0; i < players; ++i)
			colors[i] = new Color(rnd.nextInt(200) + 50, rnd.nextInt(200) + 50, rnd.nextInt(200) + 50);
			*/
		int padding = GameController.FRAME_SIZE_X / 5;
		
		Direction dir = new Direction();
		byte multiplier = 1;
		
		for (int i = 0; i < players; ++i) {
			if(rnd.nextBoolean())
				multiplier *= -1;
			dir.setI(rnd.nextDouble() * GameController.DEFAULT_CURVE_SPEED * multiplier);
			
			if(rnd.nextBoolean())
				multiplier *= -1;
			dir.setJ( Math.sqrt( 
				Math.pow(GameController.DEFAULT_CURVE_SPEED, 2) - 
				Math.pow(dir.getI(), 2)
			) * multiplier);
			curves[i] = new Curve( randBetween(padding, GameController.FRAME_SIZE_X - padding), randBetween(padding, GameController.FRAME_SIZE_Y - padding), GameController.DEFAULT_THICK, GameController.DEFAULT_CURVE_ANGLE, colors.get(i), dir);			
			curveControllers[i] = new CurveController(curves[i]);
		}
				
		setBorder(BorderFactory.createLineBorder(Color.WHITE, GameController.PLAYGROUND_BORDER_WIDTH));
	}
		
	/*
	private void resizeImage() {
		BufferedImage tmp = new BufferedImage(this.getWidth(),this.getHeight(),BufferedImage.TYPE_INT_ARGB);
		tmp.getGraphics().drawImage(img, 0, 0, null);
		img = tmp;
		gr = img.getGraphics();
	}*/
	
	/***********************************************************
	 * 
	 * PAINT PAINT PAINT PAINT PAINT PAINT PAINT PAINT
	 * 
	 ***********************************************************/
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.compressedLayer == null) {
			this.compressedLayer = new ImageLayer(this.getWidth(), this.getHeight(), GameController.PLAYGROUND_BACKGROUND);
			this.backgroundLayer = new ImageLayer(this.getWidth(), this.getHeight(), GameController.PLAYGROUND_BACKGROUND);
			this.curvesLayer = new ImageLayer(this.getWidth(), this.getHeight(), null);
			this.extrasLayer = new ImageLayer(this.getWidth(), this.getHeight(), null);
			
			this.defaultLayerColor = GameController.PLAYGROUND_BACKGROUND.getRGB();  
					
			this.initPaint(g);
		} else {
			
			g.drawImage(this.backgroundLayer.getImg(), 0, 0, null);

			for (int i = 0; i < players; ++i) {
				
				int x = (int)curves[i].getX();
				int y = (int)curves[i].getY();
				int r = curves[i].getRadius();
				int padding = r + GameController.PLAYGROUND_BORDER_WIDTH;
				
				if (outOfBorderBounds(x, y, padding)) {
					g.drawImage(curvesLayer.getImg(), 0, 0, null);					
					managePlayerDeath(i);
					return;
				}
				if (crashedToSomething(curves[i])) {
					g.drawImage(curvesLayer.getImg(), 0, 0, null);					
					managePlayerDeath(i);
					return;
				}
				
				if (curves[i].isPaused() &&
						Math.hypot(x - curves[i].getPausedX(), y - curves[i].getPausedY()) >=  r
					) {
					/**
					 * DELETE PREVIOUS CIRCLE
					 */
					//curves[i].getCurveLayer().getGr().setColor(GameController.PLAYGROUND_BACKGROUND);
					//curves[i].getCurveLayer().getGr().fillOval((int)curves[i].getOldX() - r, (int)curves[i].getOldY() - r, 2 * r, 2 * r);
					/////OLD WAY
					
					/**
					 * New way: paint only to refreshed dash-layer
					 */
					//curves[i].resetDashLayer();
					//curves[i].getDashLayer().getGr().setColor(Color.RED);
					//curves[i].getDashLayer().getGr().fillOval(x - r, y - r, 2 * r, 2 * r);
					
					/**
					 * Actually working version :D
					 */
					g.setColor(curves[i].getColor());
					g.fillOval(x - r, y - r, 2 * r, 2 * r);
					
				} else {
					//this.getGr().setColor(curve.getColor());
					//this.getGr().fillOval(x - r, y - r, 2 * r, 2 * r);
					/**
					 * GOOD but maybe no need for curvelayer. JUST compressed layer
					 */
					//curves[i].getCurveLayer().getGr().setColor(curves[i].getColor());
					//curves[i].getCurveLayer().getGr().fillOval(x - r, y - r, 2 * r, 2 * r);

					//this.curvesLayer.getGr().drawImage(curves[i].getCurveLayer().getImg(), 0, 0, null);
					
					curvesLayer.getGr().setColor(curves[i].getColor());
					curvesLayer.getGr().fillOval(x - r, y - r, 2 * r, 2 * r);
				}
				
				//g.drawImage(curves[i].getCurveLayer().getImg(), 0, 0, null);
				//compressedLayer.getGr().drawImage(curves[i].getCurveLayer().getImg(), 0, 0, null);
				
			}
			
			
			g.drawImage(curvesLayer.getImg(), 0, 0, null);
			this.compressedLayer.getGr().drawImage(curvesLayer.getImg(), 0, 0, null);
			//compressedLayer.getGr().drawImage(this.curvesLayer.getImg(), 0, 0, null);
			//compressedLayer.getGr().drawImage(this.extrasLayer.getImg(), 0, 0, null);
			
			
		}
	}
	
	/***********************************************************
	 * 
	 * INIT DIRECTION PAINTING
	 * 
	 ***********************************************************/
	
	public void initPaint(Graphics g) {
		g.drawImage(this.backgroundLayer.getImg(), 0, 0, null);
		System.out.println("initPaint");
		for (int i = 0; i < players; ++i) {
			int r = curves[i].getRadius();
			int color = curves[i].getColor().getRGB(); 
			BufferedImage direction =  null;
			BufferedImage finalImg = null;
			try {
				direction = ImageIO.read(new File("images\\direction.png"));				
			} catch (IOException e) {}
			
			ImageFilter filter =  new RGBImageFilter() {
				@Override
				public int filterRGB(final int x, final int y, final int rgb) {
					return (rgb >> 24 == 0x00) ? 0 : color;
				}
			};
			ImageProducer ip = new FilteredImageSource(direction.getSource(),filter);
			finalImg = toBufferedImage(Toolkit.getDefaultToolkit().createImage(ip));
			/** 
			 *  ___________>   oX  v0
			 *  \alpha
			 *   \
			 *    \
			 *    _\|  v1
			 *     
			 * 
			 * v1 * v0 = |v1| * |v0| * cos alpha
			 * 
			 * =>
			 * 
			 * alpha = arccos(v1 * v0 / || ||)
			 */
			double rotationRequired = Math.acos(
					curves[i].getDirection().getI() /
					curves[i].calcSpeed()
			) * Math.signum(curves[i].getDirection().getJ()) + Math.PI / 2;
			AffineTransform tx = new AffineTransform();
			double scale = 50.0 / direction.getWidth();		
			tx.translate(curves[i].getX()  - direction.getWidth() * scale / 2, curves[i].getY() - direction.getHeight() * scale);
			tx.rotate(rotationRequired, direction.getWidth() * scale / 2, direction.getHeight() * scale);
			tx.scale(scale, scale);
			
			
			/*
			this.playgroundImg.gr2DDrawImage(finalImg, tx, null);
			this.playgroundImg.grSetColor(curves[i].getColor());
			this.playgroundImg.grFillOval((int)curves[i].getX() - r, (int)curves[i].getY() - r, 2 * r, 2 * r);
			*/
			((Graphics2D)this.backgroundLayer.getGr()).drawImage(finalImg, tx, null);
			this.backgroundLayer.getGr().setColor(curves[i].getColor());
			this.backgroundLayer.getGr().fillOval((int)curves[i].getX() - r, (int)curves[i].getY() - r, 2 * r, 2 * r	);
			
		}
		
		g.drawImage(this.backgroundLayer.getImg(), 0, 0, null);
		
	}
	
	private void managePlayerDeath(int player) {
		
		this.curveControllers[player].stop();

		this.playersStillAlive.remove(this.names.get(player));
		
		if (this.playersStillAlive.size() == 1) {
			this.curveWindow.getDisplayRefresher().stopRefresher();
			String winner = this.playersStillAlive.get(0);
			if (winner.isEmpty())
				winner = "Player " + Integer.toString(player);
			CountDownModal endRound = new CountDownModal(this.curveWindow, ++round, winner);
			GameController.finished = true;
		}
	}
	
	/***************************************************************************************************************************************************************
	 * 
	 * COLLISION and BORDER
	 * 
	 ****************************************************************************************************************************************************************/
	
	private boolean outOfBorderBounds(int x, int y, int padding) {
		return (x < padding  || y < padding || x > this.backgroundLayer.getImg().getWidth() - padding  || y > this.backgroundLayer.getImg().getHeight() - padding); 
	}
	
	private boolean crashedToSomething(Curve curve) {
		//System.out.println("Checking for: " + curves[z].getX() + " ; " + curves[z].getY());
		/*System.out.println(
		 "\nCenter of old circle:" + curves[z].getOldX() + " " + curves[z].getOldY()
		+ "\nCenter of new circle:" + curves[z].getX() + " " + curves[z].getY()
			);
*/
		if (curve.isPaused())
			return false;
		//System.out.println(curve.getDirection());
		int r = curve.getRadius();
		double i = curve.getDirection().getI();
		double j = curve.getDirection().getJ();
		double k = r / Math.hypot(
			curve.getX() - curve.getOldX(), 
			curve.getY() - curve.getOldY() 
		);
		
		Point2D.Double center = new Point2D.Double(curve.getX(), curve.getY());
		Point2D.Double startPoint = new Point2D.Double(
			center.getX() + (curve.getX() - curve.getOldX()) * k,
			center.getY() + (curve.getY() - curve.getOldY()) * k
		);
		//System.out.println("\tChecking: " + startPoint.getX() + " ; " + startPoint.getY());
		if (!pointIsOk(startPoint, curve)) {
			return true;
		}
		
		return false;
		
		/*
		
		int alpha = 30;
		int limit = 30;
		int nr = limit / alpha;
		AffineTransform rot = AffineTransform.getRotateInstance(Math.toRadians(alpha), curves[z].getX(), curves[z].getY());
		Point2D.Double nextPoint = new Point2D.Double(startPoint.getX(), startPoint.getY());
		
		//////
		   //
		  // Clockwise direction check
		 //
		//////
			
		for (int ii = 0; ii < nr; ++ii) {
			rot.transform(nextPoint, nextPoint);
			System.out.println("\tChecking: " + nextPoint.getX() + " ; " + nextPoint.getY());
			if (!pointIsOk(nextPoint, curves[z])) {
				return true;
			}
		}
		rot.setToRotation(-alpha, curves[z].getX(), curves[z].getY());
		nextPoint.setLocation(startPoint);
		
		//////
		   // 
		  // Counter - Clockwise direction check
		 //
		//////

		for (int ii = 0; ii < nr; ++ii) {
			rot.transform(nextPoint, nextPoint);
			System.out.println("\tChecking: " + nextPoint.getX() + " ; " + nextPoint.getY());
			if (!pointIsOk(nextPoint, curves[z])) {
				return true;
			}
		}
			
		return false;
		
		*/	
	}
	
	private boolean pointIsOk(Point2D.Double point, Curve curve) {
		/**
		 * red   = (paintedColor & 0x00ff0000) >> 16;
		 * green = (paintedColor & 0x0000ff00) >> 8;
		 * blue  = (paintedColor & 0x000000ff);
		 */
		
		/**
		 * INSIDE PREVIOUS PAINTED CIRCLE => IGNORE IT
		 */
		if( Math.hypot(point.getX() - curve.getOldX(), point.getY() - curve.getOldY()) <= 
			curve.getRadius()
				) {
			/*System.out.println("INSIDE OLD CIRCLE:\n\t" + "Point:" + point.getX() + " " + point.getY()
				+ "\n\tCenter of old circle:" + curve.getOldX() + " " + curve.getOldY()
				+ "\n\tCenter of new circle:" + curve.getX() + " " + curve.getY()
				+ "\n\tR:" + curve.getRadius()
					); */
			return true;
		}
		
		int paintedColor;
		try {
			paintedColor = this.compressedLayer.getImg().getRGB((int)point.getX(), (int)point.getY());			
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("out of bounds");
			return false;
		}		
		/*for (int i = 0; i < players; ++i)
			if (curves[i].getColor().getRGB() == paintedColor) {
				System.out.println("already colored here" + (i+1));
				return false;
			}*/
		// canot check deault color, layer is uninitialized, alpha channel, etc
		 if (paintedColor != this.defaultLayerColor) {			
			System.out.println("already colored here");
			return false;
		}
		System.out.println("OUT OF CIRCLE OK");
		return true;
	}
	
	public static BufferedImage toBufferedImage(Image image) { 
		if (image instanceof BufferedImage) return (BufferedImage) image;

		image = new ImageIcon(image).getImage(); 
		BufferedImage bimage = new BufferedImage(image.getWidth(null),image.getHeight(null), BufferedImage.TYPE_INT_ARGB); 
		Graphics g = bimage.createGraphics(); 
		g.drawImage(image,0,0,null); 
		g.dispose(); 
		return bimage; 
	}
	
	public void eraseArrows() {
		this.backgroundLayer.getGr().setColor(GameController.PLAYGROUND_BACKGROUND);
		this.backgroundLayer.getGr().fillRect(0, 0, this.getWidth(), this.getHeight());
		this.getGraphics().drawImage(this.backgroundLayer.getImg(), 0, 0, null);
	}
	
	/*
	public Graphics getGr() {
		return this.playgroundImg.getGr();
	}*/
	public void startGame() {
		for(int i = 0; i < players; ++i) {
			curveControllers[i].start();
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
	
	
	
}