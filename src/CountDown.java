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

public class CountDown extends JFrame{
	
	CurveWindow curveWindow;
	Container contentPane;
	JLabel countDownLabel;
	
	public CountDown(CurveWindow curveWindow, String label) {
		this.curveWindow = curveWindow;
		
		contentPane = this.getContentPane();
		contentPane.setBackground(new Color(0, 0, 0, 0));
		//setBackground(new Color(0, 0, 0, 0));
		
		addItems(label);
		
		this.setContentPane(contentPane);
		this.setSize(170, 170);
		this.setBounds(Main.screenSize.width / 2 - this.getWidth() / 2, Main.screenSize.height / 2 - this.getHeight() / 2, this.getWidth(), this.getHeight());
		this.setUndecorated(true);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		
		startCountDown();
	}
	
	private void addItems(String title) {		
		JLabel label = new JLabel( new ImageIcon( new ImageIcon("images\\loading.gif").getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT) ));
		countDownLabel = new JLabel("5", SwingConstants.CENTER);
		//countDownLabel.setOpaque(true);
		countDownLabel.setForeground(Color.WHITE);
		countDownLabel.setBackground(new Color(0,0,0,1));
		countDownLabel.setFont(new Font("Calibri", Font.BOLD, 20));
		
		JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
		//titleLabel.setOpaque(true);
		titleLabel.setForeground(Color.WHITE);
		//titleLabel.setBackground(new Color(0,0,0,0));
		titleLabel.setFont(new Font("Calibri", Font.BOLD, 20)); 
		
		contentPane.add(titleLabel, BorderLayout.NORTH);
		contentPane.add(label, BorderLayout.CENTER);
		contentPane.add(countDownLabel, BorderLayout.SOUTH);
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
					curveWindow.getControl().startGame();
				}
				countDownLabel.setText(Integer.toString(--nr));
			}
		});
		count.start();
	}
}
