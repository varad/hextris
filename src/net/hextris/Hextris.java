package net.hextris;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;

/**
 * Hextris main class
 * 
 * Implements the Controller part of the Hextris game.
 * Reacts on keyboard input and starts a thread for
 * moving the stone down after a certain amount of time.
 *
 * @author fränk
 * @author Radek Varbuchta
 */
public class Hextris extends JPanel implements Runnable {

    private static final long serialVersionUID = -3267887732569843668L;
    private static ResourceBundle rb = java.util.ResourceBundle.getBundle("net/hextris/language");
    private static final String version = rb.getString("version");
    private static final int NONE = 0;
    private static final int MOVE_DOWN = 1;
    private static final int FALL_DOWN = 2;
    private int action = NONE;
    private AtomicBoolean paused = new AtomicBoolean(false);
    private Context ctx = Context.getContext();
    private GamePanel playPanel = null;
    private GamePanel previewPanel = null;
    private JLabel levelLabel = new JLabel("");
    private JLabel stonesLabel = new JLabel("");
    private JLabel linesLabel = new JLabel("");
    private Object[] startMsg = null;
    private JComboBox severityCB = null;
    private JComboBox levelCB = null;
    private JButton buttonStart = null;
    private JButton buttonPause = null;
    private JButton buttonDemo = null;
    private JButton buttonHighscore = null;
    private Thread moverThread;
    private Stone currentStone;
    private Stone nextStone;
    private int lines;
    private int stones;
    private int level;
    private int severity;
    private boolean gameOver;
    private boolean demo = false;

    public Hextris() {
        super();
        initialize();
    }

    /**
     * moverThread to move the stone down after a certain amount of time
     * handles move down and fall down requests
     */
    public void run() {
        while (moverThread == Thread.currentThread()) {
            try {
                Thread.sleep(1800 / (level + 1) - 100);
            } catch (InterruptedException ex) {
                //System.out.println("interrupted");
            }

            if (paused.get()) {
                continue;
            }


            if (gameOver || this.currentStone == null) {
                continue;
            }

            switch (this.action) {
                case FALL_DOWN:
                    this.fallDown();
                    break;
                case MOVE_DOWN:
                case NONE:
                    moveDown();
                    break;
                default:
                    System.out.println("no action: " + this.action);
                    break;
            }
            this.action = NONE;
        }
    }

