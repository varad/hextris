package net.hextris;

import javax.swing.JApplet;
import java.awt.event.KeyEvent;

/**
 * Starting point for use of hextris as an applet. This class simply loads the
 * Hextris panel class as it's content pane
 * 
 * @author fränk
 * @author Radek Varbuchta
 */
public class HextrisApplet extends JApplet {

	private static final long serialVersionUID = -8291800152310127713L;
	private Hextris hextris = null;

	public HextrisApplet() {
		super();
	}

	@Override
	public void init() {
		hextris = getHextris();
		setContentPane(hextris);
		setSize(hextris.getPreferredSize());

		hextris.addKeyListener(new java.awt.event.KeyAdapter() {

			@Override
			public void keyPressed(java.awt.event.KeyEvent e) {
				gameKeyPressed(e);
			}
		});

	}

	@Override
	public void stop() {
		super.stop();
		hextris.gameOver();
	}

	private Hextris getHextris() {
		if (hextris == null) {
			hextris = new Hextris(Hextris.APPLET);
			hextris.setFocusable(true);
			hextris.setRequestFocusEnabled(true);
		}
		return hextris;
	}

	private void gameKeyPressed(KeyEvent e) {
		int kc = e.getKeyCode();
		int km = e.getModifiers();

		if (kc == KeyEvent.VK_N && (km & KeyEvent.ALT_MASK) != 0) {
			getHextris().newGame(false, true);
		} else if (kc == KeyEvent.VK_D && (km & KeyEvent.ALT_MASK) != 0) {
			getHextris().newGame(true, true);
		} else {
			getHextris().gameKeyPressed(e);
		}
	}
}
