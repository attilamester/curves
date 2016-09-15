
public class Control {

	private int left;
	private int right;

	public Control (int left, int right) {
		this.left = left;
		this.right = right;
	}

	public int getLeft() {
		return this.left;
	}

	public int getRight() {
		return this.right;
	}
	
	public void swap() {
		int tmp = left;
		left = right;
		right = tmp;
	}
}