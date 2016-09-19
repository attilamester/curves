import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayerStatus extends JPanel implements Comparable {
	private static final long serialVersionUID = 1;
	
	private JLabel nameLabel;
	private JLabel scoreLabel;
	private int score;
	
	public PlayerStatus(String name, Color c) {
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());
		setBackground(c);
		
		if (name.length() == 0)
			name = "#noName";
		score = 0;
		
		nameLabel = new JLabel(name);
		nameLabel.setHorizontalAlignment(JLabel.LEFT);
		nameLabel.setVerticalAlignment(JLabel.CENTER);		
		nameLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 0, 10));
		nameLabel.setForeground(Color.WHITE);
		nameLabel.setFont(new Font("Calibri", Font.BOLD, 20));
		
		scoreLabel = new JLabel("0 pt");
		scoreLabel.setFont(new Font("Calibri", Font.BOLD, 20));		
		scoreLabel.setVerticalAlignment(JLabel.CENTER);
		scoreLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 0, 10));
		scoreLabel.setForeground(Color.WHITE);
		
		this.add(nameLabel, gbc);
		this.add(scoreLabel, gbc);
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
		this.scoreLabel.setText(score + " pt");		
	}
	public void increaseScore() {
		this.setScore(this.getScore() + 1);
	}

	@Override
	public int compareTo(Object obj) {	
		return this.score - ((PlayerStatus) obj).getScore();
	}
}