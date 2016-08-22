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
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class PlayGround extends JPanel  {

	private static final long serialVersionUID = 1L;
	
	private BufferedImage img;
	private Graphics gr;
	
	private byte[] pixels;
	/*
	 * Explicite playground
	 */
	public int players;
	public Curve[] curves;
	public CurveController[] curveControllers;
	private List<Control> controls;
	
	private Random rnd = new Random();
	
	public PlayGround(int players, List<Control> controls, List<Color> colors) {
		
		this.players = players;
		this.controls = controls;

		curves = new Curve[players];
		curveControllers = new CurveController[players];
		
		/*
		Color[] colors = new Color[players];
		for (int i = 0; i < players; ++i)
			colors[i] = new Color(rnd.nextInt(200) + 50, rnd.nextInt(200) + 50, rnd.nextInt(200) + 50);
			*/
		int padding = Main.screenSize.width / 5;
		
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
			curves[i] = new Curve( randBetween(padding, Main.screenSize.width - padding), randBetween(padding, Main.screenSize.height - padding), GameController.DEFAULT_THICK, GameController.DEFAULT_CURVE_ANGLE, colors.get(i), dir);			
			curveControllers[i] = new CurveController(curves[i]);
		}
		
		setBorder(BorderFactory.createLineBorder(Color.WHITE, GameController.PLAYGROUND_BORDER_WIDTH));
	}
	
	public void setCurves(Curve[] curves) {
		//this.curves = curves;
	}
	
	private void resizeImage() {
		BufferedImage tmp = new BufferedImage(this.getWidth(),this.getHeight(),BufferedImage.TYPE_INT_ARGB);
		tmp.getGraphics().drawImage(img, 0, 0, null);
		img = tmp;
		gr = img.getGraphics();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (img == null) {
			this.initPaint(g);			
		} else {
			
			for (int i = 0; i < players; ++i ) {
				
				int x = (int)curves[i].getX();
				int y = (int)curves[i].getY();
				int r = curves[i].getRadius();
				int padding = r + GameController.PLAYGROUND_BORDER_WIDTH;
				
				if (outOfBorderBounds(x, y, padding)) {					
					System.out.println("Player " + (i+1) + " out of borders");
					GameController.finished = true;
					g.drawImage(img, 0, 0, null);
					return;
				}
				if (crashedToSomething(curves, i)) {
					System.out.println("Player " + (i+1) + " crashed");
					GameController.finished = true;
					g.drawImage(img, 0, 0, null);
					return;
				}
								
				if (curves[i].isPaused() && 
						Math.hypot(x - curves[i].getPausedX(), y - curves[i].getPausedY()) >=  r
					) {
					gr.setColor(GameController.PLAYGROUND_BACKGROUND);
					gr.fillOval((int)curves[i].getOldX() - r, (int)curves[i].getOldY() - r, 2 * r, 2 * r);
				}
				
				gr.setColor(curves[i].getColor());
				gr.fillOval((int)curves[i].getX() - r, (int)curves[i].getY() - r, 2 * r, 2 * r);
				
			}
		
			g.drawImage(img, 0, 0, null);
		}
		
	}
	
	public void initPaint(Graphics g) {
		this.img = new BufferedImage(this.getWidth(),this.getHeight(),BufferedImage.TYPE_INT_ARGB);
		//pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		gr = img.getGraphics();
		gr.setColor(GameController.PLAYGROUND_BACKGROUND);
		gr.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.drawImage(img, 0, 0, null);
		
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
			double scale = 75.0 / direction.getWidth();		
			tx.translate(curves[i].getX()  - direction.getWidth() * scale / 2, curves[i].getY() - direction.getHeight() * scale);
			tx.rotate(rotationRequired, direction.getWidth() * scale / 2, direction.getHeight() * scale);
			tx.scale(scale, scale);
			
			Graphics2D g2d = (Graphics2D) gr;
			g2d.drawImage(finalImg, tx, null);
			
			gr.setColor(curves[i].getColor());
			gr.fillOval((int)curves[i].getX() - r, (int)curves[i].getY() - r, 2 * r, 2 * r);
			
		}
		
		g.drawImage(img, 0, 0, null);
		
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
		gr = img.getGraphics();
		gr.setColor(GameController.PLAYGROUND_BACKGROUND);
		gr.fillRect(0, 0, this.getWidth(), this.getHeight());
		this.getGraphics().drawImage(img, 0, 0, null);
	}
	
	
	public Graphics getGr() {
		return gr;
	}
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
	/**************************************************************
	 * 
	 * COLLISION and BORDER
	 * 
	 **************************************************************/
	private boolean outOfBorderBounds(int x, int y, int padding) {
		return (x < padding  || y < padding || x > this.getWidth() - padding  || y > this.getHeight() - padding); 
	}
	
	private boolean crashedToSomething(Curve[] curves, int z) {
		//System.out.println("Checking for: " + curves[z].getX() + " ; " + curves[z].getY());
		/*System.out.println(
		 "\nCenter of old circle:" + curves[z].getOldX() + " " + curves[z].getOldY()
		+ "\nCenter of new circle:" + curves[z].getX() + " " + curves[z].getY()
			);
*/
		System.out.println(curves[z].getDirection());
		int r = curves[z].getRadius();
		double i = curves[z].getDirection().getI();
		double j = curves[z].getDirection().getJ();
		double k = r / Math.hypot(
			curves[z].getX() - curves[z].getOldX(), 
			curves[z].getY() - curves[z].getOldY() 
		);
		
		Point2D.Double center = new Point2D.Double(curves[z].getX(), curves[z].getY());
		Point2D.Double startPoint = new Point2D.Double(
			center.getX() + (curves[z].getX() - curves[z].getOldX()) * k,
			center.getY() + (curves[z].getY() - curves[z].getOldY()) * k
		);
		System.out.println("\tChecking: " + startPoint.getX() + " ; " + startPoint.getY());
		if (!pointIsOk(startPoint, curves[z])) {
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
			System.out.println("INSIDE OLD CIRCLE:\n\t" + "Point:" + point.getX() + " " + point.getY()
				+ "\n\tCenter of old circle:" + curve.getOldX() + " " + curve.getOldY()
				+ "\n\tCenter of new circle:" + curve.getX() + " " + curve.getY()
				+ "\n\tR:" + curve.getRadius()
					); 
			return true;
		}	
		
		int paintedColor;
		try {
			paintedColor = img.getRGB((int)point.getX(), (int)point.getY());			
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("out of bounds");
			return false;
		}		
		if (paintedColor != GameController.PLAYGROUND_BACKGROUND.getRGB()) {			
			System.out.println("already colored here");
			return false;
		}
		System.out.println("OUT OF CIRCLE OK");
		return true;
	}
	
}



// No need for resize 
/*addComponentListener(new ComponentAdapter() {
	public void componentResized(ComponentEvent e) {
		resizeImage();
    }
});*/