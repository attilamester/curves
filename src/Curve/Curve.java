package curve;
import java.awt.Color;
import java.io.Serializable;

public class Curve implements Serializable {
	private static final long serialVersionUID = 1;
	
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

	private long lastCollidedAt;
	private int collisionCount;
	
	private int flyCount;
	private int bulldozerCount;
	private int swapCount;
	
	private int circleNumber;
	
	public Curve (Curve curve) {
		this(curve.getX(), curve.getY(), curve.getRadius(), curve.getTurnAngle(), new Color(curve.getColor().getRGB()), curve.getDirection());
	}
	
	public Curve (double x, double y, int radius, double turnAngle, Color color, Direction direction) {
		this.direction = new Direction(0, 0);
		this.initData(x, y, radius, turnAngle, color, direction);
	}
	
	public void initData(double x, double y, int radius, double turnAngle, Color color, Direction direction) {
		this.x = this.oldX = x;
		this.y = this.oldY = y;
		
		this.radius = radius;
		this.turnAngle = turnAngle;
		this.color = color;		
		this.direction.setI(direction.getI());
		this.direction.setJ(direction.getJ());
		
		this.leftIsPressed = false;
		this.rightIsPressed = false;
		
		this.lastCollidedAt = 0;
		this.collisionCount = 0;
		this.flyCount = 0;
		this.swapCount = 0;
		this.circleNumber = 0;
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
	
	public double getM() {
		return direction.getJ() / direction.getI();
	}
	
	public long getLastCollidedAt() {
		return lastCollidedAt;
	}

	public void setLastCollidedAt(long lastCollidedAt) {
		this.lastCollidedAt = lastCollidedAt;
	}

	public int getCollisionCount() {
		return collisionCount;
	}

	public void setCollisionCount(int collisionCount) {
		this.collisionCount = collisionCount;
	}
	
	public int getFlyCount() {
		return flyCount;
	}

	public void setFlyCount(int flyCount) {
		this.flyCount = flyCount;
	}
	
	public int getSwapCount() {
		return swapCount;
	}

	public void setSwapCount(int swapCount) {
		this.swapCount = swapCount;
	}
	
	public void swapControl(boolean processStarted) {
		if (processStarted) {
			++this.swapCount;
		} else {
			--this.swapCount;
		}
		boolean tmp = this.leftIsPressed;
		this.leftIsPressed = this.rightIsPressed; 
		this.rightIsPressed = tmp;
	}
	
	public int getBulldozerCount() {
		return bulldozerCount;
	}

	public void setBulldozerCount(int bulldozerCount) {
		this.bulldozerCount = bulldozerCount;
	}

	public double calcSpeed() {
		return Math.hypot(direction.getI(), direction.getJ());
	}
	
	public int getCircleNumber() {
		return circleNumber;
	}

	public void setCircleNumber(int circleNumber) {
		this.circleNumber = circleNumber;
	}

	// nem akar menni a jo egnek se :(  :/ :D :))
	public void resetDashLayer() {
		/*Graphics2D gr = (Graphics2D)this.dashLayer.getGr();
		
		gr.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 1f));
		gr.fill(new Rectangle2D.Double(0, 0, GameController.FRAME_SIZE_X, GameController.FRAME_SIZE_Y));
		
		gr.setComposite(defaultComposite);
		//gr.setBackground(new Color(50,0,0,0));
		//gr.setColor(Color.green);
		//gr.clearRect(0, 0, GameController.FRAME_SIZE_X, GameController.FRAME_SIZE_Y);
		//this.dashLayer = new ImageLayer(GameController.FRAME_SIZE_X, GameController.FRAME_SIZE_Y, null);
		//this.dashLayer.getGr().clearRect(0, 0, GameController.FRAME_SIZE_X, GameController.FRAME_SIZE_Y);
		
		//gr.fill(new Rectangle2D.Double(0,0,GameController.FRAME_SIZE_X, GameController.FRAME_SIZE_Y));
		//gr.setComposite(defaultComposite);
		/*
		Graphics2D gr2D = this.dashLayer.getImg().createGraphics();
		gr2D.setBackground(new Color(0,0,0,0));
		gr2D.setComposite(AlphaComposite.Clear);
		gr2D.clearRect(0, 0, GameController.FRAME_SIZE_X, GameController.FRAME_SIZE_Y);
		*/
		
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
	
	/**********************************************************
	 * 
	 * ACTIONS
	 * 
	 ********************************************************/
	public void slowDown() {
		direction.setI(direction.getI() * 0.5);
		direction.setJ(direction.getJ() * 0.5);
	}
	
	public void speedUp() {
		direction.setI(direction.getI() * 2);
		direction.setJ(direction.getJ() * 2);
	}
	
	public void thickUp() {
		this.radius *= 2;
	}
	
	public void thickDown() {
		this.radius *= 0.5;
	}
	
	
}
