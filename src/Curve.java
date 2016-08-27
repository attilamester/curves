import java.awt.Color;
import java.awt.image.BufferedImage;

public class Curve {
	
	private double x;
	private double y;
	private double oldX;
	private double oldY;
	
	private int radius;
	private double turnAngle;
	private Color color;
	private Direction direction;
	private boolean leftIsPressed;
	private boolean rightIsPressed;
	
	private boolean paused;
	private double pausedX;
	private double pausedY;

	private BufferedImage img;
	
	public Curve(double x, double y, int radius, double turnAngle, Color color, Direction direction) {
		
		this.x = this.oldX = x;
		this.y = this.oldY = y;
		
		this.radius = radius;
		this.turnAngle = turnAngle;
		this.color = color;		
		this.direction = new Direction(direction.getI(), direction.getJ());
		
		this.leftIsPressed = false;
		this.rightIsPressed = false;
		
		this.img = new BufferedImage(GameController.FRAME_SIZE_X, GameController.FRAME_SIZE_Y, BufferedImage.TYPE_INT_ARGB);
	}
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getOldX() {
		return oldX;
	}

	public void setOldX(double oldX) {
		this.oldX = oldX;
	}

	public double getOldY() {
		return oldY;
	}

	public void setOldY(double oldY) {
		this.oldY = oldY;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public Color getColor() {
		return color;
	}
	public double getTurnAngle() {
		return turnAngle;
	}

	public void setTurnAngle(double turnAngle) {
		this.turnAngle = turnAngle;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	public Direction getDirection() {
		return direction;
	}
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	public boolean isLeftPressed() {
		return leftIsPressed;
	}

	public void setLeftPressed(boolean leftIsPressed) {
		this.leftIsPressed = leftIsPressed;
	}

	public boolean isRightPressed() {
		return rightIsPressed;
	}

	public void setRightPressed(boolean rightIsPressed) {
		this.rightIsPressed = rightIsPressed;
	}
	
	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
	public double getPausedX() {
		return pausedX;
	}

	public void setPausedX(double pausedX) {
		this.pausedX = pausedX;
	}

	public double getPausedY() {
		return pausedY;
	}

	public void setPausedY(double pausedY) {
		this.pausedY = pausedY;
	}

	public double calcSpeed() {
		return Math.hypot(direction.getI(), direction.getJ());
	}
	
	public double getM() {
		return direction.getJ() / direction.getI();
	}
	
	public BufferedImage getImg() {
		return img;
	}

	/*******************************************************
	 * 
	 * TURN
	 * 
	 *******************************************************/
	public void turnLeft() {
		double i = this.getDirection().getI();
		double j = this.getDirection().getJ();
		double newI = (
				i * Math.cos(Math.toRadians(-turnAngle)) -
				j * Math.sin(Math.toRadians(-turnAngle))
			);
		double newJ = (
				i * Math.sin(Math.toRadians(-turnAngle)) +
				j * Math.cos(Math.toRadians(-turnAngle))
			);
		this.getDirection().setI(newI);
		this.getDirection().setJ(newJ);
	}
	public void turnRight() {
		double i = this.getDirection().getI();
		double j = this.getDirection().getJ();
		this.getDirection().setI( (double) (
			i * Math.cos(Math.toRadians(turnAngle)) -
			j * Math.sin(Math.toRadians(turnAngle))
		));
		this.getDirection().setJ( (double) (
			i * Math.sin(Math.toRadians(turnAngle)) +
			j * Math.cos(Math.toRadians(turnAngle))
		));
	}
}
