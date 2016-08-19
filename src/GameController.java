import java.awt.Color;
import java.awt.Dimension;

public class GameController {
	
	private CurveWindow curveWindow;
	private DisplayRefresher displayRefresher;
	
	/*
	 * Game constants
	 */
	public static boolean finished;
	public static final int FRAME_SIZE_X = Main.screenSize.width;
	public static final int FRAME_SIZE_Y = Main.screenSize.height;
	public static final int DEFAULT_THICK = 4;
	public static final Color DEFAULT_COLOR = Color.BLUE;
	public static final double DEFAULT_CURVE_SPEED = 1.5;
	public static final double DEFAULT_CURVE_ANGLE = 1.5;
	public static final Color PLAYGROUND_BACKGROUND = new Color(30, 30, 30);
	public static final int PLAYGROUND_BORDER_WIDTH = 8;
	public static final Direction DEFAULT_DIR = new Direction(0, 1.5);
	
	
	public GameController() {
		
		curveWindow = new CurveWindow(this);
		displayRefresher = new DisplayRefresher();
		
	}
		
	public void startGame() {
		
		curveWindow.getPlayGround().startGame();
		displayRefresher.start();
		
	}
	
	public void endGame() {
		
	}
	
	public void setDisplayRefresherPlayGround(PlayGround pl) {
		displayRefresher.setPlayGround(pl);
	}
	
}	
