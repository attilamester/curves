package curve_window;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

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

import curve.Control;
import curve.Player;
import generals.Colors;
import generals.GameController;
import generals.Main;

public class CurveWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private Container contentPane;
	private int curveWindowSizeX;
	private int curveWindowSizeY;
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
	private List<Control> ctrlsToListen;
	
	private JPanel namesPane;	
	private Map<String, Integer> namesScores;
	private Map<String, PlayerStatus> playerStatusPanes;
	private JPanel generalProgressPane;
	private JProgressBar generalProgressBar;
	
	private PlayGround playGround;
	private DisplayRefresher displayRefresher;	
	/******************************************************************
	 * 
	 * ESSENTIALS
	 *  
	 ******************************************************************/
	
	
	
	public CurveWindow (List<Control> ctrlsToListen, List<String> localNames, List<Color> localColors, List<String> remoteNames, List<Color> remoteColors) {
		super();
		
		contentPane = this.getContentPane();
		contentPane.setBackground(Color.WHITE);
		contentPane.setLayout(new BorderLayout());
		
		Rectangle r = Main.getGameController().getLandingWindow().getBounds();
		curveWindowSizeX = 600;//Main.SCREEN_WIDTH;
		curveWindowSizeY = 500;//Main.SCREEN_HEIGHT;
		int leftX = (int)r.getMinX();
		int topY  = (int)r.getMinY();
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
		
		this.ctrlsToListen = ctrlsToListen;
		
		/*****************/
		JPanel playGroundContainer = new JPanel();
		playGroundContainer.setLayout(null);
		playGroundContainer.setBackground(Color.BLACK);
		
		this.playGround = new PlayGround(this, localNames, localColors, remoteNames, remoteColors, curveWindowSizeX, curveWindowSizeY - GameController.MENU_HEIGHT - GameController.PLAYER_STATUS_PANE_HEIGHT);
		playGroundContainer.add(playGround);
		this.contentPane.add(playGroundContainer, BorderLayout.CENTER);
		this.playGround.repaint();
		
		this.addHeaderContent();
		/*****************/		
		this.displayRefresher = new DisplayRefresher(this.playGround);		
		/******************************************************************
		 * 
		 * WINDOW PROPERTIES
		 * 
		 ******************************************************************/		
		
		
		//this.setBounds((int)r.getMinX(),(int)r.getMinY(), curveWindowSizeX, curveWindowSizeY);
		this.setBounds(leftX, topY, curveWindowSizeX, curveWindowSizeY);
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		//this.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
	    		
	    		for (ListIterator<Control> iter = ctrlsToListen.listIterator(); iter.hasNext(); ) {
	    			int i = iter.nextIndex();
	    			Control obj = iter.next();
	    			if (code == obj.getLeft()){
	    				playGround.leftTurnTriggered(playGround.getLocalPlayers().get(i));
	    			}
	    			else if (code == obj.getRight())
	    				playGround.rightTurnTriggered(playGround.getLocalPlayers().get(i));
	    		}
	    	}
	    	@Override
	    	public void keyReleased(KeyEvent e) {
	    		int code = e.getKeyCode();
	    		for (ListIterator<Control> iter = ctrlsToListen.listIterator(); iter.hasNext(); ) {
	    			int i = iter.nextIndex();
	    			Control obj = iter.next();
	    			if (code == obj.getLeft())
	    				playGround.leftTurnStopped(playGround.getLocalPlayers().get(i));
	    			else if (code == obj.getRight())
	    				playGround.rightTurnStopped(playGround.getLocalPlayers().get(i));
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
			    r.mouseMove((int)getBounds().getMinX(), (int)getBounds().getMinY() + menuBar.getHeight() + (namesPane.getHeight() >> 1));
			    r.mousePress( InputEvent.BUTTON1_MASK );
			}
		});
	}
		
	private void addHeaderContent() {		
		JLayeredPane namesWrapper = new JLayeredPane();
		namesWrapper.setSize(this.curveWindowSizeX, GameController.PLAYER_STATUS_PANE_HEIGHT);		
		namesWrapper.setPreferredSize(new Dimension(this.curveWindowSizeX, GameController.PLAYER_STATUS_PANE_HEIGHT));
		
		this.namesPane = new JPanel(new GridLayout(1, this.playGround.getAllPlayers().size()));
		this.namesPane.setBounds(0, 0, this.curveWindowSizeX, GameController.PLAYER_STATUS_PANE_HEIGHT);		
		
		/***************************************************************
		 * PROGRESS PANE - BAR 
		 ****************************************************************/
		this.generalProgressPane = new JPanel(new BorderLayout());
		this.generalProgressPane.setBounds(0, GameController.PLAYER_STATUS_PANE_HEIGHT - GameController.PROGRESS_BAR_HEIGHT, this.curveWindowSizeX, GameController.PROGRESS_BAR_HEIGHT);		
		
		this.generalProgressBar = new JProgressBar();
		this.generalProgressBar.setBackground(Colors.TRANSPARENT);
		this.generalProgressBar.setForeground(Color.RED);
		this.generalProgressBar.setPreferredSize(new Dimension(this.curveWindowSizeX, GameController.PROGRESS_BAR_HEIGHT));		
		this.generalProgressBar.setBorder(BorderFactory.createEmptyBorder());
		this.generalProgressBar.setBorderPainted(false);
		this.generalProgressBar.setMaximum(GameController.PROGRESS_BAR_STEPS);
		this.generalProgressBar.setValue(0);		
		this.generalProgressPane.add(this.generalProgressBar, BorderLayout.NORTH);	
		
		this.playerStatusPanes = new HashMap<String, PlayerStatus>();
		for (Player p : this.playGround.getAllPlayers()) {
			this.playerStatusPanes.put(p.getName(), p.getPlayerStatusPane());
			namesPane.add(p.getPlayerStatusPane());
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

	public Map<String, PlayerStatus> getPlayerStatusPanes() {
		return this.playerStatusPanes;
	}

	public List<Control> getCtrlsToListen() {
		return ctrlsToListen;
	}

	public JPanel getGeneralProgressPane() {
		return generalProgressPane;
	}
	
	public JProgressBar getGeneralProgressBar() {
		return generalProgressBar;
	}

	public Map<String, Integer> getNamesScores() {
		return namesScores;
	}

	public int getCurveWindowSizeX() {
		return curveWindowSizeX;
	}

	public int getCurveWindowSizeY() {
		return curveWindowSizeY;
	}
	
}