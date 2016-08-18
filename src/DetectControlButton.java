import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;

public  class DetectControlButton extends JButton implements FocusListener {

	private String label;
	private boolean listening;
	private int code;

	public DetectControlButton(String label) {
		super(label);
		this.label = label;

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (listening) {
					code = e.getKeyCode();
					setText(Integer.toString(code));
				}
			}
		});
	}

	@Override
	public void focusGained(FocusEvent e) {
		listening = true;
	}

	@Override
	public void focusLost(FocusEvent e) {
		listening = false;
	}

	public int getCode() {
		return this.code;
	}
}