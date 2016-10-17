package modals;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import curve_window.CurveWindow;
import generals.GameController;
import generals.Main;

public class CountDownModal extends JDialog {
	private static final long serialVersionUID = 1;
	
	private CurveWindow curveWindow;
	private Container contentPane;
	private JLabel countDownLabel;
	
	private Color bg = new Color(38, 38, 38);
	
	public CountDownModal(CurveWindow curveWindow, int round, String winner, Color color) {
		this.curveWindow = curveWindow;
		
		this.contentPane = this.getContentPane();
		contentPane.setBackground(bg);
		
		addItems(winner, round, color);

		this.setSize(GameController.COUNT_DOWN_WIDTH, GameController.COUNT_DOWN_HEIGHT);
		this.setBounds(Main.SCREEN_WIDTH / 2 - this.getWidth() / 2, Main.SCREEN_HEIGHT / 2 - this.getHeight() / 2, this.getWidth(), this.getHeight());
		this.setUndecorated(true);
		curveWindow.setAlwaysOnTop(false);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		
		startCountDown(round);
	}
	
	private void addItems(String winner, int round, Color color) {
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
		nextRoundPane.setBackground(bg);
		nextRoundPane.add(roundLabel);
		nextRoundPane.add(countDownLabel);
		
		
		if (winner != null && winner.length() > 0) {
			String col = Main.getCssColor(color.getRGB());
			JPanel titlePane = new JPanel(new GridLayout(2, 1));
			titlePane.setBackground(bg);
			JLabel titleLabel_name = new JLabel("<html><span style='color: " + col + "'>" + winner + "</span></html>", SwingConstants.CENTER);
			titleLabel_name.setOpaque(false);
			titleLabel_name.setForeground(Color.WHITE);		
			titleLabel_name.setFont(new Font("Calibri", Font.BOLD, 20));
			JLabel titleLabel_status = new JLabel("wins this round", SwingConstants.CENTER);
			titleLabel_status.setOpaque(false);
			titleLabel_status.setForeground(Color.WHITE);		
			titleLabel_status.setFont(new Font("Calibri", Font.BOLD, 20));
			
			titlePane.add(titleLabel_name);
			titlePane.add(titleLabel_status);
			
			this.contentPane.add(titlePane, BorderLayout.NORTH);			
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
					//curveWindow.setAlwaysOnTop(true);
					curveWindow.getPlayGround().eraseArrows();					
					if (round == 1) {
						curveWindow.startGame();
						curveWindow.getPlayGround().getPowerUpLoader().start();
					}
					else
						curveWindow.restartGame();
				}
				countDownLabel.setText(Integer.toString(--nr));
			}
		});
		count.start();
	}
}
