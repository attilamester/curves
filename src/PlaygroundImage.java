import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class PlaygroundImage {

	private BufferedImage img;
	private Graphics gr;
	
	public PlaygroundImage(int width, int height) {
		this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);		
		this.gr = img.getGraphics();		
		this.gr.setColor(GameController.PLAYGROUND_BACKGROUND);
		this.gr.fillRect(0, 0, width, height);
	}

	public Graphics getGr() {
		return gr;
	}
	
	public Graphics2D getGr2D() {
		return (Graphics2D) gr;
	}

	public void setGr(Graphics gr) {
		this.gr = gr;
	}
	
	public void grSetColor(Color color) {
		gr.setColor(color);
	}
	
	public void grFillOval(int x, int y, int w, int h) {
		gr.fillOval(x,  y,  w,  h);
	}
	
	public void gr2DDrawImage(Image img, AffineTransform xform, ImageObserver obs){
		((Graphics2D)this.gr).drawImage(img, xform, obs);
	}

	public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
	}
	
}
