import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class EndGameModal extends JDialog {
	private static final long serialVersionUID = 1;
	
	private CurveWindow curveWindow;
	private Container contentPane;
	
	private Color bg = new Color(38, 38, 38);
	
	public EndGameModal(CurveWindow curveWindow, List<PlayerStatus> playerStatusPanes) {
		contentPane = this.getContentPane();
		contentPane.setLayout(new GridLayout(playerStatusPanes.size() + 2, 1));
		
		this.curveWindow = curveWindow;
		
		this.contentPane = this.getContentPane();
		contentPane.setBackground(bg);
		
		addItems(playerStatusPanes);
		
		this.setSize(GameController.COUNT_DOWN_WIDTH, GameController.COUNT_DOWN_HEIGHT);
		this.setBounds(Main.SCREEN_WIDTH / 2 - this.getWidth() / 2, Main.SCREEN_HEIGHT / 2 - this.getHeight() / 2, this.getWidth(), this.getHeight());
		this.setUndecorated(true);
		
		this.setAlwaysOnTop(true);
		this.setVisible(true);	
	}
	
	private void addItems(List<PlayerStatus> playerStatusPanes) {
		JPanel topPane = new JPanel();
		topPane.setOpaque(false);
		JLabel info = new JLabel("The game ended here :)", JLabel.CENTER);
		info.setOpaque(false);
		info.setForeground(Color.WHITE);
		info.setFont(new Font("Calibri", Font.BOLD, 15));
		
		JLabel info2 = new JLabel("Final score-board:", JLabel.LEFT);
		info2.setOpaque(false);
		info2.setForeground(Color.WHITE);
		info2.setFont(new Font("Calibri", Font.BOLD, 14));
		topPane.add(info);
		topPane.add(info2);
		
		contentPane.add(topPane);
		
		
		Collections.sort(playerStatusPanes);
		
		for (ListIterator<PlayerStatus> iter = playerStatusPanes.listIterator(); iter.hasNext();) {
			contentPane.add(iter.next());
		}
		
		JLabel end = new JLabel(new ImageIcon("images\\endBg.png"));
		end.setBackground(new Color(0,0,0,0));
		end.setOpaque(false);
		contentPane.add(end);
		
		end.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				dispose();
				curveWindow.dispose();
			}
		});
	}
}
