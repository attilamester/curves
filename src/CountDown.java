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
	
	Container contentPane;
	JLabel countDownLabel;
	
	public CountDown() {
		contentPane = this.getContentPane();
		contentPane.setBackground(new Color(38, 38, 38));
		
		addItems();
		
		this.setContentPane(contentPane);
		this.setSize(300, 170);
		//this.setBounds(Main.screenSize.width / 2 - this.getWidth() / 2, Main.screenSize.height / 2 - this.getHeight() / 2, this.getWidth(), this.getHeight());
		this.setUndecorated(true);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		
		startCountDown();
	}
	
	private void addItems() {
		ImageIcon img = new ImageIcon("..\\loading.gif");		
		JLabel label = new JLabel( new ImageIcon( new ImageIcon("..\\loading2.gif").getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT) ));
		countDownLabel = new JLabel("5", SwingConstants.CENTER);
		countDownLabel.setOpaque(true);
		countDownLabel.setForeground(Color.WHITE);
		countDownLabel.setBackground(new Color(0,0,0,0));
		countDownLabel.setFont(new Font("Calibbri", Font.BOLD, 20));
		
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
				}
				countDownLabel.setText(Integer.toString(--nr));
			}
		});
		count.start();
	}
}
