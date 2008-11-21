package net.hextris;

import java.awt.Color;
import javax.swing.*;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;

/**
 * Main Hextris window.
 * @author fränk
 * @author Radek Varbuchta
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = -2432811889021456787L;
    private static ResourceBundle rb = java.util.ResourceBundle.getBundle("net/hextris/language");
    private Hextris hextris = null;
    private Context ctx = Context.getContext();

    public MainFrame(Hextris h) {
        super(rb.getString("appName"));
        this.hextris = h;
        initialize();
    }

    /**
     * Create widgets and stuff.
     */
    public void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setContentPane(this.hextris);
        this.setResizable(false);
        this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

        // menubar
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu(rb.getString("Game"));
        gameMenu.setMnemonic('g');
        JMenu helpMenu = new JMenu(rb.getString("Help"));
        JMenuItem newGameMI = new JMenuItem(rb.getString("New_Game"));
        JMenuItem newDemoMI = new JMenuItem(rb.getString("Demo"));
        //---JMenuItem highScoresMI = new JMenuItem(rb.getString("Highscores"));
        JMenuItem prefsMI = new JMenuItem(rb.getString("Preferences"));
        JMenuItem exitMI = new JMenuItem(rb.getString("Quit"));
        JMenuItem gameInfo = new JMenuItem(rb.getString("About"));
        JMenuItem gameHelp = new JMenuItem(rb.getString("Help"));
        gameMenu.add(newGameMI);
        gameMenu.add(newDemoMI);
        //---gameMenu.add(highScoresMI);
        gameMenu.add(prefsMI);
        gameMenu.add(exitMI);
        helpMenu.add(gameInfo);
        helpMenu.add(gameHelp);
        menuBar.add(gameMenu);
        menuBar.add(helpMenu);
        newGameMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.Event.ALT_MASK, false));
        newGameMI.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                hextris.newGame(false, true);
            }
        });
        newDemoMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.Event.ALT_MASK, false));
        newDemoMI.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                hextris.newGame(true, true);
            }
        });
        /*---highScoresMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.Event.ALT_MASK, false));
        highScoresMI.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
        hextris.showHighScores();
        }
        });*/
        prefsMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.Event.ALT_MASK, false));
        prefsMI.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showGameProperties();
            }
        });
        exitMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.Event.ALT_MASK, false));
        exitMI.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
        gameInfo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showGameInfo();
            }
        });
        gameHelp.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showGameHelp();
            }
        });

        this.hextris.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                hextris.gameKeyPressed(e);
            }
        });


        this.setJMenuBar(menuBar);
        this.hextris.setFocusable(true);
        this.hextris.setRequestFocusEnabled(true);
        //this.hextris.highScores.read();
        this.hextris.setBackground(Color.BLACK);

        this.setIconImage(Toolkit.getDefaultToolkit().createImage(this.getClass().getResource("/net/hextris/images/hextris_icon.gif")));

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void dispose() {
        super.dispose();
        hextris.setMoverThread(null);
    }

    /**
     * Bring up the properties dialog.
     */
    private void showGameProperties() {
        PrefsDlg dlg = new PrefsDlg(JOptionPane.getFrameForComponent(this));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        this.pack();
    }

    /**
     * Show a little info dialog.
     */
    private void showGameInfo() {
        JTextArea jta = new JTextArea("Tetris with Hexagons\n\n" +
                "written by Frank Felfe\n\n" +
                "based on an idea by David Markley\n" +
                "and code from Java-Tetris by Christian Schneider\n\n" +
                "released under the terms of the GNU General Public License");
        jta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(this),
                jta,
                "Hextris " + Hextris.getVersion(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * show a little help window
     *
     */
    private void showGameHelp() {
        JTextArea jta = new JTextArea(
                "Please visit the hextris homepage at\n" +
                "http://hextris.inner-space.de/\n\n" +
                "Keys:\n" +
                "move left - " + KeyEvent.getKeyText(ctx.getKeyValue(Context.Key.MOVE_LEFT)) + " or Left\n" +
                "move right - " + KeyEvent.getKeyText(ctx.getKeyValue(Context.Key.MOVE_RIGHT)) + " or Right\n" +
                "rotate clockwise - " + KeyEvent.getKeyText(ctx.getKeyValue(Context.Key.ROTATE_RIGHT)) + " or Up\n" +
                "rotate counterclockwise - " + KeyEvent.getKeyText(ctx.getKeyValue(Context.Key.ROTATE_LEFT)) + "\n" +
                "move down - " + KeyEvent.getKeyText(ctx.getKeyValue(Context.Key.MOVE_DOWN)) + " or Down\n" +
                "fall down - " + KeyEvent.getKeyText(ctx.getKeyValue(Context.Key.FALL_DOWN)) + " or Space\n\n" //"Score:\n" +
                //"move down - level x severity\n" +
                //"fall down - level x severity x 2 x lines\n" +
                //"stone - 10 x level x severity\n" +
                //"line removed - 100 x level x severity x lines"
                );
        jta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(this),
                jta,
                "Hextris Help",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exit() {
        System.exit(0);
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            exit();
        }
    }

    public void setHextris(Hextris h) {
        this.hextris = h;
    }
} 
