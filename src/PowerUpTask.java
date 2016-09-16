import java.awt.BorderLayout;
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
	private Callable<?> callback;
	
	public PowerUpTask(int time, boolean PERSONAL, JPanel panel) {
		this.panel = panel;
		this.time = time;
		
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
					panel.remove(progressBar);
					panel.revalidate();
					panel.repaint();
					return;
				}
				progressBar.setValue(progressBar.getValue() + 1);
			}
		});		
	}
	
	private void createCommonLoadingBar() {
		int STEPS = 100;
		
		if (panel.getComponentCount() == 1) {
			progressBar = (JProgressBar) panel.getComponent(0);
			progressBar.setValue(0);
		} else {
			progressBar = new JProgressBar();
			progressBar.setBackground(Colors.TRANSPARENT);
			progressBar.setForeground(Color.RED);		
			progressBar.setPreferredSize(new Dimension(Main.SCREEN_WIDTH, 5));
			progressBar.setBorderPainted(false);
			progressBar.setBorder(BorderFactory.createEmptyBorder());
			progressBar.setBorderPainted(false);
			progressBar.setMaximum(STEPS);
			progressBar.setValue(0);
			
			panel.add(progressBar, BorderLayout.NORTH);		
			panel.revalidate();
		}
		
		progressBarEffect = new Timer(time / STEPS, null);
		progressBarEffect.addActionListener(new ActionListener() {
			int i = 0;
			@Override
			public void actionPerformed(ActionEvent e) {				
				if (i == STEPS) {					
					progressBarEffect.stop();
					panel.remove(progressBar);
					panel.setBackground(Colors.TRANSPARENT);
					panel.revalidate();
					panel.repaint();
					return;
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
		try {
			this.callback.call();			
		} catch (Exception e1) {}
		if(panel.getComponentCount() != 0) {
			panel.remove(progressBar);
			panel.revalidate();
			panel.repaint();
		}
	}
	
}