    /**
     * Initialize widgets and stuff.
     */
    private void initialize() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setLayout(new GridBagLayout());
        if (playPanel == null) {
            playPanel = new GamePanel(15, 27);
        }
        this.add(playPanel,
                new GridBagConstraints(0, 0, 1, 10, 0.0, 0.0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 10),
                0, 0));

        if (previewPanel == null) {
            previewPanel = new GamePanel(6, 6);
        }
        previewPanel.setBackground(Color.BLACK);
        JLabel nextLabel = new JLabel(rb.getString("Next:"));
        this.add(nextLabel,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE,
                new Insets(10, 0, 0, 10),
                0, 0));
        this.add(previewPanel,
                new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 10),
                0, 0));

        this.add(levelLabel,
                new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE,
                new Insets(10, 0, 0, 10),
                0, 0));

        this.add(stonesLabel,
                new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE,
                new Insets(5, 0, 0, 10),
                0, 0));

        this.add(linesLabel,
                new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE,
                new Insets(5, 0, 0, 10),
                0, 0));

        //labels appearance
        Font font = new Font(this.getFont().getName(), this.getFont().getStyle() | Font.BOLD, 12);
        nextLabel.setFont(font);
        nextLabel.setForeground(Color.WHITE);
        levelLabel.setFont(font);
        levelLabel.setForeground(Color.WHITE);
        stonesLabel.setFont(font);
        stonesLabel.setForeground(Color.WHITE);
        linesLabel.setFont(font);
        linesLabel.setForeground(Color.WHITE);

        //buttons
        this.buttonStart = new JButton(rb.getString("Start"));
        this.add(this.buttonStart,
                new GridBagConstraints(1, 6, 1, 1, 0.0, 1.0,
                GridBagConstraints.SOUTHWEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(10, 0, 0, 10),
                0, 0));
        this.buttonStart.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                newGame(false, false);
            }
        });
        this.buttonPause = new JButton(rb.getString("Pause"));
        this.add(this.buttonPause,
                new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0,
                GridBagConstraints.SOUTHWEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(10, 0, 0, 10),
                0, 0));
        this.buttonPause.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                pause();
            }
        });
        this.buttonDemo = new JButton(rb.getString("Demo"));
        this.add(this.buttonDemo,
                new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(10, 0, 0, 10),
                0, 0));
        this.buttonDemo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                newGame(true, true);
            }
        });

        this.buttonHighscore = new JButton(rb.getString("Highscores"));
        this.add(this.buttonHighscore,
                new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(10, 0, 10, 10),
                0, 0));
        final Hextris _this = this;
        this.buttonHighscore.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showHighScore();
            }
        });


        this.setVisible(true);
        this.setName("HextrisPane");

        playPanel.getBoard().drawPlayField();
        gameOver = true;
        setFocusable(true);

        newGame(false, false);
    }

    /**
     * reacts on keystrokes and moves the stone left, right and down.
     * rotates the stone.
     * configured keys have higher priority
     *
     * @param e
     */
    protected void gameKeyPressed(KeyEvent e) {
        int kc = e.getKeyCode();

        if (gameOver || demo) {
            return;
        }
        if (this.currentStone == null) {
            return;
        }

        if (kc == ctx.getKeyValue(Context.Key.MOVE_LEFT)) {
            this.currentStone.moveStone(Stone.MOVE_LEFT);
            playPanel.repaint();
        } else if (kc == ctx.getKeyValue(Context.Key.MOVE_RIGHT)) {
            this.currentStone.moveStone(Stone.MOVE_RIGHT);
            playPanel.repaint();
        } else if (kc == ctx.getKeyValue(Context.Key.ROTATE_LEFT)) {
            this.currentStone.moveStone(Stone.ROTATE_LEFT);
            playPanel.repaint();
        } else if (kc == ctx.getKeyValue(Context.Key.ROTATE_RIGHT)) {
            this.currentStone.moveStone(Stone.ROTATE_RIGHT);
            playPanel.repaint();
        } else if (kc == ctx.getKeyValue(Context.Key.MOVE_DOWN)) {
            if (this.action == NONE) {
                this.action = MOVE_DOWN;
                moverThread.interrupt();
            }
        } else if (kc == ctx.getKeyValue(Context.Key.FALL_DOWN)) {
            this.action = FALL_DOWN;
            if (moverThread != null) {
                moverThread.interrupt();
            }
        }
    }

    /**
     * Starts a new game
     *
     * Resets the score, clears the board and creates a new stone.
     * Starts the moverThread.
     */
    public void newGame(boolean demo, boolean showOptions) {
        if (demo) {
            severity = 1;
            setLevel(7);
        } else {
            if (showOptions) {
                int option = JOptionPane.showOptionDialog(JOptionPane.getFrameForComponent(this),
                        getStartMsg(),
                        rb.getString("New_Game"), JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null,
                        new String[]{rb.getString("Cancel"), rb.getString("Start")}, rb.getString("Start"));
                grabFocus();
                if (option != 1) {
                    return;
                }
                severity = severityCB.getSelectedIndex();
                setLevel(levelCB.getSelectedIndex() + 1);
            } else {
                // default options
                this.severity = 1;
                setLevel(1);
            }

        }

        this.demo = demo;
        setStones(0);
        setLines(0);
        if (nextStone != null) {
            nextStone.place(false);
        }
        playPanel.getBoard().drawPlayField();
        playPanel.setGameOver(false);
        nextStone = new Stone(previewPanel.getBoard(), severity);
        createNextStone();
        playPanel.repaint();
        gameOver = false;
        setPaused(false);
        moverThread = new Thread(this);
        moverThread.start();
        this.grabFocus();
    }

    /**
     * Creates a new random stone and places it at the top of the board.
     */
    private void createNextStone() {
        currentStone = new Stone(nextStone, playPanel.getBoard());
        currentStone.setPosition((this.playPanel.getPanelWidth() - 5) / 2, -1);

        if (currentStone.mayPlace(currentStone.getPosition().x, currentStone.getPosition().y)) {
            nextStone.place(false);
            nextStone = new Stone(previewPanel.getBoard(), severity);
            nextStone.setPosition(0, 0);
            nextStone.place(true);
            currentStone.place(true);
            previewPanel.repaint();
            playPanel.repaint();
            if (this.demo) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException iex) {
                }
                this.currentStone.place(false);
                int[] bp = this.currentStone.getBestPosition();
                this.currentStone.place(true);

                for (int i = 0; i < bp[1]; i++) {
                    this.currentStone.moveStone(Stone.ROTATE_LEFT);
                    this.playPanel.repaint();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException iex) {
                    }
                }
                this.currentStone.place(false);
                this.currentStone.setPosition(bp[0], -1);
                this.currentStone.place(true);
                int dx = bp[0] - this.currentStone.getPosition().x;
                for (int i = 0; Math.abs(dx) > i; i++) {
                    this.currentStone.moveStone(dx > 0 ? Stone.MOVE_RIGHT : Stone.MOVE_LEFT);
                    this.playPanel.repaint();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException iex) {
                    }
                }
            }
        } else {
            this.gameOver();
        }
    }

    /**
     * Tries to move down the stone. Places the stone one line below its current
     * position if possible. If the stone hits a filled block the board is
     * cleared of full lines and stone is released to the board.
     * @return true if the stone was placed succesfully, false otherwise
     */
    private synchronized boolean moveDown() {
        if (currentStone.moveStone(Stone.MOVE_DOWN)) {
            this.playPanel.repaint();
            return true;
        }

        this.releaseCurrentStone();
        addLines(this.playPanel.removeFullLines());
        createNextStone();
        return false;
    }

    /**
     * Moves the stone down, until he hits another stone.
     */
    private void fallDown() {
        while (this.moveDown() && !gameOver) {
        }
    }

    /**
     * Pause/resume the game.
     */
    public boolean pause() {
        setPaused(!paused.get());
        return paused.get();
    }

    /**
     * Pause or resume the game.
     * @param to pause or not
     */
    private void setPaused(boolean val) {
        paused.set(val);
        if (paused.get()) {
            buttonPause.setText(rb.getString("Resume"));
        } else {
            buttonPause.setText(rb.getString("Pause"));
            grabFocus();
        }
    }

    /**
     * Called when the game is over.
     * Checks if the player gets into the highscore list. If so the name is
     * queried and the score added.
     * Stops the moverThread.
     */
    public void gameOver() {
        this.moverThread = null;
        this.playPanel.setGameOver(true);
        this.gameOver = true;
        buttonStart.grabFocus();

        HighScore highScore = initHighScore();
        if (highScore.isHighScore(lines)) {
            String defValue = ctx.getLastName();
            String name = (String) JOptionPane.showInputDialog(
                    this,
                    rb.getString("Type in your name:"),
                    rb.getString("High score"),
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    null,
                    defValue);
            if (name != null && !name.equals("")) {
                ctx.put(Context.Property.LAST_NAME, name);
                highScore.addScore(name, lines);
                highScore.setVisible(true);
            }
        }
    }

    /**
     * Instantiates HighScore.
     */
    private HighScore initHighScore() {
        HighScore highScore = new HighScore();
        highScore.setLocationRelativeTo(this);
        return highScore;
    }

    /**
     * Shows high score panel.
     */
    private void showHighScore() {
        HighScore hs = initHighScore();
        hs.setVisible(true);
        hs.addWindowListener(new WindowAdapter() {

            @Override
            public void windowDeactivated(WindowEvent e) {
                grabFocus();
            }
        });
    }

    /**
     * The current stone is released to the board
     * and thus can not bemoved anymore.
     */
    private void releaseCurrentStone() {
        this.currentStone = null;
        incStones();
    }

    private int getLevel() {
        return level;
    }

    private void setLevel(int i) {
        level = i;
        levelLabel.setText(rb.getString("Level:") + " " + level);
    }

    public void setMoverThread(Thread thread) {
        moverThread = thread;
    }

    /**
     * Increases a numbers of stones used in the game.
     */
    private void incStones() {
        setStones(++stones);
        if (stones > 20 * level && level < 10) {
            setLevel(getLevel() + 1);
        }
    }

    /**
     * Sets stones count to the given amount.
     * @param stones count
     */
    private void setStones(int s) {
        stones = s;
        stonesLabel.setText(rb.getString("Stones:") + " " + this.stones);
    }

    /**
     * Increases the lines variable.
     * @param number of lines to be added.
     */
    private void addLines(int l) {
        setLines(lines += l);
    }

    /**
     * Sets lines count to the given amount.
     * @param lines count
     */
    private void setLines(int lines) {
        this.lines = lines;
        linesLabel.setText(rb.getString("Lines:") + " " + this.lines);
    }

    /**
     * Returns version of this application.
     * @return version of this application
     */
    public static String getVersion() {
        return version;
    }

    /**
     * The dialog when game is started containing level and severity selection.
     * @return
     */
    private Object[] getStartMsg() {
        if (this.startMsg == null) {
            severityCB = new JComboBox();
            severityCB.addItem(rb.getString("Beginner"));
            severityCB.addItem(rb.getString("Medium"));
            severityCB.addItem(rb.getString("Expert"));
            severityCB.setSelectedIndex(1);
            levelCB = new JComboBox();
            for (int i = 1; i <= 10; i++) {
                levelCB.addItem(new Integer(i));
            }
            this.startMsg = new Object[]{
                        new JLabel(rb.getString("Severity:")),
                        severityCB,
                        new JLabel(rb.getString("Start_level:")),
                        levelCB};
        }

        return this.startMsg;
    }

    public static void main(String[] args) {
        new MainFrame(new Hextris());
    }
} 
