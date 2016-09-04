import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class ImageLayer {

	private BufferedImage img;
	private Graphics gr;
	
	public ImageLayer(int width, int height, Color defaultColor) {
		this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);		
		this.gr = img.getGraphics();
		if (defaultColor != null) {
			this.gr.setColor(GameController.PLAYGROUND_BACKGROUND);
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
	
	/*
	public Graphics2D getGr2D() {
		return (Graphics2D) gr;
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
*/
	
}
