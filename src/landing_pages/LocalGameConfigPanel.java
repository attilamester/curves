package landing_pages;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import curve.Control;
import curve_window.CurveWindow;
import generals.Colors;
import generals.GameController;
import generals.Main;
import landing_pages.PlayerConfigRow.TextFieldPlaceholder;
import modals.CountDownModal;
import modals.ErrorDialog;
import network_packages.PreGameInfo;

public class LocalGameConfigPanel extends JPanel {
	private static final long serialVersionUID = 1;
	
	protected Random rnd;
	
	protected JLabel speedLabel;
	protected JLabel angleLabel;
	protected JLabel playerNrLabel;
	
	/**
	 * Content - to CENTER of contentPane
	 */
	protected JPanel contentPane;
	protected JPanel topConfigsPane;
	
	protected JPanel localPlayersPane;
	protected JPanel localPlayersPaneHeader;
	protected JPanel localPlayersPaneContent;
	protected JScrollPane localPlayersPaneContentScrollPane;
	
	protected JPanel buttonsPane;
		
	protected JSlider speedSlider;
	protected JSlider angleSlider;
	
	protected JSpinner playerCount;
	protected volatile List<PlayerConfigRow> localPlayerConfigRows;
	
	protected JLabel start;
	
	public LocalGameConfigPanel(int width, int height, Callable<Void> callBack) {
		
		this.rnd = new Random();
		
		this.setLayout(new BorderLayout());		
		this.setSize(width, height);		
		
		addItems(callBack);
				
	}
	
	public Random getRnd() {
		return rnd;
	}

	private void addItems(Callable<Void> callBack) {
		Main.addBackPane(this, callBack);
		
		createSpeedSlider();
		createAngleSlider();
		createNumberSpinner();
		
		createTopConfigPane();
		createMiddleContent();
		createBottomButtonsPane();
		
		this.contentPane = new JPanel(new BorderLayout());
		this.contentPane.add(topConfigsPane, BorderLayout.NORTH);
		this.contentPane.add(localPlayersPane, BorderLayout.CENTER);
		this.contentPane.add(buttonsPane,BorderLayout.SOUTH);
		
		this.add(this.contentPane, BorderLayout.CENTER);
	}
	
	private void createSpeedSlider() {
		Color bg = Colors.MAIN_COLORS[0];
		
		this.speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 500, 150);
		speedSlider.setBackground(bg);
		
