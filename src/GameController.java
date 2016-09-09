import java.awt.Color;

public class GameController {
	
	public LandingWindow landingWindow;
	
	//private CurveWindow curveWindow;
	private ConfigPanel configPanel;	
	
	/***********************************************************************
	 * 
	 * Game constants
	 * 
	 ************************************************************************/
	public static boolean finished;
	public static final int FRAME_SIZE_X = Main.SCREEN_WIDTH;
	public static final int FRAME_SIZE_Y = Main.SCREEN_HEIGHT;
	public static final int DEFAULT_THICK = 4;
	public static final Color DEFAULT_COLOR = Color.BLUE;
	public static final Color PLAYGROUND_BACKGROUND = new Color(30, 30, 30);
	public static final int PLAYGROUND_BORDER_WIDTH = 4;
	public static double DEFAULT_CURVE_SPEED;
	public static double DEFAULT_CURVE_ANGLE = 3;
	public static final int COUNT_DOWN_WIDTH  = 200;
	public static final int COUNT_DOWN_HEIGHT = 200;
	
	
		
	public GameController() {
		
		configPanel = new ConfigPanel();
		
		landingWindow = new LandingWindow(configPanel);
		
		
		configPanel.setSize(landingWindow.getContentPane().getWidth(), landingWindow.getContentPane().getHeight());
		configPanel.setLandingWindow(landingWindow);
	}
		
	public void startGame() {
		
		//curveWindow.startGame();	
		
	}
	
	public void endGame() {
		
	}
	
}	
