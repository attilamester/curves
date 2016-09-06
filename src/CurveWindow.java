import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
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
	private JMenu mainMenu;
	private JMenuItem newGameItem;
	
	private JPanel namesPane;
	
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
		
		mainMenu = new JMenu("Options");
		newGameItem = new JMenuItem("New game");
		
		
		JMenu stopMenu = new JMenu("<html><div>sa</div></html>");
		mainMenu.add(stopMenu);
		
		mainMenu.add(newGameItem);
		menuBar.add(mainMenu);
		
		menuBar.setBackground(GameController.PLAYGROUND_BACKGROUND);
		menuBar.setBorder(new EmptyBorder(0, 0, 0, 0));
		mainMenu.setForeground(Color.WHITE);
		
		
		setJMenuBar(menuBar);
		
		/******************************************************************
		 * 
		 * CONTENT
		 *  
		 ******************************************************************/
		
		this.ctrl = ctrl;
		this.names = names;
		this.colors = colors;
		
		this.playGround = new PlayGround(players, ctrl, colors);		
		
		this.addPlayerNames(names, colors);		
		this.contentPane.add(playGround, BorderLayout.CENTER);
		this.revalidate();
		this.playGround.repaint();
		
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
	    this.setAlwaysOnTop(true);
	    this.setUndecorated(true);
		this.setVisible(true);
	    
		
		addWindowListeners();
		addMenuListeners();
		
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
	
	public void addMenuListeners() {
		
		
	}
	
	public void createPlayGround(int players, List<Control> ctrl, List<String> names, List<Color> colors) {
		
		
		/*
		Timer timer_paintDirections = new Timer(1, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				control.startGame();
			}
		});
		Timer timer_eraseDirections = new Timer(1, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playGround.eraseArrows();
			}
		});
		timer_paintDirections.setInitialDelay(3001);
		timer_paintDirections.setRepeats(false);
		timer_eraseDirections.setInitialDelay(3000);
		timer_eraseDirections.setRepeats(false);
		timer_paintDirections.start();
		timer_eraseDirections.start();
		*/
	}
		
	private void addPlayerNames(List<String> names, List<Color> colors) {
		namesPane = new JPanel(new GridLayout(1, names.size()));
		namesPane.setPreferredSize(new Dimension(Main.SCREEN_WIDTH, 30));
		ListIterator<String> iterNames  = names.listIterator();
		ListIterator<Color> iterColors = colors.listIterator();
		while(iterNames.hasNext()) {
			String name = (String)iterNames.next();
			Color color = (Color)iterColors.next();
			namesPane.add(new PlayerStatus(name, color));
		} 		
		this.contentPane.add(namesPane, BorderLayout.NORTH);
		
	}
	
	public void startGame() {
		this.playGround.startGame();
		this.displayRefresher.start();
	}
	
	public PlayGround getPlayGround() {
		return playGround;
	}
	
}