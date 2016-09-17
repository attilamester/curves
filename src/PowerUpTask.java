
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class PowerUpTask {
	
	private JPanel panel;
	private JProgressBar progressBar;
	private Timer progressBarEffect;
	private int time;
	private boolean PERSONAL;
	private Callable<?> callback;
	
	public static final int FINISHED = 1;
	public static final int PROGRESS = 0;
	
	private int state;
	
	public static boolean generalStarted = false;
	public static int generalProgressValue = 0;
	private Timer generalTimer = new Timer(GameController.GENERAL_TASK_TIMER_LENGTH / GameController.PROGRESS_BAR_STEPS, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (PowerUpTask.generalProgressValue == GameController.PROGRESS_BAR_STEPS) {					
				generalTimer.stop();
				PowerUpTask.generalStarted = false;
				finish();
				return;
			}
			progressBar.setValue(++PowerUpTask.generalProgressValue);
		}
	});
	
	public PowerUpTask(int time, boolean PERSONAL, JPanel panel) {
		this.panel = panel;
		this.time = time;
		this.PERSONAL = PERSONAL;
		this.state = PowerUpTask.PROGRESS;
		
		if (PERSONAL) {
			createPersonalLoadingBar();
		} else {
			createCommonLoadingBar();
		}
	}
	
	
	public void setCallback(Callable<?> callback) {
		this.callback = callback;
	}

	private void createPersonalLoadingBar() {
		progressBar = new JProgressBar();		
		progressBar.setOrientation(SwingConstants.VERTICAL);
		progressBar.setPreferredSize(new Dimension(10, GameController.PLAYER_STATUS_PANE_HEIGHT));
		progressBar.setBackground(Color.WHITE);
		progressBar.setForeground(Color.RED);
		progressBar.setBorder(BorderFactory.createEmptyBorder());
		progressBar.setBorderPainted(false);
		progressBar.setMaximum(100);
		progressBar.setValue(0);
		
		panel.add(progressBar);
		panel.revalidate();
		
		
		progressBarEffect = new Timer(time / 100, null);
		progressBarEffect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (progressBar.getValue() == 100) {
					progressBarEffect.stop();
					finish();
					return;
				}
				progressBar.setValue(progressBar.getValue() + 1);
			}
		});		
	}
	
	private void createCommonLoadingBar() {
		progressBar = (JProgressBar) panel.getComponent(0);
		PowerUpTask.generalProgressValue = 0;
		progressBar.setValue(0);
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
			if(panel.getComponentCount() != 0) {
				panel.remove(progressBar);
				panel.revalidate();
				panel.repaint();
			}
		} else {
			progressBarEffect.stop();
			progressBar.setValue(0);
		}
	}

	public int getState() {
		return state;
	}

	public boolean isPERSONAL() {
		return PERSONAL;
	}
}
