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
	private Timer powerUpEffect;
	private int time;
	private boolean PERSONAL;
	private Callable<?> callback;
	
	private static final int FINISHED = 1;
	private static final int PROGRESS = 0;
	
	private int state;
	
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
		
		this.powerUpEffect = new Timer(time, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {				
				PowerUpTask.this.finish();
			}
		});
		powerUpEffect.setRepeats(false);		
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
					return;
				}
				progressBar.setValue(progressBar.getValue() + 1);
			}
		});		
	}
	
	private void createCommonLoadingBar() {		
		
		progressBar = (JProgressBar) panel.getComponent(0);
		progressBar.setValue(0);
		
		progressBarEffect = new Timer(time / GameController.PROGRESS_BAR_STEPS, null);
		progressBarEffect.addActionListener(new ActionListener() {
			int i = 0;
			@Override
			public void actionPerformed(ActionEvent e) {				
				if (i == GameController.PROGRESS_BAR_STEPS) {					
					progressBarEffect.stop();
					progressBar.setValue(0);
					progressBar.revalidate();
					progressBar.repaint();
					finish();
					return;
				}
				if (progressBar.getValue() < i) {
					progressBarEffect.stop();
				}
				progressBar.setValue(++i);				
			}
		});
	}
	
	
	public PowerUpTask start() {
		progressBarEffect.start();
		powerUpEffect.start();		
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
			progressBar.setValue(0);			
			progressBar.revalidate();
			progressBar.repaint();
			panel.revalidate();
			panel.repaint();
		}
	}


	public int getState() {
		return state;
	}
	
}
