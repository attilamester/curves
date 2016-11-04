package power_up;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import curve.Player;
import curve_window.CurveWindow;
import generals.GameController;

public class PowerUpTask {
	
	private CurveWindow curveWindow;
	
	private List<Player> players;
	
	private List<JProgressBar> progressBars;
	private Timer progressBarEffect;
	private int personalProgressValue = 0;
	
	private JProgressBar generalProgressBar;
	public static boolean generalStarted = false;
	public static int generalProgressValue = 0;
	private Timer generalTimer;
		
	private int duration;
	private boolean PERSONAL;
	private Callable<?> callback;
	
	public static final int FINISHED = 1;
	public static final int PROGRESS = 0;
	
	private int state;
	
	public PowerUpTask (CurveWindow curveWindow, int time, boolean PERSONAL, List<Player> players) {
		this.curveWindow = curveWindow;
		
		this.players = players;
		
		this.duration = time;
		this.PERSONAL = PERSONAL;
		this.state = PowerUpTask.PROGRESS;
		
		if (PERSONAL) {
			createPersonalLoadingBar();
		} else {
			createCommonLoadingBar();
		}
		
		
		generalTimer = new Timer(GameController.GENERAL_TASK_TIMER_LENGTH / GameController.PROGRESS_BAR_STEPS, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (PowerUpTask.generalProgressValue == GameController.PROGRESS_BAR_STEPS) {					
					generalTimer.stop();
					PowerUpTask.generalStarted = false;
					finish();					
					return;
				}
				curveWindow.getGeneralProgressBar().setValue(++PowerUpTask.generalProgressValue);
				curveWindow.repaint();
			}
		});
	}
	
	
	public void setCallback(Callable<?> callback) {
		this.callback = callback;
	}

	private void createPersonalLoadingBar() {
		this.progressBars = new ArrayList<>(this.players.size());
		
		for (Player i : this.players) {			
			JProgressBar progressBar = new JProgressBar();		
			progressBar.setOrientation(SwingConstants.VERTICAL);
			progressBar.setPreferredSize(new Dimension(10, GameController.PLAYER_STATUS_PANE_HEIGHT));
			progressBar.setBackground(Color.WHITE);
			progressBar.setForeground(Color.RED);
			progressBar.setBorder(BorderFactory.createEmptyBorder());
			progressBar.setBorderPainted(false);
			progressBar.setMaximum(100);
			progressBar.setValue(0);
			
			this.progressBars.add(progressBar);
			
			i.getPlayerStatusPane().add(progressBar);
			i.getPlayerStatusPane().revalidate();
			i.getPlayerStatusPane().repaint();
		}
		
		progressBarEffect = new Timer(duration / 100, null);
		progressBarEffect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {			
				for (JProgressBar progressBar : progressBars) {
					if (personalProgressValue == 100) {
						progressBarEffect.stop();
						finish();
						return;
					}
					progressBar.setValue(++personalProgressValue);
				}
			}
		});		
	}
	
	private void createCommonLoadingBar() {
		generalProgressBar = curveWindow.getGeneralProgressBar();
		generalProgressBar.setValue(0);
		PowerUpTask.generalProgressValue = 0;		
		progressBarEffect = this.generalTimer;
	}
	
	
	public PowerUpTask start() {
		if(this.PERSONAL) {
			progressBarEffect.start();
		} else {
			generalTimer.start();
			PowerUpTask.generalStarted = true;
		}		
		return this;
	}
	
	public void finish() {
		if (this.state == PowerUpTask.FINISHED)
			return;
		this.state = PowerUpTask.FINISHED;
		
		removeProgressBar();
		
		try {
			this.callback.call();
		} catch (Exception e1) {}
	}
	
	private void removeProgressBar() {
		if (this.PERSONAL) {
			
			int nr = 0;
			for (Player i : this.players) {
				
				JPanel panel = i.getPlayerStatusPane();
				
				if(panel.getComponentCount() != 0) {
					panel.remove(progressBars.get(nr++));
					panel.revalidate();
					panel.repaint();
				}				
			}
		} else {
			PowerUpTask.generalStarted = false;
			generalTimer.stop();
			generalProgressBar.setValue(0);
			curveWindow.repaint();
		}
	}

	public int getState() {
		return state;
	}

	public boolean isPERSONAL() {
		return PERSONAL;
	}
}
