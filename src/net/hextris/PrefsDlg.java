package net.hextris;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Shows a dialog with programm properties that can be changed
 * @author frank
 */

public class PrefsDlg extends JDialog
{
	Context ctx = Context.getContext();
	JPanel mainPN = new JPanel();
	JButton closeBT = new JButton();

	JComboBox sizeCB = new JComboBox();

	JButton moveLeftBT = new JButton("grab");
	JButton moveRightBT = new JButton("grab");
	JButton moveDownBT = new JButton("grab");
	JButton fallDownBT = new JButton("grab");
	JButton rotateLeftBT = new JButton("grab");
	JButton rotateRightBT = new JButton("grab");

	JTextField moveLeftJTF = new JTextField();
	JTextField moveRightJTF = new JTextField();
	JTextField moveDownJTF = new JTextField();
	JTextField fallDownJTF = new JTextField();
	JTextField rotateLeftJTF = new JTextField();
	JTextField rotateRightJTF = new JTextField();
	
	
	public PrefsDlg(Frame frame)
	{
		super(frame,"Hextris Preferences",true);
		this.initialize();
	}
	
	private void initialize()
	{
		this.getContentPane().add(mainPN,null);
		this.mainPN.setLayout(new GridBagLayout());
		this.mainPN.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.mainPN.setPreferredSize(new Dimension(250,250));
		
		int count = 0;
		this.mainPN.add(new JLabel("Hexagonsize: "),
				new GridBagConstraints(0, count, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0),
                        0, 0));
		this.sizeCB.setPreferredSize(new Dimension(50,26));
		this.mainPN.add(this.sizeCB,
				new GridBagConstraints(1, count, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0),
                        0, 0));
		for (int i = 2; i<10; i++) this.sizeCB.addItem(new Integer(i));
		this.sizeCB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sizeCB_actionPerformed(e);
			}
		});
		this.sizeCB.setSelectedIndex(((Integer)ctx.getProperty(Context.HEX_SIZE)).intValue()-1);
		count++;
		this.addKeyGrabber(count++,"move left: ", moveLeftBT, moveLeftJTF, Context.KEY_MOVE_LEFT, ctx.getKeys()[0]);
		this.addKeyGrabber(count++,"move right: ", moveRightBT, moveRightJTF, Context.KEY_MOVE_RIGHT, ctx.getKeys()[1]);
		this.addKeyGrabber(count++,"rotate left: ", rotateLeftBT, rotateLeftJTF, Context.KEY_ROTATE_LEFT, ctx.getKeys()[2]);
		this.addKeyGrabber(count++,"rotate right: ", rotateRightBT, rotateRightJTF, Context.KEY_ROTATE_RIGHT, ctx.getKeys()[3]);
		this.addKeyGrabber(count++,"move down: ", moveDownBT, moveDownJTF, Context.KEY_MOVE_DOWN, ctx.getKeys()[4]);
		this.addKeyGrabber(count++,"fall down: ", fallDownBT, fallDownJTF, Context.KEY_FALL_DOWN, ctx.getKeys()[5]);

		this.closeBT.setPreferredSize(new Dimension(120,26));
		this.closeBT.setText("Close");
		this.closeBT.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				closeBT_actionPerformed(e);
			}
		});
		this.mainPN.add(closeBT,
				new GridBagConstraints(0, 10, 10, 1, 1.0, 1.0,
						GridBagConstraints.SOUTHEAST,
						GridBagConstraints.NONE,
                        new Insets(5, 5, 0, 0),
                        0, 0));

		
		this.pack();
		
	}

	private void closeBT_actionPerformed(ActionEvent e)
	{
		ctx.savePersistProp();
		this.dispose();
	}

	private void sizeCB_actionPerformed(ActionEvent e)
	{
		int newSize = sizeCB.getSelectedIndex()+1;
		GamePanel.setHexSize(newSize);
	}

	/**
	 * adds widgets for configuring a key to the mainPanel
	 * @param pos
	 * @param label
	 * @param button
	 * @param jtf
	 * @param prop
	 * @param init
	 */
	private void addKeyGrabber(int pos, String label, JButton button, final JTextField jtf, final String prop, int init)
	{
		final JDialog _this = this;
		this.mainPN.add(new JLabel(label),
				new GridBagConstraints(0, pos, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0),
                        0, 0));
		jtf.setPreferredSize(new Dimension(50,26));
		this.mainPN.add(jtf,
				new GridBagConstraints(1, pos, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0),
                        0, 0));
		jtf.setText(KeyEvent.getKeyText(init));
		this.mainPN.add(button,
				new GridBagConstraints(2, pos, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0),
                        0, 0));
		//invoke the key-grabber
		button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				KeyGrabber kg = new KeyGrabber();
				kg.setLocationRelativeTo(_this);
				int keyId = kg.grab();
				if (keyId>=0) {
					jtf.setText(KeyEvent.getKeyText(keyId));
					ctx.put(prop, new Integer(keyId));
					Context.readKeys();
				}
			}
		});
		
	}
	

	/**
	 * a small modal dialog to grab keys
	 * disposes on key-press or lose-focus events
	 * @author felfe
	 *
	 */
	class KeyGrabber extends javax.swing.JDialog
	{
		JLabel l = new JLabel("press key");
		int key = -1;
		
		public KeyGrabber()
		{
			super();
			this.setContentPane(l);
			this.setModal(true);
			this.pack();
			this.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyPressed(java.awt.event.KeyEvent e) {
					key = e.getKeyCode();
					endGrab();
				}
			});
			this.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					endGrab();
				}
			});
		}
		
		/**
		 * displays the dialog and returns the grabbed key
		 * @return
		 */
		public int grab()
		{
			this.setVisible(true);
			return key;
		}
		
		public void endGrab()
		{
			this.dispose();
		}		
	}
	
}
