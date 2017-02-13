package curve;

import java.io.Serializable;

public class Direction implements Serializable {
	private static final long serialVersionUID = 1;
	
	private double i;
	private double j;
	
	public Direction(double i, double j) {		
		this.i = i;
		this.j = j;		
	}
	
	public Direction() {}
	
	public Direction(Direction direction) {
		this.i = direction.i;
		this.j = direction.j;
	}

	@Override
	public String toString() {
		return "Direction [i=" + i + ", j=" + j + "]";
	}

	public double getI() {
		return i;
	}
	public void setI(double i) {
		this.i = i;
	}
	public double getJ() {
		return j;
	}
	public void setJ(double j) {
		this.j = j;
	}
	
}
