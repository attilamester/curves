import java.awt.Color;
import java.awt.geom.Point2D;

public class BaseImageLayer extends ImageLayer {
	
	public BaseImageLayer(int w, int h, Color c) {
		super(w, h, c);
	}
	
	public synchronized void drawCurveHead(Curve curve) {
		
		int x = (int)curve.getX();
		int y = (int)curve.getY();
		int r = curve.getRadius();
		int padding = r + GameController.PLAYGROUND_BORDER_WIDTH;
		
		if (outOfBorderBounds(x, y, padding)) {
			System.out.println("Player out of borders");
			GameController.finished = true;
			return;
		}
		if (crashedToSomething(curve)) {
			System.out.println("Player crashed");
			GameController.finished = true;
			return;
		}

		if (curve.isPaused() &&
				Math.hypot(x - curve.getPausedX(), y - curve.getPausedY()) >=  r
			) {
			/**
			 * DELETE PREVIOUS CIRCLE
			 */
			//curves[i].getCurveLayer().getGr().setColor(GameController.PLAYGROUND_BACKGROUND);
			//curves[i].getCurveLayer().getGr().fillOval((int)curves[i].getOldX() - r, (int)curves[i].getOldY() - r, 2 * r, 2 * r);
			/////OLD WAY
			
			//curve.getDashLayer().getGr().setColor(new Color(10,50, 30));
			//curve.getDashLayer().getGr().fillOval((int)curve.getOldX() - r, (int)curve.getOldY() - r, 2 * r, 2 * r);
			//curve.setDashLayer(new ImageLayer(GameController.FRAME_SIZE_X, GameController.FRAME_SIZE_Y, null));
			curve.resetDashLayer();
			curve.getDashLayer().getGr().setColor(curve.getColor());
			curve.getDashLayer().getGr().fillOval(x - r, y - r, 2 * r, 2 * r);
			
			//this.getGr().drawImage(curve.getDashLayer().getImg(), 0, 0, null);
			
		} else {
			//this.getGr().setColor(curve.getColor());
			//this.getGr().fillOval(x - r, y - r, 2 * r, 2 * r);
			
			curve.getCurveLayer().getGr().setColor(curve.getColor());
			curve.getCurveLayer().getGr().fillOval(x - r, y - r, 2 * r, 2 * r);
			
			this.getGr().drawImage(curve.getCurveLayer().getImg(), 0, 0, null);
		}
		
		this.getGr().drawImage(curve.getDashLayer().getImg(), 0, 0, null);
		this.getGr().drawImage(curve.getCurveLayer().getImg(), 0, 0, null);
		
	}
	
	/***************************************************************************************************************************************************************
	 * 
	 * COLLISION and BORDER
	 * 
	 ****************************************************************************************************************************************************************/
	
	private boolean outOfBorderBounds(int x, int y, int padding) {
		return (x < padding  || y < padding || x > this.getImg().getWidth() - padding  || y > this.getImg().getHeight() - padding); 
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
			paintedColor = this.getImg().getRGB((int)point.getX(), (int)point.getY());			
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
