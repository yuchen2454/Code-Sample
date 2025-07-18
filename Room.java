package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class Room {
    private int bottom;
    private int left;
    private int top;
    private int right;

    private int height;
    private int width;
    private static final int WIDTHMIN = 4;
    private static final int HEIGHTMIN = 4;
    private static final int WIDTHMAX = 10;
    private static final int HEIGHTMAX = 8;

    public Room(Position p, int height, int width) {
        this.left = p.getX();
        this.bottom = p.getY();
        this.height = height;
        this.width = width;

        this.top = bottom + height - 1;
        this.right = left + width - 1;
    }

    public void drawRoom(TETile[][] world) {
        // check walls collision w hallway grass
        // $ $ $ $ $ $ $
        // . . . . . . . <-- room wall
        // $ $ $ $ $ $ $
        // then
        // x x x x x x x
        // . . . . . . .
        // . . . . . . .

        // draw walls
        for (int dx = 0; dx < width; dx++) {
            if (!world[this.left + dx][this.top].equals(Tileset.FLOOR)) {
                world[this.left + dx][this.top] = Tileset.WALL;
            }
            if (!world[this.left + dx][this.bottom].equals(Tileset.FLOOR)) {
                world[this.left + dx][this.bottom] = Tileset.WALL;
            }
        }
        for (int dy = 0; dy < height; dy++) {
            if (!world[this.left][this.bottom + dy].equals(Tileset.FLOOR)) {
                world[this.left][this.bottom + dy] = Tileset.WALL;
            }
            if (!world[this.right][this.bottom + dy].equals(Tileset.FLOOR)) {
                world[this.right][this.bottom + dy] = Tileset.WALL;
            }
        }
        //draw floor
        for (int dx = 1; dx < width - 1; dx++) {
            for (int dy = 1; dy < height - 1; dy++) {
                world[this.left + dx][this.bottom + dy] = Tileset.FLOOR;
            }
        }
    }

    public boolean inRange(int w, int h) {
        if (this.top < h & this.right < w) {
            return true;
        }
        return false;
    }

    public boolean isEmpty(TETile[][] world) {
        for (int dx = 0; dx < width; dx++) {
            for (int dy = 0; dy < height; dy++) {
                if (world[left + dx][bottom + dy].equals(Tileset.WALL)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isConnected(TETile[][] world) {
        for (int dx = 0; dx < width; dx++) {
            for (int dy = 0; dy < height; dy++) {
                if (world[left + dx][bottom + dy].equals(Tileset.FLOOR)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Position randomPos(Random r) {
        int x = left + 1 + r.nextInt(width - 3);
        int y = bottom + 1 + r.nextInt(height - 3);
        return new Position(x, y);
    }

    public static Room generateRoom(Random r) {
        int width = WIDTHMIN + r.nextInt(WIDTHMAX - WIDTHMIN);
        int height = HEIGHTMIN + r.nextInt(HEIGHTMAX - HEIGHTMIN);
        int left = r.nextInt(80 - width);
        int bottom = r.nextInt(30 - height);
        Position p = new Position(left, bottom);
        Room room = new Room(p, height, width);
        return room;
    }

    public Position getPosition() {
        return new Position(this.bottom, this.left);
    }
}
