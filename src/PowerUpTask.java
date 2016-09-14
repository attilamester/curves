import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class PowerUpTask {
	
	private JPanel panel;
	private Timer progressBar;
	private Timer powerUpEffect;
	private int time;
	
	public PowerUpTask(int time, JPanel panel, Callable<?> func) {
		this.panel = panel;
		this.time = time;
		
		startPanelLoading();
		
		this.powerUpEffect = new Timer(time, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					func.call();
				} catch (Exception e1) {}
			}
		});
		powerUpEffect.setRepeats(false);
		
		powerUpEffect.start();		
		progressBar.start();
		
	}
	
	private void startPanelLoading() {
		JProgressBar bar = new JProgressBar();
		bar.setOrientation(SwingConstants.VERTICAL);
		bar.setPreferredSize(new Dimension(4,20));
		bar.setBackground(Color.WHITE);
		bar.setForeground(Color.RED);
		bar.setBorderPainted(false);
		bar.setBorder(BorderFactory.createEmptyBorder());
		bar.setMaximum(100);
		bar.setValue(0);
		
		panel.add(bar);		
		panel.revalidate();
		
		
		progressBar = new Timer(time / 100, null);
		progressBar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (bar.getValue() == 100) {
					progressBar.stop();
					panel.remove(bar);
					panel.revalidate();
					panel.repaint();
					return;
				} 
				bar.setValue(bar.getValue() + 1);
			}
		});		
	}
}
