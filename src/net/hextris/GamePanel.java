package net.hextris;

import java.util.Vector;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * implements a Panel to display a hextris gameboard
 * paints the hexagons
 * @author fränk
 * @author Radek Varbuchta
 */
public class GamePanel extends JPanel {

    private static final long serialVersionUID = -9073308186742942554L;
    private static Context ctx = Context.getContext();
    static Vector<GamePanel> panels = new Vector<GamePanel>();
    //static final int removeLineDelay = 100;
    int panelWidth;
    int panelHeight;
    int pixelWidth;
    int pixelHeight;
    JLabel gameOverLbl;

    //some variables for hexagon drawing
    static int rh = 7;
    int bh;
    int hexHeight;
    //coordinates of hexagon corners
    int hexWidth;
    int[] xPoints = new int[7];
    int[] yPoints = new int[7];
    /**
     * color array with colors for stones of some index
     */
    /*Color colors[] = {
    Color.LIGHT_GRAY,
    new Color(200,0,100),
    new Color(100,200,0),
    new Color(0,100,200),
    new Color(100,100,100),
    new Color(240,0,0),
    new Color(0,240,0),
    new Color(0,0,240),
    new Color(130,0,0),
    new Color(0,130,0),
    new Color(0,0,130),
    new Color(240,240,0),
    new Color(240,0,240),
    new Color(0,240,240),
    new Color(200,100,0),
    new Color(0,200,100),
    new Color(100,0,200)};*/
    Color colors[] = {
        Color.WHITE,
        Color.WHITE,
        Color.WHITE,
        Color.WHITE,
        Color.WHITE,
        Color.WHITE,
        Color.WHITE,
        Color.WHITE,
        Color.WHITE,
        Color.WHITE,
        Color.WHITE,
        Color.WHITE,
        Color.WHITE,
        Color.WHITE,
        Color.WHITE,
        Color.WHITE,
        Color.WHITE};
    private Board board;
    private Color backgroundColor = Color.BLACK;
    private boolean gameOver = false;

    /**
     *
     * @param width number of hexagons horicontally
     * @param height number of hexagons vertically
     */
    public GamePanel(int width, int height) {
        super();
        panels.add(this);
        panelWidth = width;
        panelHeight = height;
        board = new Board(width, height);

        // get properties
        Integer hs = ((Integer) ctx.getProperty(Context.HEX_SIZE));
        if (hs != null) {
            rh = hs.intValue();
        }

        this.initComponents();
        this.initialize();
    }

    /**
     * computes coordiinates of the corners of the hexagons and stets the size in pixel of the panel
     *
     */
    public void initialize() {
        //hexsize
        bh = (int) java.lang.Math.round(2 * rh * java.lang.Math.cos(java.lang.Math.PI / 6));
        hexHeight = 2 * bh;
        hexWidth = 3 * rh;

        //coordinates of hexcorners
        int cy = bh;
        int cx = 2 * rh + hexWidth - 4;
        yPoints[0] = cy;
        yPoints[1] = cy + cy;
        yPoints[2] = cy + cy;
        yPoints[3] = cy;
        yPoints[4] = cy - cy;
        yPoints[5] = cy - cy;
        yPoints[6] = yPoints[0];

        xPoints[0] = cx + 2 * rh;
        xPoints[1] = cx + rh;
        xPoints[2] = cx - rh;
        xPoints[3] = cx - 2 * rh;
        xPoints[4] = cx - rh;
        xPoints[5] = cx + rh;
        xPoints[6] = xPoints[0];

        pixelWidth = (panelWidth - 1) * hexWidth - rh;
        pixelHeight = panelHeight * hexHeight - rh - bh;

        //set panelsize
        Dimension d = new Dimension(pixelWidth + 2 * hexWidth, pixelHeight + hexHeight);
        this.setPreferredSize(d);
        this.setMaximumSize(d);
        this.setMinimumSize(d);
        this.setSize(d);
    }

    /**
     * Paints the hexagons in the panel.
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(this.backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (board != null) {
            for (int x = 0; x < board.getWidth(); x++) {
                int lineOffset = (x % 2) * (hexHeight / 2);
                for (int y = 0; y < board.getHeight(); y++) {
                    int colorId = board.field[y][x];
                    if (colorId != 0) {
                        g.setColor(colors[colorId - 1]);

                        int bx = x * hexWidth - hexWidth / 2 - rh;
                        int by = y * hexHeight - bh;

                        int[] xs = new int[7];
                        int[] ys = new int[7];
                        for (int i = 0; i < 7; i++) {
                            xs[i] = xPoints[i] + bx;
                            ys[i] = yPoints[i] + by + lineOffset;
                        }

                        /*g.fillPolygon(xs, ys, 6);
                        g.setColor(Color.BLACK);
                        g.drawLine(xs[1], ys[1], xs[0], ys[0]);
                        g.drawLine(xs[5], ys[5], xs[0], ys[0]);
                        g.drawLine(xs[1], ys[1], xs[2], ys[2]);
                        g.drawLine(xs[5], ys[5], xs[4], ys[4]);
                        g.drawLine(xs[2], ys[2], xs[3], ys[3]);
                        g.drawLine(xs[4], ys[4], xs[3], ys[3]);*/

                        Image img = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/net/hextris/brick.gif"));
                        g.drawImage(img, xs[4], ys[4], this);
                    }
                }
            }
        }
    }

    /**
     * @return
     */
    public Board getBoard() {
        return board;
    }

    /**
     * removes the full lines in the board and drops the above lines down
     * @return
     */
    public int removeFullLines() {
        int lines = 0;
        for (int y = this.board.getHeight() - 2; y > 0; y--) {
            if (this.board.lineFull(y)) {
                this.board.clearLine(y);
                this.repaint();
                this.board.removeLine(y);
                this.repaint();
                lines++;
                y++;
            }
        }
        return lines;

    }

    /**
     * sets size of hexagons and repaints all panels
     * @param size
     */
    public static void setHexSize(int size) {
        if (rh != size) {
            rh = size;
            for (GamePanel panel : panels) {
                panel.initialize();
                Component c = panel;
                while (c.getParent() != null) {
                    c = c.getParent();
                }
                ((JFrame) c).pack();
            }
            ctx.put(Context.HEX_SIZE, new Integer(size));
        }
    }

    public static int getHexSize() {
        return rh;
    }

    /**
     * sets the background color and repaints
     */
    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        this.backgroundColor = bg;
        this.repaint();
    }

    public int getPanelWidth() {
        return this.panelWidth;
    }

    public void setGameOver(boolean isOver) {
        this.gameOver = isOver;
        gameOverLbl.setVisible(isOver);
        this.repaint();
    }

    private void initComponents() {
        gameOverLbl = new JLabel("Game over");
        gameOverLbl.setForeground(new Color(185, 38, 2));
        gameOverLbl.setFont(new Font("arial", Font.BOLD, 25));
        gameOverLbl.setHorizontalAlignment(JLabel.CENTER);
        gameOverLbl.setVerticalAlignment(JLabel.CENTER);
        gameOverLbl.setVisible(false);

        this.setLayout(new GridLayout());
        this.add(gameOverLbl);
    }
}
