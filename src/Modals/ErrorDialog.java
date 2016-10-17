package modals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import generals.GameController;
import generals.Main;


public class ErrorDialog extends JDialog {
	private static final long serialVersionUID = 1;
	
	public ErrorDialog(String messageText) {
		Container pane = this.getContentPane();
		
		pane.setLayout(new BorderLayout());
		
		JLabel message = new JLabel(messageText, JLabel.CENTER);
		message.setForeground(Color.RED);
		
		JLabel ok = new JLabel("Ok", JLabel.CENTER);
		ok.setFont(new Font("Calibri", Font.BOLD, 20));
		ok.setBackground(GameController.PLAYGROUND_BACKGROUND);
		ok.setForeground(Color.WHITE);
		ok.setSize(new Dimension(20,20));
		ok.setPreferredSize(new Dimension(20,20));
		
		pane.add(message, BorderLayout.CENTER);
		pane.add(ok, BorderLayout.SOUTH);
		
		ok.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//setBackground(GameController.PLAYGROUND_BACKGROUND);
				dispose();
			}
		});			
		
		pane.setBackground(GameController.PLAYGROUND_BACKGROUND);
		this.getRootPane().setBorder(new LineBorder(Color.WHITE));
		this.setSize(GameController.ERROR_MODAL_WIDTH, GameController.ERROR_MODAL_HEIGHT);
		this.setBounds(Main.SCREEN_WIDTH / 2 - this.getWidth() / 2, Main.SCREEN_HEIGHT / 2 - this.getHeight() / 2, this.getWidth(), this.getHeight());
		this.setUndecorated(true);
		this.setAlwaysOnTop(true);
		this.setModal(true);
		this.setVisible(true);
	}
}
