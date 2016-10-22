package curve;

import java.awt.Color;
import java.io.Serializable;

import curve_window.PlayerStatus;

public class Player implements Serializable {
	private static final long serialVersionUID = 1;
	
	private static int ID_COUNT = 0;
	
	private int id;
	private String name;
	private int score;
	private Color color;
	private Curve curve;
	private transient CurveController controller;
	private transient Control control;
	private boolean alive;
	private PlayerStatus playerStatusPane;
	
	public Player(String name, Color color, Curve curve, Control control) {
		this.id = Player.ID_COUNT++;
		this.name = name;
		this.color = color;
		this.score = 0;
		this.curve = curve;
		this.controller = new CurveController(curve);
		this.control = control;
		this.alive = true;
		this.playerStatusPane = new PlayerStatus(name, color);
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Curve getCurve() {
		return curve;
	}

	public void setCurve(Curve curve) {
		this.curve = curve;
	}

	public PlayerStatus getPlayerStatusPane() {
		return playerStatusPane;
	}

	public void setPlayerStatusPane(PlayerStatus playerStatusPane) {
		this.playerStatusPane = playerStatusPane;
	}

	public CurveController getController() {
		return controller;
	}

	public void setController(CurveController controller) {
		this.controller = controller;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	public boolean isDead() {
		return !alive;
	}
	
	public void increaseScore() {
		++this.score;
		this.playerStatusPane.increaseScore();
	}

	public Control getControl() {
		return control;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)	return false;
		if (obj == this)	return true;
		if (!(obj instanceof Player))	return false;
		Player p = (Player)obj;
		return (this.name.equals(p.getName()) && this.color.getRGB() == p.getColor().getRGB());
	}
	
	@Override
	public String toString() {
		return "Player: " + this.name + " - Color: " + this.color;
	}
	
	public void updateState(Player p) {
		this.setCurve(p.getCurve());
		this.setAlive(p.isAlive());
	}
}
