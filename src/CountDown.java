import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class CountDown extends JFrame {
	
	private CurveWindow curveWindow;
	private Container contentPane;
	private JLabel countDownLabel;
	
	public CountDown(CurveWindow curveWindow, String label) {
		this.curveWindow = curveWindow;
		
		this.contentPane = this.getContentPane();
		contentPane.setBackground(GameController.PLAYGROUND_BACKGROUND);
		
		addItems(label);

		this.setSize(200, 200);
		this.setBounds(Main.SCREEN_WIDTH / 2 - this.getWidth() / 2, Main.SCREEN_HEIGHT / 2 - this.getHeight() / 2, this.getWidth(), this.getHeight());
		this.setUndecorated(true);
		curveWindow.setAlwaysOnTop(false);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		
		startCountDown();
	}
	
	private void addItems(String title) {
		ImageIcon icon = new ImageIcon("images\\loading10.gif");
		JLabel label = new JLabel(new ImageIcon( icon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT) ));
		countDownLabel = new JLabel("5", SwingConstants.CENTER);
		countDownLabel.setOpaque(false);
		countDownLabel.setForeground(Color.WHITE);
		countDownLabel.setFont(new Font("Calibri", Font.BOLD, 20));
		
		JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
		titleLabel.setOpaque(false);
		titleLabel.setForeground(Color.WHITE);		
		titleLabel.setFont(new Font("Calibri", Font.BOLD, 20)); 
		
		this.contentPane.add(titleLabel, BorderLayout.NORTH);
		this.contentPane.add(label, BorderLayout.CENTER);
		this.contentPane.add(countDownLabel, BorderLayout.SOUTH);
	}
	
	private void startCountDown() {
		Timer count = new Timer(1000, null);
		count.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int nr = Integer.parseInt(countDownLabel.getText());
				if (nr == 0) {
					count.stop();
					setVisible(false);
					dispose();
					
					curveWindow.getPlayGround().eraseArrows();					
					curveWindow.startGame();
				}
				countDownLabel.setText(Integer.toString(--nr));
			}
		});
		count.start();
	}
}