		speedLabel = new JLabel("Curve speed:");
		speedLabel.setOpaque(true);
		speedLabel.setBorder(new EmptyBorder(5, 10, 5, 0));
		speedLabel.setBackground(bg);
		speedLabel.setForeground(Color.WHITE);
		speedLabel.setLabelFor(speedSlider);
	}
	
	private void createAngleSlider() {
		Color bg = Colors.MAIN_COLORS[1];
		
		this.angleSlider = new JSlider(JSlider.HORIZONTAL, 10, 40, 20);
		angleSlider.setBackground(bg);
		
		angleLabel = new JLabel("Turn angle:");
		angleLabel.setOpaque(true);
		angleLabel.setBorder(new EmptyBorder(5, 10, 5, 0));
		angleLabel.setForeground(Color.WHITE);
		angleLabel.setBackground(bg);
		angleLabel.setLabelFor(angleSlider);
	}
	
	private void createNumberSpinner() {
		Color bg = Colors.MAIN_COLORS[2];
		
		SpinnerModel model = new SpinnerNumberModel(1, 1, 10, 1);
		this.playerCount = new JSpinner(model);
		((JSpinner.DefaultEditor) playerCount.getEditor()).getTextField().setEditable(false);
		((JSpinner.DefaultEditor) playerCount.getEditor()).getTextField().setForeground(Color.WHITE);
		playerCount.setPreferredSize(new Dimension(50, 25));
		playerCount.setBorder(BorderFactory.createEmptyBorder());
		
		
		Component c = playerCount.getEditor().getComponent(0);
		c.setFont(new Font("Calibri", Font.BOLD, 18));
		c.setBackground(bg);
		
		playerCount.addChangeListener(new ChangeListener() {
			@Override
		    public void stateChanged(ChangeEvent e) {			
				int nr = (int)playerCount.getValue();				
				if (nr > localPlayerConfigRows.size()) {
					localPlayersPaneContent.setLayout(new GridLayout(nr, 1));
					PlayerConfigRow row = new PlayerConfigRow(rnd);
					localPlayerConfigRows.add(row);
					localPlayersPaneContent.add(row);					
					localPlayersPaneContent.revalidate();
				} else {
					localPlayersPaneContent.setLayout(new GridLayout(localPlayerConfigRows.size() - 1, 1));
					PlayerConfigRow ref = localPlayerConfigRows.get(localPlayerConfigRows.size() - 1);
					localPlayerConfigRows.remove(ref);					
					localPlayersPaneContent.remove(ref);					
					localPlayersPaneContent.revalidate();
				}
			}
		});
		
		playerNrLabel = new JLabel("Local players:");
		playerNrLabel.setOpaque(true);
		playerNrLabel.setBorder(new EmptyBorder(5, 10, 5, 0));
		playerNrLabel.setBackground(bg);
		playerNrLabel.setForeground(Color.WHITE);
		playerNrLabel.setLabelFor(playerCount);
		
	}
	
	private void createTopConfigPane() {		
		this.topConfigsPane = new JPanel();
		topConfigsPane.setLayout(new GridLayout(2, 2));
		topConfigsPane.add(this.speedLabel);
		topConfigsPane.add(this.speedSlider);
		topConfigsPane.add(this.angleLabel);
		topConfigsPane.add(this.angleSlider);
	}
	
	private void createMiddleContent() {
		this.localPlayersPane = new JPanel(new BorderLayout());
		
		this.localPlayersPaneHeader = new JPanel(new GridLayout(1, 2));
		this.localPlayersPaneHeader.add(this.playerNrLabel);
		this.localPlayersPaneHeader.add(this.playerCount);
		
		int nr = (int)playerCount.getValue();
		this.localPlayersPaneContent = new JPanel(new GridLayout(nr, 1));
		this.localPlayersPaneContent.setBackground(GameController.PLAYGROUND_BACKGROUND);
		this.localPlayerConfigRows = new ArrayList<>();
		for (int i = 0; i < nr; ++i) {
			PlayerConfigRow row = new PlayerConfigRow(rnd);
			this.localPlayerConfigRows.add(row);
			this.localPlayersPaneContent.add(row);
		}
		
		this.localPlayersPaneContentScrollPane = new JScrollPane(this.localPlayersPaneContent);
		this.localPlayersPaneContentScrollPane.setBackground(GameController.PLAYGROUND_BACKGROUND);
		this.localPlayersPaneContentScrollPane.getViewport().setBackground(GameController.PLAYGROUND_BACKGROUND);
		this.localPlayersPaneContentScrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		this.localPlayersPane.add(this.localPlayersPaneHeader, BorderLayout.NORTH);
		this.localPlayersPane.add(this.localPlayersPaneContentScrollPane, BorderLayout.CENTER);
	}
	
	private void createBottomButtonsPane() {
		this.buttonsPane = new JPanel();
		buttonsPane.setBackground(Colors.BACK_PANE);
		
		this.start = new JLabel(new ImageIcon(Main.class.getResource("/startBg.png")));
		start.setBackground(new Color(0,0,0,0));
		start.setOpaque(false);
		
		this.buttonsPane.add(start);
				
		/*************************************************************************
		 * 
		 * START GAME
		 * 
		 *************************************************************************/		
		this.start.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {										
				start.setBackground(new Color(0,0,0,0));
				start.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				start.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				start.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
				List<Control> ctrl = new ArrayList<Control>();
				List<String> names = new ArrayList<String>();
				List<Color> colors = new ArrayList<Color>();
				
				if (!checkPlayersCorrectness(localPlayerConfigRows, ctrl, names, colors)) {
					return;
				}
				
				GameController.finished = false;
				GameController.DEFAULT_CURVE_ANGLE = angleSlider.getValue() / 10;
				GameController.DEFAULT_CURVE_SPEED = (double)speedSlider.getValue() / 100;
				
				CurveWindow curveWindow = new CurveWindow(ctrl, names, colors);
				Main.getGameController().setCurveWindow(curveWindow);
				new CountDownModal(curveWindow, 1, null, null);
			}
		});
	    
	}
	
	protected boolean checkPlayersCorrectness(List<PlayerConfigRow> configRowList, List<Control> ctrl, List<String> names, List<Color> colors) {
		int i = 0;
		for (PlayerConfigRow row : configRowList) {
			
			if (row.getLeft() == -1 || row.getRight() == -1 || row.getLeft() == row.getRight()) {						
				new ErrorDialog("Curves must have proper controls!");
				return false;
				//ctrl.add(new Control(65, 83));
			} else {
				ctrl.add(new Control(row.getLeft(), row.getRight()));
			}
			
			
			/**
			 * Color check
			 */
			if (colors.contains(row.getColor()) || names.contains(row.getName())) {
				new ErrorDialog("Colors and Names must be unique!");
				return false;
			} else {
				colors.add(row.getColor());
				names.add(row.getName().length() > 0 ? row.getName() : ("#player" + Integer.toString(++i)));
				
			}
			
		}
		return true;
	}
	
	
	public List<TextFieldPlaceholder> collectTextFields() {
		List<TextFieldPlaceholder> list = new ArrayList<>();
		for(PlayerConfigRow row : this.localPlayerConfigRows) {
			list.add(row.getTextFieldPlaceholder());
		}
		System.out.println(list.size());
		return list;
	}
	
	protected void addTextFieldListToPanel(JPanel panel, List<TextFieldPlaceholder> playerRows) {
		panel.setLayout(new GridLayout(playerRows.size(), 1));
		for(TextFieldPlaceholder textBox : playerRows) {
			textBox.setEditable(false);
			textBox.setPreferredSize(new Dimension(150, 25));
			textBox.setFocusable(false);
			textBox.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1, true));
			
			JPanel playerPanel = new JPanel();
			playerPanel.setBackground(GameController.PLAYGROUND_BACKGROUND);
			playerPanel.add(textBox);
			panel.add(playerPanel);
		}
		panel.revalidate();
		panel.repaint();
	}
	
	protected void triggerTextFieldChange() {
		if (Main.getGameServer() != null) {
			// we are server
			Main.getGameController().getLandingWindow().getLanGameConfigPanel().shareServerPlayersToClients();
		} else {
			if (Main.getGameClient() != null) {
				Main.getGameClient().respondToServer(new PreGameInfo(0, collectTextFields()));
			}
		}
	}
	
	public JPanel getTopConfigPane() {
		return topConfigsPane;
	}

	public JScrollPane getScrollPane() {
		return this.localPlayersPaneContentScrollPane;
	}
	
	public JPanel getContentPane() {
		return contentPane;
	}

	public List<PlayerConfigRow> getPlayers() {
		return localPlayerConfigRows;
	}

	public JPanel getLocalPlayersPane() {
		return localPlayersPane;
	}
	
	public JPanel getLocalPlayersPaneContent() {
		return localPlayersPaneContent;
	}

	public JPanel getButtonsPane() {
		return buttonsPane;
	}

	
	
	
}