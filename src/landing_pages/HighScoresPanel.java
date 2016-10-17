package landing_pages;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import generals.GameController;
import generals.Main;

public class HighScoresPanel extends JPanel {
	private static final long serialVersionUID = 1;
	
	
	public HighScoresPanel(int width, int height) {
		
		this.setLayout(new BorderLayout());		
		this.setSize(width, height);		
		
		addItems();
				
	}
	
	private void addItems() {
		Main.addBackPane(this);
		
		JPanel scorePanel = new JPanel();
		scorePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	
		try {
			addScores(scorePanel);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Could not read score-file");
		}
		
		JScrollPane scrollPane = new JScrollPane(scorePanel);
		scrollPane.setBorder(new CompoundBorder(new EmptyBorder(10,10,10,10), BorderFactory.createDashedBorder(null, 1, 10, 5, false)));
		this.add(scrollPane, BorderLayout.CENTER);
	}
	
	private void addScores(JPanel panel) throws IOException {
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		try {
			FileReader fr = new FileReader(GameController.SCORE_FILE_PATH);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				JLabel lineLabel = new JLabel(line);
				lineLabel.setFont(new Font("Courier New", Font.ITALIC, 15));
				panel.add(lineLabel);
			}
			br.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Could not find score-file");
		}
	}
}
