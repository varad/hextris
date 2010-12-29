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
    private static Vector<GamePanel> panels = new Vector<GamePanel>();
    private int panelWidth;
    private int panelHeight;
    private JLabel gameOverLbl;

    //some variables for hexagon drawing
    private static int rh = 7;
    private int bh;
    private int hexHeight;
    //coordinates of hexagon corners
    private int hexWidth;
    private int[] xPoints = new int[7];
    private int[] yPoints = new int[7];
    private Board board;

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
        Integer hexSize = hexSizeToInt(ctx.getHexSize());
        if (hexSize != null) {
            rh = hexSize;
        }

        initComponents();
        initialize();
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

        //set panelsize
        int width = (panelWidth - 1) * hexWidth - rh + 2 * hexWidth;
        int height = panelHeight * hexHeight - rh - bh + hexHeight;
        Dimension dim = new Dimension(width, height);
        setPreferredSize(dim);
        setMaximumSize(dim);
        setMinimumSize(dim);
        setSize(dim);
    }

    /**
     * Paints the hexagons in the panel.
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.fillRect(0, 0, getWidth(), getHeight());

        if (board != null) {
            for (int x = 0; x < board.getWidth(); x++) {
                int lineOffset = (x % 2) * (hexHeight / 2);
                for (int y = 0; y < board.getHeight(); y++) {
                    int colorId = board.field[y][x];
                    if (colorId != 0) {
                        int bx = x * hexWidth - hexWidth / 2 - rh;
                        int by = y * hexHeight - bh;

                        int[] xs = new int[7];
                        int[] ys = new int[7];
                        for (int i = 0; i < 7; i++) {
                            xs[i] = xPoints[i] + bx;
                            ys[i] = yPoints[i] + by + lineOffset;
                        }

                        String imgName = ctx.getHexSize() == Context.HexSize.NORMAL
                                ? "brick.gif" : "brick_big.gif";
                        Image img = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/net/hextris/images/" + imgName));
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
     * Removes the full lines from the board and drops the lines above down.
     * @return
     */
    public int removeFullLines() {
        int lines = 0;
        for (int y = board.getHeight() - 2; y > 0; y--) {
            if (board.lineFull(y)) {
                board.clearLine(y);
                repaint();
                board.removeLine(y);
                repaint();
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
    public static void setHexSize(Context.HexSize hexSize) {
        int size = hexSizeToInt(hexSize);
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
        }
    }

    private static int hexSizeToInt(Context.HexSize size) {
        return size == Context.HexSize.BIG ? 7 : 4;
    }

    public int getPanelWidth() {
        return this.panelWidth;
    }

    public void setGameOver(boolean isOver) {
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

        setLayout(new GridLayout());
        add(gameOverLbl);
    }
}
