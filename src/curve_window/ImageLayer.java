package curve_window;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class ImageLayer {

	private BufferedImage img;
	private Graphics gr;
	
	public ImageLayer(int width, int height, Color defaultColor, int alphaMode) {
		this.img = new BufferedImage(width, height, alphaMode);		
		this.gr = img.getGraphics();
		//((Graphics2D)this.gr).setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);
		if (defaultColor != null) {
			this.gr.setColor(defaultColor);
			this.gr.fillRect(0, 0, width, height);
		}
	}
	
	public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
	}

	public Graphics getGr() {
		return gr;
	}
	
	public void setGr(Graphics gr) {
		this.gr = gr;
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * newm kellll
	 */
	
	public Graphics2D getGr2D() {
		return (Graphics2D) gr;
	}

	
	public void grSetColor(Color color) {
		gr.setColor(color);
	}
	
	public void grFillOval(int x, int y, int w, int h) {
		gr.fillOval(x,  y,  w,  h);
	}
	
	public void grFillRect(int x, int y, int w, int h) {
		gr.fillRect(x,  y,  w,  h);
	}
	
	public void gr2DDrawImage(Image img, AffineTransform xform, ImageObserver obs){
		((Graphics2D)gr).drawImage(img, xform, obs);
	}

	
}
