import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class CurveWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private GameController control;
	private Container contentPane;
	
	/**
	 * MENU BAR
	 */
	private JMenuBar menuBar;
	private JMenu mainMenu;
	private JMenuItem newGameItem;
	
	private JPanel namesPane;
	private PlayGround playGround;
	private ConfigPanel configPanel;
	
	private List<Control> ctrl; 
	
	public CurveWindow(GameController control) {
		super("Get the hang of it! ");
		this.control = control;
		
		contentPane = this.getContentPane();
		
		contentPane.setBackground(Color.WHITE);
		contentPane.setLayout(new BorderLayout());
		
		/******************************************************************
		 * 
		 * Menu bar
		 *  
		 ******************************************************************/
		menuBar = new JMenuBar();
		mainMenu = new JMenu("Options");
		newGameItem = new JMenuItem("New game");
		
		mainMenu.add(newGameItem);
		menuBar.add(mainMenu);
		setJMenuBar(menuBar);
		
		
		ctrl = null;
		
		/******************************************************************
		 * 
		 * WINDOW PROPERTIES
		 * 
		 ******************************************************************/
		this.setContentPane(contentPane);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(0,0,screenSize.width, screenSize.height);
		//this.setBounds(0, 0, 500, 500);
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    
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
		CurveWindow ref = this;
		newGameItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConfigPanel cfg = new ConfigPanel(ref);
			}
		});
		
	}
	
	public void createPlayGround(int players, List<Control> ctrl, List<String> names, List<Color> colors) {
		
		this.ctrl = ctrl;
		this.playGround = new PlayGround(players, ctrl, colors);
		
		addPlayerNames(names, colors);
		
		this.contentPane.add(playGround, BorderLayout.CENTER);		
		this.newGameItem.setEnabled(false);
		this.revalidate();
		
		this.playGround.repaint();
		
		control.setDisplayRefresherPlayGround(playGround);
		
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
		namesPane.setPreferredSize(new Dimension(Main.screenSize.width, 30));
		ListIterator iterNames  = names.listIterator();
		ListIterator iterColors = colors.listIterator();
		while(iterNames.hasNext()) {
			String name = (String)iterNames.next();
			Color color = (Color)iterColors.next();
			namesPane.add(new PlayerStatus(name, color));
		}
			  
		
		this.contentPane.add(namesPane, BorderLayout.NORTH);
		this.revalidate();
	}
	
	public GameController getControl() {
		return control;
	}
	
	public PlayGround getPlayGround() {
		return playGround;
	}
	
}