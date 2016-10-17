package power_up;

public class PowerUp {
	
	private String name;
	private int x;
	private int y;
	
	public static final int POWERUP_SIZE = 50;
	public static final int POWERUP_RADIUS = 24;
	
	public PowerUp(String name, int x, int y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
}
