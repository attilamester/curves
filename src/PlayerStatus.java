import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayerStatus extends JPanel{
	
	private JLabel nameLabel;
	private JLabel scoreLabel;
	private int score;
	
	public PlayerStatus(String name, Color c) {
		setBackground(c);
		
		if (name.length() == 0)
			name = "#noName";
		nameLabel = new JLabel("<html><body style='text-shadow:1px 1px 5px black'>" + name + "</body></html>");
		//nameLabel.setMinimumSize(new Dimension(300, 30));
		nameLabel.setHorizontalAlignment(JLabel.LEFT);
		nameLabel.setVerticalAlignment(JLabel.CENTER);
		nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		nameLabel.setOpaque(true);
		nameLabel.setForeground(Color.WHITE);
		nameLabel.setBackground(c);
		nameLabel.setFont(new Font("Calibri", Font.BOLD, 15));
		
		scoreLabel = new JLabel("0 pt");
		scoreLabel.setFont(new Font("Calibri", Font.PLAIN, 15));
		scoreLabel.setMinimumSize(new Dimension(30, 30));
		scoreLabel.setVerticalAlignment(JLabel.CENTER);
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