package byow.TileEngine;

import byow.Core.Engine;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

/**
 * Utility class for rendering tiles. You do not need to modify this file. You're welcome
 * to, but be careful. We strongly recommend getting everything else working before
 * messing with this renderer, unless you're trying to do something fancy like
 * allowing scrolling of the screen or tracking the avatar or something similar.
 */
public class TERenderer {
    private static final int TILE_SIZE = 16;
    private int width;
    private int height;
    private int xOffset;
    private int yOffset;
    private static final Font NORMAL_FONT = new Font("Georgia", Font.PLAIN, 15);
    private static final Font TITLE_FONT = new Font("Georgia", Font.PLAIN, 28);

    /**
     * Same functionality as the other initialization method. The only difference is that the xOff
     * and yOff parameters will change where the renderFrame method starts drawing. For example,
     * if you select w = 60, h = 30, xOff = 3, yOff = 4 and then call renderFrame with a
     * TETile[50][25] array, the renderer will leave 3 tiles blank on the left, 7 tiles blank
     * on the right, 4 tiles blank on the bottom, and 1 tile blank on the top.
     * @param w width of the window in tiles
     * @param h height of the window in tiles.
     */
    public void initialize(int w, int h, int xOff, int yOff) {
        this.width = w;
        this.height = h;
        this.xOffset = xOff;
        this.yOffset = yOff;
        StdDraw.setCanvasSize(width * TILE_SIZE, height * TILE_SIZE);
        Font font = new Font("Monaco", Font.BOLD, TILE_SIZE - 2);
        StdDraw.setFont(font);      
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.clear(new Color(0, 0, 0));

        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    /**
     * Initializes StdDraw parameters and launches the StdDraw window. w and h are the
     * width and height of the world in number of tiles. If the TETile[][] array that you
     * pass to renderFrame is smaller than this, then extra blank space will be left
     * on the right and top edges of the frame. For example, if you select w = 60 and
     * h = 30, this method will create a 60 tile wide by 30 tile tall window. If
     * you then subsequently call renderFrame with a TETile[50][25] array, it will
     * leave 10 tiles blank on the right side and 5 tiles blank on the top side. If
     * you want to leave extra space on the left or bottom instead, use the other
     * initializatiom method.
     * @param w width of the window in tiles
     * @param h height of the window in tiles.
     */
    public void initialize(int w, int h) {
        initialize(w, h, 0, 0);
    }

    /**
     * Takes in a 2d array of TETile objects and renders the 2d array to the screen, starting from
     * xOffset and yOffset.
     *
     * If the array is an NxM array, then the element displayed at positions would be as follows,
     * given in units of tiles.
     *
     *              positions   xOffset |xOffset+1|xOffset+2| .... |xOffset+world.length
     *                     
     * startY+world[0].length   [0][M-1] | [1][M-1] | [2][M-1] | .... | [N-1][M-1]
     *                    ...    ......  |  ......  |  ......  | .... | ......
     *               startY+2    [0][2]  |  [1][2]  |  [2][2]  | .... | [N-1][2]
     *               startY+1    [0][1]  |  [1][1]  |  [2][1]  | .... | [N-1][1]
     *                 startY    [0][0]  |  [1][0]  |  [2][0]  | .... | [N-1][0]
     *
     * By varying xOffset, yOffset, and the size of the screen when initialized, you can leave
     * empty space in different places to leave room for other information, such as a GUI.
     * This method assumes that the xScale and yScale have been set such that the max x
     * value is the width of the screen in tiles, and the max y value is the height of
     * the screen in tiles.
     * @param world the 2D TETile[][] array to render
     */
    public void renderFrame(TETile[][] world) {
        int numXTiles = world.length;
        int numYTiles = world[0].length;
        StdDraw.clear(new Color(0, 0, 0));
        for (int x = 0; x < numXTiles; x += 1) {
            for (int y = 0; y < numYTiles; y += 1) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                            + " is null.");
                }
                world[x][y].draw(x + xOffset, y + yOffset);
            }
        }

        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    public void renderHUD(TETile[][] world, double x, double y, String string) {
        StdDraw.setPenColor(Color.MAGENTA);
        StdDraw.setFont(NORMAL_FONT);
        StdDraw.text(x, y, string);

        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    public void renderMenu(TETile[][] world) {
        StdDraw.clear();
        StdDraw.setPenColor(Color.RED);
        Font title = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(title);
        StdDraw.text(this.width / 2.0, this.height * 0.75, "Hi World");

        Font heading = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(heading);
        StdDraw.text(Engine.WIDTH * 0.5, Engine.HEIGHT * 0.5, "Load Game (L)");
        StdDraw.text(Engine.WIDTH * 0.5, Engine.HEIGHT * 0.5 + 2, "New Game (N)");
        StdDraw.text(Engine.WIDTH * 0.5, Engine.HEIGHT * 0.5 - 2, "Quit (Q)");

        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    public void renderSeedPromptScreen(TETile[][] world, String seed) {
        StdDraw.clear();
        StdDraw.setPenColor(Color.RED);
        Font heading = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(heading);
        StdDraw.text(width / 2, height * 0.75, "Enter Random Number:");
        StdDraw.text(width / 2, height / 2, seed);
        StdDraw.text(width / 2, 0.25 * height, "Press 'S' to Create World");

        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    // return the corresponding tile type at the position of mouse
    public String mousePosition(TETile[][] world) {
        double x = StdDraw.mouseX();
        double y = StdDraw.mouseY();
        int xtile = (int) Math.round(x - 0.5);
        int ytile = (int) Math.round(y - 0.5);
        if (x >= world.length | y >= world[0].length) {
            return "";
        }
        TETile t = world[xtile][ytile];
        return t.description();
    }

    // TETile[][] world, String mousePos, boolean twoPlayer, int score1, int socre2
    public void updateHUD(Engine e) {
        StdDraw.clear();
        renderFrame(e.getWorld());
        renderHUD(e.getWorld(), 7, height - 1, "Use 'W' 'S' 'A' 'D' to walk around!");
        if (e.twoPlayer()) {
            renderHUD(e.getWorld(), width - 7, height - 1, "Use 'I' 'K' 'J' 'L' to walk around!");
        } else {
            renderHUD(e.getWorld(), width - 6, height - 1, "Press 'N' to join the game!");
        }
        updateCoin(e);
    }

    public void updateCoin(Engine e) {
        renderHUD(e.getWorld(), width / 2, height - 2, "Player 1: " + e.getScore(true));
        if (e.twoPlayer()) {
            renderHUD(e.getWorld(), width / 2, height - 3, "Player 2: " + e.getScore(false));
        }
    }

    public boolean hasNextKey() {
        return StdDraw.hasNextKeyTyped();
    }

    public void update(Engine e) {
        TETile[][] w = e.getWorld();
        double x = StdDraw.mouseX();
        double y = StdDraw.mouseY();
        int xtile = (int) Math.round(x - 0.5);
        int ytile = (int) Math.round(y - 0.5);
        if (x >= w.length | y >= w[0].length) {
            return;
        }
        TETile t = w[xtile][ytile];
        updateHUD(e);
        renderHUD(w, width / 2, height - 1, "There is " + t.description());
    }
}
