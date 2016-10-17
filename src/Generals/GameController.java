package generals;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import landing_pages.LandingWindow;

public class GameController {
	
	private LandingWindow landingWindow;
	
	/***********************************************************************
	 * 
	 * Game constants
	 * 
	 ************************************************************************/
	public static boolean finished;
	public static int ROUND_COUNT = 15;
	
	public static final int MENU_HEIGHT = 20;
	public static final int PLAYER_STATUS_PANE_HEIGHT = 30;
	public static final int PROGRESS_BAR_HEIGHT = 3;
	public static final int PROGRESS_BAR_STEPS = 100;
	public static final int GENERAL_TASK_TIMER_LENGTH = 5000;
	
	public static final int FRAME_SIZE_X = Main.SCREEN_WIDTH;
	public static final int FRAME_SIZE_Y = Main.SCREEN_HEIGHT - MENU_HEIGHT - PLAYER_STATUS_PANE_HEIGHT;
	
	public static final int DEFAULT_THICK = 4;
	public static final Color DEFAULT_COLOR = Color.BLUE;
	
	public static final Color PLAYGROUND_BACKGROUND = new Color(30, 30, 30);
	public static final int PLAYGROUND_BORDER_WIDTH = 4;
	public static final Border PLAYGROUND_BORDER_FACTORY = BorderFactory.createMatteBorder(
			GameController.PLAYGROUND_BORDER_WIDTH,// - GameController.PROGRESS_BAR_HEIGHT,
			GameController.PLAYGROUND_BORDER_WIDTH,
			GameController.PLAYGROUND_BORDER_WIDTH,
			GameController.PLAYGROUND_BORDER_WIDTH, Color.WHITE);
	public static final Border PLAYGROUND_NO_BORDER_FACTORY = BorderFactory.createMatteBorder(
			GameController.PLAYGROUND_BORDER_WIDTH ,//- GameController.PROGRESS_BAR_HEIGHT,
			GameController.PLAYGROUND_BORDER_WIDTH,
			GameController.PLAYGROUND_BORDER_WIDTH,
			GameController.PLAYGROUND_BORDER_WIDTH, new Color(75, 75, 75));
	
	public static double DEFAULT_CURVE_SPEED;
	public static double DEFAULT_CURVE_ANGLE = 3;
	
	public static final int COUNT_DOWN_WIDTH  = 200;
	public static final int COUNT_DOWN_HEIGHT = 200;
	
	public static final int ERROR_MODAL_WIDTH  = 200;
	public static final int ERROR_MODAL_HEIGHT = 75;
	
	public static final String SCORE_FILE_PATH = ".\\scores.txt";
	
		
	public GameController() {
		landingWindow = new LandingWindow();
	}
		
	public void startGame() {
		
		//curveWindow.startGame();	
		
	}
	
	public void endGame() {
		
	}

	public LandingWindow getLandingWindow() {
		return landingWindow;
	}
	
	
	
}	
