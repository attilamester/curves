import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class CountDownModal extends JDialog {
	
	private CurveWindow curveWindow;
	private Container contentPane;
	private JLabel countDownLabel;
	
	public CountDownModal(CurveWindow curveWindow, int round, String winner) {
		this.curveWindow = curveWindow;
		
		this.contentPane = this.getContentPane();
		contentPane.setBackground(GameController.PLAYGROUND_BACKGROUND);
		
		addItems(winner, round);

		this.setSize(200, 200);
		this.setBounds(Main.SCREEN_WIDTH / 2 - this.getWidth() / 2, Main.SCREEN_HEIGHT / 2 - this.getHeight() / 2, this.getWidth(), this.getHeight());
		this.setUndecorated(true);
		curveWindow.setAlwaysOnTop(false);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		
		startCountDown(round);
	}
	
	private void addItems(String winner, int round) {
		ImageIcon icon = new ImageIcon("images\\loading.gif");
		JLabel loading = new JLabel(new ImageIcon( icon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT) ));
		JLabel roundLabel = new JLabel("Round " + Integer.toString(round) + " in ");
		roundLabel.setOpaque(false);
		roundLabel.setForeground(Color.WHITE);
		roundLabel.setFont(new Font("Calibri", Font.BOLD, 20));
		
		countDownLabel = new JLabel("3", SwingConstants.CENTER);
		countDownLabel.setOpaque(false);
		countDownLabel.setForeground(Color.RED);
		countDownLabel.setFont(new Font("Calibri", Font.BOLD, 20));
		
		JPanel nextRoundPane = new JPanel();
		nextRoundPane.setBackground(GameController.PLAYGROUND_BACKGROUND);
		nextRoundPane.add(roundLabel);
		nextRoundPane.add(countDownLabel);
		
		
		if (winner != null && winner.length() > 0) {
			JLabel titleLabel = new JLabel("<html><span style='color:red'>" + winner + "</span> wins this round</html>", SwingConstants.CENTER);
			titleLabel.setOpaque(false);
			titleLabel.setForeground(Color.WHITE);		
			titleLabel.setFont(new Font("Calibri", Font.BOLD, 20));
			this.contentPane.add(titleLabel, BorderLayout.NORTH);
		}
		this.contentPane.add(loading, BorderLayout.CENTER);
		this.contentPane.add(nextRoundPane, BorderLayout.SOUTH);
	}
	
	private void startCountDown(int round) {
		Timer count = new Timer(1000, null);
		count.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int nr = Integer.parseInt(countDownLabel.getText());
				if (nr == 0) {
					count.stop();
					setVisible(false);
					dispose();
					curveWindow.setAlwaysOnTop(true);
					curveWindow.getPlayGround().eraseArrows();					
					if (round == 1)
						curveWindow.startGame();
					else
						curveWindow.restartGame();
				}
				countDownLabel.setText(Integer.toString(--nr));
			}
		});
		count.start();
	}
}
