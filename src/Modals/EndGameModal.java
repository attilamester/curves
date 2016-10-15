package Modals;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import CurveWindow.CurveWindow;
import CurveWindow.PlayerStatus;
import Generals.GameController;
import Generals.Main;

public class EndGameModal extends JDialog {
	private static final long serialVersionUID = 1;
	
	private CurveWindow curveWindow;
	private Container contentPane;
	
	private Color bg = new Color(38, 38, 38);
	
	public EndGameModal(CurveWindow curveWindow, List<PlayerStatus> playerStatusPanes) {
		contentPane = this.getContentPane();
		contentPane.setLayout(new GridLayout(playerStatusPanes.size() + 4, 1));
		
		this.curveWindow = curveWindow;
		
		this.contentPane = this.getContentPane();
		contentPane.setBackground(bg);
		
		Collections.sort(playerStatusPanes,  Collections.reverseOrder());
		addItems(playerStatusPanes);
		
		this.setSize(GameController.COUNT_DOWN_WIDTH, playerStatusPanes.get(0).getHeight() * (playerStatusPanes.size() + 4));
		this.setBounds(Main.SCREEN_WIDTH / 2 - this.getWidth() / 2, Main.SCREEN_HEIGHT / 2 - this.getHeight() / 2, this.getWidth(), this.getHeight());
		this.setUndecorated(true);
		
		this.setAlwaysOnTop(true);
		this.setVisible(true);	
	}
	
	private void addItems(List<PlayerStatus> playerStatusPanes) {
		JLabel info = new JLabel("The game ended here :)", JLabel.CENTER);
		info.setOpaque(false);
		info.setForeground(Color.WHITE);
		info.setFont(new Font("Calibri", Font.BOLD, 15));
		
		JLabel info2 = new JLabel("Final score-board:", JLabel.LEFT);
		info2.setOpaque(false);
		info2.setForeground(Color.WHITE);
		info2.setFont(new Font("Calibri", Font.BOLD, 14));
		contentPane.add(info);
		contentPane.add(info2);
		
		for (ListIterator<PlayerStatus> iter = playerStatusPanes.listIterator(); iter.hasNext();) {
			contentPane.add(iter.next());
		}
		
		JCheckBox check = new JCheckBox("Save this score");
		check.setBackground(GameController.PLAYGROUND_BACKGROUND);
		check.setForeground(Color.WHITE);
		check.setFocusPainted(false);
		
		contentPane.add(check);
		
		JLabel end = new JLabel(new ImageIcon("images\\endBg.png"));
		end.setSize(new Dimension(100, 50));
		end.setBackground(new Color(0,0,0,0));
		end.setOpaque(false);
		contentPane.add(end);
		
		end.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				dispose();
				if (check.isSelected()) {
					saveScore(playerStatusPanes);
				}
				curveWindow.dispose();
			}
		});
	}
	
	private void saveScore(List<PlayerStatus> playerStatusPanes) {
		File f = new File(GameController.SCORE_FILE_PATH);
		if(!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Could not create score-file");
				return;
			}
		}
		try {
			FileWriter bw = new FileWriter(f, true);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			bw.write("\n" + dateFormat.format(new Date()) + "\n");
			for (ListIterator<PlayerStatus> iter = playerStatusPanes.listIterator(); iter.hasNext();) {
				PlayerStatus ref = iter.next();
				String line = new String(ref.getName() + " - " + ref.getScore()) + "\n";
				bw.write(line);
			}
			bw.flush();
			bw.close();
		}
		catch(FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Could not read score-file");
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error while reading score-file");
		}
	}
}
