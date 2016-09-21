import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class CurveWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private Container contentPane;
	
	/******************************************************************
	 * 
	 * Menu bar
	 *  
	 ******************************************************************/
	
	private JMenuBar menuBar;
	private JMenuItem stopMenu;
	private JMenuItem resumeMenu;

	/*****************************************************************
	 * Names + general progress bar
	 *****************************************************************/	
	private JPanel namesPane;
	private List<PlayerStatus> playerStatusPanes;
	private JPanel generalProgressPane;
	private JProgressBar generalProgressBar;
	
	private PlayGround playGround;
	private DisplayRefresher displayRefresher;	
	/******************************************************************
	 * 
	 * ESSENTIALS
	 *  
	 ******************************************************************/
	private List<Control> ctrl;
	private List<String> names;
	private List<Color> colors;
	
	public CurveWindow (int players, List<Control> ctrl, List<String> names, List<Color> colors) {
		super("Get the hang of it! ");
		
		contentPane = this.getContentPane();
		
		contentPane.setBackground(Color.WHITE);
		contentPane.setLayout(new BorderLayout());
		
		/******************************************************************
		 * 
		 * Menu bar
		 *  
		 ******************************************************************/
		UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(Color.WHITE, 1));
		menuBar = new JMenuBar();
		
		stopMenu = new JMenu("PAUSE");
		stopMenu.setForeground(Color.WHITE);
		
		resumeMenu = new JMenu("RESUME");
		resumeMenu.setForeground(Color.WHITE);
		resumeMenu.setEnabled(false);
		
		menuBar.add(stopMenu);
		menuBar.add(resumeMenu);
		
		menuBar.setBackground(GameController.PLAYGROUND_BACKGROUND);
		menuBar.setBorder(new EmptyBorder(0, 0, 0, 0));
		menuBar.setPreferredSize(new Dimension(Main.SCREEN_WIDTH, GameController.MENU_HEIGHT));
		
		setJMenuBar(menuBar);
		
		/******************************************************************
		 * 
		 * CONTENT
		 *  
		 ******************************************************************/
		
		this.ctrl = ctrl;
		this.names = names;
		this.colors = colors;
		
		this.addPlayerNames(names, colors);
		
		/*****************/
		JPanel playGroundContainer = new JPanel();
		playGroundContainer.setLayout(null);
		playGroundContainer.setBackground(Color.BLACK);
		
		this.playGround = new PlayGround(this, players, names, colors, GameController.FRAME_SIZE_X, GameController.FRAME_SIZE_Y);
		playGroundContainer.add(playGround);
		this.contentPane.add(playGroundContainer, BorderLayout.CENTER);		
		this.playGround.repaint();
		
		/*****************/
		this.displayRefresher = new DisplayRefresher(this.playGround);		
		/******************************************************************
		 * 
		 * WINDOW PROPERTIES
		 * 
		 ******************************************************************/		
		this.setBounds(0,0,Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    //this.setAlwaysOnTop(true);
	    this.setUndecorated(true);
	    
	 	this.setVisible(true);	    
		
		addWindowListeners();
		try {
			addMenuListeners();
		} catch(AWTException e) {}
	}
	
	public void addWindowListeners() {
		this.addKeyListener(new KeyAdapter() {
	    	@Override
	    	public void keyPressed(KeyEvent e) {
	    		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	    			System.exit(0);
	    		}
	    		int code = e.getKeyCode();	    		
	    		
	    		for (ListIterator<Control> iter = ctrl.listIterator(); iter.hasNext(); ) {
	    			int i = iter.nextIndex();
	    			Control obj = iter.next();
	    			if (code == obj.getLeft()){
	    				playGround.leftTurnTriggered(i);
	    			}
	    			else if (code == obj.getRight())
	    				playGround.rightTurnTriggered(i);
	    		}
	    	}
	    	@Override
	    	public void keyReleased(KeyEvent e) {
	    		int code = e.getKeyCode();
	    		for (ListIterator<Control> iter = ctrl.listIterator(); iter.hasNext(); ) {
	    			int i = iter.nextIndex();
	    			Control obj = iter.next();
	    			if (code == obj.getLeft())
	    				playGround.leftTurnStopped(i);
	    			else if (code == obj.getRight())
	    				playGround.rightTurnStopped(i);
	    		}
	    	}
	    });
	}
	
	public void addMenuListeners() throws AWTException {
		this.stopMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				CurveWindow.this.playGround.stopEvent();
				stopMenu.setEnabled(false);
				resumeMenu.setEnabled(true);
			}
		});
		Robot r = new Robot();
		this.resumeMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				CurveWindow.this.playGround.resumeEvent();
				stopMenu.setEnabled(true);
				resumeMenu.setEnabled(false);				
			    r.mouseMove(0, menuBar.getHeight() + (namesPane.getHeight() >> 1));
			    r.mousePress( InputEvent.BUTTON1_MASK );
			}
		});
	}
		
	private void addPlayerNames(List<String> names, List<Color> colors) {		
		JLayeredPane namesWrapper = new JLayeredPane();
		namesWrapper.setSize(Main.SCREEN_WIDTH, GameController.PLAYER_STATUS_PANE_HEIGHT);		
		namesWrapper.setPreferredSize(new Dimension(Main.SCREEN_WIDTH, GameController.PLAYER_STATUS_PANE_HEIGHT));
		
		this.namesPane = new JPanel(new GridLayout(1, names.size()));
		this.namesPane.setBounds(0, 0, Main.SCREEN_WIDTH, GameController.PLAYER_STATUS_PANE_HEIGHT);		
		
		/***************************************************************
		 * PROGRESS PANE - BAR 
		 ****************************************************************/
		this.generalProgressPane = new JPanel(new BorderLayout());
		this.generalProgressPane.setBounds(0, GameController.PLAYER_STATUS_PANE_HEIGHT - GameController.PROGRESS_BAR_HEIGHT, Main.SCREEN_WIDTH, GameController.PROGRESS_BAR_HEIGHT);		
		
		this.generalProgressBar = new JProgressBar();
		this.generalProgressBar.setBackground(Colors.TRANSPARENT);
		this.generalProgressBar.setForeground(Color.RED);
		this.generalProgressBar.setPreferredSize(new Dimension(Main.SCREEN_WIDTH, GameController.PROGRESS_BAR_HEIGHT));		
		this.generalProgressBar.setBorder(BorderFactory.createEmptyBorder());
		this.generalProgressBar.setBorderPainted(false);
		this.generalProgressBar.setMaximum(GameController.PROGRESS_BAR_STEPS);
		this.generalProgressBar.setValue(0);		
		this.generalProgressPane.add(this.generalProgressBar, BorderLayout.NORTH);	
		
		this.playerStatusPanes = new ArrayList<PlayerStatus	>();
		ListIterator<String> iterNames  = names.listIterator();
		ListIterator<Color> iterColors = colors.listIterator();
		while(iterNames.hasNext()) {
			String name = (String)iterNames.next();
			Color color = (Color)iterColors.next();
			PlayerStatus status = new PlayerStatus(name, color);
			this.playerStatusPanes.add(status);
			namesPane.add(status);
		}
		
		namesWrapper.add(this.namesPane, JLayeredPane.DEFAULT_LAYER);
		namesWrapper.add(this.generalProgressPane, new Integer(100));
		
		this.contentPane.add(namesWrapper, BorderLayout.NORTH);
		
	}

	public void startGame() {
		this.playGround.startGame();
		this.displayRefresher.start();
	}
	
	public void restartGame() {
		GameController.finished = false;
		this.playGround.restartGame();
		this.displayRefresher.restartRefresher();
	}
	
	public PlayGround getPlayGround() {
		return playGround;
	}

	public DisplayRefresher getDisplayRefresher() {
		return displayRefresher;
	}

	public List<PlayerStatus> getPlayerStatusPanes() {
		return playerStatusPanes;
	}

	public List<Control> getCtrl() {
		return ctrl;
	}

	public JPanel getGeneralProgressPane() {
		return generalProgressPane;
	}
	
	public JProgressBar getGeneralProgressBar() {
		return generalProgressBar;
	}
	
}