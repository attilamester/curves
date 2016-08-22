import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayerStatus extends JPanel{
	
	private JLabel nameLabel;
	private JLabel scoreLabel;
	private int score;
	
	public PlayerStatus(String name, Color c) {
		if (name.length() == 0)
			name = "#noName";
		nameLabel = new JLabel(name);
		nameLabel.setMinimumSize(new Dimension(300, 30));
		nameLabel.setHorizontalAlignment(JLabel.LEFT);
		nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		nameLabel.setOpaque(true);
		nameLabel.setBackground(c);
		nameLabel.setForeground(Color.WHITE);
		
		scoreLabel = new JLabel("0 pt");
		this.add(nameLabel);
		this.add(scoreLabel);
	}

	public JLabel getNameLabel() {
		return nameLabel;
	}

	public void setNameLabel(JLabel nameLabel) {
		this.nameLabel = nameLabel;
	}

	public JLabel getScoreLabel() {
		return scoreLabel;
	}

	public void setScoreLabel(JLabel scoreLabel) {
		this.scoreLabel = scoreLabel;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}