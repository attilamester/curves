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

	private Random rnd = new Random();
	private volatile BaseImageLayer playgroundImg;
	/*
	 * Explicite playground
	 */
	public int players;
	public Curve[] curves;
	public CurveController[] curveControllers;
	private List<Control> controls;
	
	
	
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
		if (this.playgroundImg == null) {
			this.playgroundImg = new BaseImageLayer(this.getWidth(), this.getHeight(), GameController.PLAYGROUND_BACKGROUND);
			for (int i = 0; i < players; ++i)
				curveControllers[i].setBaseImg(this.playgroundImg);
			this.initPaint(g);
		} else {
			
			g.drawImage(this.playgroundImg.getImg(), 0, 0, null);

		}
	}
	
	/***********************************************************
	 * 
	 * INIT DIRECTION PAINTING
	 * 
	 ***********************************************************/
	
	public void initPaint(Graphics g) {
		g.drawImage(this.playgroundImg.getImg(), 0, 0, null);
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
			((Graphics2D)this.playgroundImg.getGr()).drawImage(finalImg, tx, null);
			this.playgroundImg.getGr().setColor(curves[i].getColor());
			this.playgroundImg.getGr().fillOval((int)curves[i].getX() - r, (int)curves[i].getY() - r, 2 * r, 2 * r	);
			
		}
		
		g.drawImage(this.playgroundImg.getImg(), 0, 0, null);
		
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
		this.playgroundImg.setGr(this.playgroundImg.getGr());
		this.playgroundImg.getGr().setColor(GameController.PLAYGROUND_BACKGROUND);
		this.playgroundImg.getGr().fillRect(0, 0, this.getWidth(), this.getHeight());
		this.getGraphics().drawImage(this.playgroundImg.getImg(), 0, 0, null);
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



// No need for resize 
/*addComponentListener(new ComponentAdapter() {
	public void componentResized(ComponentEvent e) {
		resizeImage();
    }
});*/