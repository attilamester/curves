package modals;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import curve.Player;
import curve_window.CurveWindow;
import generals.GameController;
import generals.Main;

public class EndGameModal extends JDialog {
	private static final long serialVersionUID = 1;
	
	private CurveWindow curveWindow;
	private Container contentPane;
	
	private Color bg = new Color(38, 38, 38);
	
	public EndGameModal(CurveWindow curveWindow, List<Player> players/*Map<String, Integer> namesScores, Map<String, PlayerStatus> playerStatusPanes*/) {
		contentPane = this.getContentPane();
		contentPane.setLayout(new GridLayout(players.size() + 4, 1));
		
		this.curveWindow = curveWindow;
		
		this.contentPane = this.getContentPane();
		contentPane.setBackground(bg);
		
		//Collections.sort(playerStatusPanes,  Collections.reverseOrder());
		addItems(players);
		
		Rectangle r = Main.getGameController().getCurveWindow().getBounds();
		this.setSize(GameController.COUNT_DOWN_WIDTH, players.get(0).getPlayerStatusPane().getHeight() * (players.size() + 4));
		this.setBounds((int)(r.getCenterX() - this.getWidth() / 2), (int)(r.getCenterY() - this.getHeight() / 2), this.getWidth(), this.getHeight());
		this.setUndecorated(true);
		
		this.setAlwaysOnTop(true);
		this.setVisible(true);
	}
	
	private void addItems(List<Player> players/*Map<String, Integer> namesScores, Map<String, PlayerStatus> playerStatusPanes*/) {
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
		
		/*
		List<Map.Entry<String, Integer>> orderedNamesScores =
	            new LinkedList<Map.Entry<String, Integer>>(namesScores.entrySet());
        Collections.sort(orderedNamesScores, new Comparator<Map.Entry<String, Integer>>() {
        	@Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );
        
		for (ListIterator<Map.Entry<String, Integer>> iter = orderedNamesScores.listIterator(); iter.hasNext();) {
			contentPane.add(playerStatusPanes.get(iter.next().getKey()));
		}
		*/
		List<Player> orderedPlayers = new ArrayList<>(players);
		Collections.sort(orderedPlayers, new Comparator<Player>() {
			@Override
			public int compare(Player p1, Player p2) {
				return p1.getScore() - p2.getScore();
			}
		});
		for (Player p : orderedPlayers) {
			contentPane.add(p.getPlayerStatusPane());
		}
		
		
		JCheckBox check = new JCheckBox("Save this score");
		check.setBackground(GameController.PLAYGROUND_BACKGROUND);
		check.setForeground(Color.WHITE);
		check.setFocusPainted(false);
		
		contentPane.add(check);
		
		JLabel end = new JLabel(new ImageIcon(this.getClass().getResource("/endBg.png")));
		end.setSize(new Dimension(100, 50));
		end.setBackground(new Color(0,0,0,0));
		end.setOpaque(false);
		contentPane.add(end);
		
		end.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				dispose();
				if (check.isSelected()) {
					saveScore(orderedPlayers);
				}
				curveWindow.dispose();
			}
		});
	}
	
	private void saveScore(List<Player> orderedPlayers/*List<Map.Entry<String, Integer>> orderedNamesScores*/) {
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
			
			/*for (ListIterator<Map.Entry<String, Integer>> iter = orderedNamesScores.listIterator(); iter.hasNext();) {
				Map.Entry<String, Integer> ref = iter.next();
				String line = new String(ref.getKey() + " - " + ref.getValue()) + "\n";
				bw.write(line);
			}*/
			for (Player p : orderedPlayers) {
				String line = new String(p.getName() + " - " + p.getScore() + "\n");
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
