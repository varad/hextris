package net.hextris;

import java.awt.AWTEvent;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
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
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.util.ResourceBundle;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

/**
 * Shows a dialog with programm properties that can be changed
 * @author frank
 * @author Radek Varbuchta
 */
public class PrefsDlg extends JDialog {

    Context ctx = Context.getContext();
    JPanel prefsPanel = new JPanel();
    JRadioButton btnNormalSize;
    JRadioButton btnBigSize;
    private static ResourceBundle rb = java.util.ResourceBundle.getBundle("net/hextris/language");

    public PrefsDlg(Frame frame) {
        super(frame, rb.getString("Hextris preferences"), true);
        initialize();
    }

    /**
     * Initializes gui widgest.
     */
    private void initialize() {
        // preferences panel
        getContentPane().add(prefsPanel, null);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                closeActionPerformed(e);
            }
        });
        prefsPanel.setLayout(new GridBagLayout());
        prefsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // hexagons size
        prefsPanel.add(new JLabel(rb.getString("Hexagons size:")),
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0),
                0, 0));

        btnNormalSize = new JRadioButton(rb.getString("Normal"), ctx.getHexSize() == Context.HexSize.NORMAL);
        btnBigSize = new JRadioButton(rb.getString("Big"), ctx.getHexSize() == Context.HexSize.BIG);
        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(btnNormalSize);
        btnGroup.add(btnBigSize);
        prefsPanel.add(btnNormalSize);
        prefsPanel.add(btnBigSize);
        btnNormalSize.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                hexSizeActionPerformed(e);
            }
        });
        btnBigSize.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                hexSizeActionPerformed(e);
            }
        });

        // keys
        int position = 2;
        addKeyGrabber(position++, rb.getString("Move left:"),  Context.Key.MOVE_LEFT);
        addKeyGrabber(position++, rb.getString("Move right:"),  Context.Key.MOVE_RIGHT);
        addKeyGrabber(position++, rb.getString("Rotate left:"), Context.Key.ROTATE_LEFT);
        addKeyGrabber(position++, rb.getString("Rotate right:"), Context.Key.ROTATE_RIGHT);
        addKeyGrabber(position++, rb.getString("Move down:"), Context.Key.MOVE_DOWN);
        addKeyGrabber(position++, rb.getString("Fall down:"),  Context.Key.FALL_DOWN);

        // buttons
        JButton btnClose = new JButton(rb.getString("Close"));
        btnClose.setPreferredSize(new Dimension(120, 26));
        btnClose.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                closeActionPerformed(e);
            }
        });
        prefsPanel.add(btnClose,
                new GridBagConstraints(0, 10, 10, 1, 1.0, 1.0,
                GridBagConstraints.SOUTH,
                GridBagConstraints.NONE,
                new Insets(5, 5, 0, 0),
                0, 0));

        pack();
    }

    /**
     * Saves settings.
     * @param source event
     */
    private void closeActionPerformed(AWTEvent e) {
        dispose();
    }

    /**
     * Sets new hexagons size.
     * @param source event
     */
    private void hexSizeActionPerformed(ActionEvent e) {
        Context.HexSize size = btnNormalSize.isSelected() ? Context.HexSize.NORMAL : Context.HexSize.BIG;
        ctx.put(Context.Property.HEX_SIZE, size.toString());
        GamePanel.setHexSize(size);
    }

    /**
     * Adds widgets for configuring a key to the main panel.
     * @param position
     * @param label
     * @param key name
     * @param value
     */
    private void addKeyGrabber(int pos, String label, final Context.Key key) {
        final JDialog _this = this;
        final JTextField jtf = new JTextField();
        JButton button = new JButton(rb.getString("Grab"));
        prefsPanel.add(new JLabel(label),
                new GridBagConstraints(0, pos, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0),
                0, 0));
        jtf.setPreferredSize(new Dimension(50, 26));
        prefsPanel.add(jtf,
                new GridBagConstraints(1, pos, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0),
                0, 0));
        jtf.setText(KeyEvent.getKeyText(Integer.valueOf(ctx.getKeyValue(key))));
        prefsPanel.add(button,
                new GridBagConstraints(2, pos, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 0, 0),
                0, 0));

        //invoke the key-grabber
        button.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                KeyGrabber kg = new KeyGrabber();
                kg.setLocationRelativeTo(_this);
                int keyId = kg.grab();
                if (keyId >= 0) {
                    jtf.setText(KeyEvent.getKeyText(keyId));
                    ctx.put(key, new Integer(keyId));
                }
            }
        });

    }

    /**
     * A small modal dialog to grab keys.
     * Disposes on key-press or lose-focus events.
     * @author felfe
     */
    class KeyGrabber extends javax.swing.JDialog {
        JLabel lbl = new JLabel(rb.getString("Press key"));
        int key = -1;

        private KeyGrabber() {
            super();
            setContentPane(lbl);
            setModal(true);
            pack();
            addKeyListener(new java.awt.event.KeyAdapter() {

                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {
                    key = e.getKeyCode();
                    endGrab();
                }
            });
            addFocusListener(new java.awt.event.FocusAdapter() {

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    endGrab();
                }
            });
        }

        /**
         * Displays the dialog and returns the grabbed key.
         * @return
         */
        public int grab() {
            setVisible(true);
            return key;
        }

        /**
         * 
         */
        public void endGrab() {
            dispose();
        }
    }
}
