package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;
import java.util.TreeMap;

/**
 * Draws a world that contains RANDOM tiles.
 */
public class World {
    private int width;
    private int height;
    private Random random;
    private TETile[][] world;
    private TreeMap<Position, Room> rooms = new TreeMap<>();
    private Position avatarPos;
    private Position avatar2Pos;

    // empty world constructor
    public World() {
    }

    // encounter world
    public World(int width, int height, boolean twoPlayer) {
        this.width = width;
        this.height = height;
        this.world = new TETile[width][height];
        fillWithNothing();
        Room r = new Room(new Position(width / 2 - 10, 0), 10, 20);
        r.drawRoom(world);
        changeTile(new Position(width / 2, 0), Tileset.UNLOCKED_DOOR);
        avatarPos = new Position(width / 2, 1);
        changeTile(avatarPos, Tileset.AVATAR);
        if (twoPlayer) {
            avatar2Pos = new Position(width / 2, 1);
            changeTile(avatar2Pos, Tileset.AVATAR2);
        }
    }

    // constructor
    public World(int width, int height, Random r) {
        this.width = width;
        this.height = height;
        this.random = r;
        this.world = new TETile[width][height];
        fillWithNothing();
        fillWithConnectedRooms();
        setEncounter();
        addAvatar(true);
    }

    public void setEncounter() {
        boolean added = false;
        while (!added) {
            int x = this.random.nextInt(width);
            int y = this.random.nextInt(height);
            Position p = new Position(x, y);
            if (detect(p, 0, 0).equals(Tileset.WALL)) {
                if (floor(p, 1, 0) | floor(p, 0, 1) | floor(p, -1, 0) | floor(p, 0, -1)) {
                    changeTile(p, Tileset.UNLOCKED_DOOR);
                    added = true;
                }
            }
        }
    }

    public boolean floor(Position p, int dx, int dy) {
        Position pnew = p.shift(dx, dy);
        if (!pnew.inRange(width, height)) {
            return false;
        }
        if (detect(p, dx, dy).equals(Tileset.FLOOR)) {
            return true;
        }
        return false;
    }

    public void addAvatar(boolean player1) {
        if (player1) {
            boolean added = false;
            while (!added) {
                int x = this.random.nextInt(width);
                int y = this.random.nextInt(height);
                if (this.world[x][y].equals(Tileset.FLOOR)) {
                    avatarPos = new Position(x, y);
                    changeTile(avatarPos, Tileset.AVATAR);
                    added = true;
                }
            }
        } else {
            avatar2Pos = avatarPos;
            changeTile(avatar2Pos, Tileset.AVATAR2);
        }
    }

    /**
    public void connectAllRooms() {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        Position p = new Position(x, y);
        Room center = new Room(p, 3, 3);
        Room first = findNeighbor(center);
        this.seperate = this.rooms;
        rooms.remove(first.getPosition());
        connected.put(first.getPosition(), first);
        for (int i = 0; i < rooms.size(); i++) {
            first = findNeighbor(center);
            rooms.remove(first.getPosition());
            Room neighbor =
                    connected.put(first.getPosition(), first);
        }
    }
     */

    public void recordRoom(Room r) {
        this.rooms.put(r.getPosition(), r);
    }

    // constructor helper
    public void fillWithNothing() {
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
    public void fillWithRooms() {
        for (int i = 0; i < 50; i++) {
            Room r = Room.generateRoom(this.random);
            if (r.inRange(width, height) & r.isEmpty(world)) {
                r.drawRoom(world);
            }
        }
    }
     */

    // constructor helper, fill blank map with connected rooms
    public void fillWithConnectedRooms() {
        Room firstR = Room.generateRoom(this.random);
        firstR.drawRoom(world);
        this.recordRoom(firstR);
        for (int i = 0; i < 100; i++) {
            Room newR = Room.generateRoom(this.random);
            if (newR.inRange(width, height) & newR.isEmpty(world)) {
                boolean connected = newR.isConnected(world);
                newR.drawRoom(world);
                if (!connected) {
                    Room neighbor = this.findNeighbor(newR);
                    connectRooms(neighbor, newR);
                }
                recordRoom(newR);
            }
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (this.world[x][y].equals(Tileset.GRASS)) {
                    this.world[x][y] = Tileset.WALL;
                }
            }
        }
    }

    // find neighbor of a given room by their bottom lefty corner position
    public Room findNeighbor(Room r) {
        Position pos = r.getPosition();
        int dis = width * width + height * height;
        Room neighbor = null;
        for (Position p : this.rooms.keySet()) {
            if (pos.distanceFrom(p) < dis) {
                dis = pos.distanceFrom(p);
                neighbor = this.rooms.get(p);
            }
        }
        return neighbor;
    }

    // fillWithConnectedRooms() helper, connect two given rooms with floor & grass
    public void connectRooms(Room r1, Room r2) {
        Position p1 = r1.randomPos(random);
        Position p2 = r2.randomPos(random);
        walkPath(p1, p2, random);
    }

    // connectRooms() helper, connect two random positions each from one room with a hallway.
    public void walkPath(Position p1, Position p2, Random r) {
        int xdir = p2.getX() - p1.getX();
        int ydir = p2.getY() - p1.getY();
        boolean xfirst = r.nextBoolean();
        if (xfirst) {
            /**
             if (checkCollision(p1, true, xdir, ydir)) {
             return false;
             }
             */
            walkX(p1, xdir);
            p1 = p1.shift(xdir, 0);
            walkY(p1, ydir);
        } else {
            /**
             if (checkCollision(p1, false, xdir, ydir)) {
             return false;
             }
             */
            walkY(p1, ydir);
            p1 = p1.shift(0, ydir);
            walkX(p1, xdir);
        }
    }

    /**
    private boolean checkCollision(Position p, boolean xfirst, int dx, int dy) {
        if (xfirst) {
            for (int x = 0; x < dx; x++) {
                if (detect(p, x, 0).equals(Tileset.WALL)) {
                    return false;
                }
            }
            for (int y = 0; y < dy; y++) {
                if (detect(p, dx - 1, y).equals(Tileset.WALL)) {
                    return false;
                }
            }
        } else {
            for (int y = 0; y < dy; y++) {
                if (detect(p, 0, y).equals(Tileset.WALL)) {
                    return false;
                }
            }
            for (int x = 0; x < dx; x++) {
                if (detect(p, x, dy - 1).equals(Tileset.WALL)) {
                    return false;
                }
            }
        }
        return true;
    }
     */

    public TETile detect(Position p, int dx, int dy) {
        return this.world[p.getX() + dx][p.getY() + dy];
    }

    public void changeTile(Position p, TETile t) {
        this.world[p.getX()][p.getY()] = t;
    }

    // walkPath() helper, for x direction walk
    public void walkX(Position p, int length) {
        if (length > 0) {
            for (int dx = 0; dx <= length; dx++) {
                this.changeTile(p.shift(dx, 0), Tileset.FLOOR);
            }
            for (int dx = 0; dx <= length + 1; dx++) {
                if (!this.detect(p, dx, 1).equals(Tileset.FLOOR)) {
                    this.changeTile(p.shift(dx, 1), Tileset.GRASS);
                }
                if (!this.detect(p, dx, -1).equals(Tileset.FLOOR)) {
                    this.changeTile(p.shift(dx, -1), Tileset.GRASS);
                }
            }
        }
        if (length < 0) {
            for (int dx = 0; dx <= -length; dx++) {
                this.changeTile(p.shift(-dx, 0), Tileset.FLOOR);
            }
            for (int dx = 0; dx <= -length + 1; dx++) {
                if (!this.detect(p, -dx, 1).equals(Tileset.FLOOR)) {
                    this.changeTile(p.shift(-dx, 1), Tileset.GRASS);
                }
                if (!this.detect(p, -dx, -1).equals(Tileset.FLOOR)) {
                    this.changeTile(p.shift(-dx, -1), Tileset.GRASS);
                }
            }
        }
    }

    // walkPath() helper, for y direction walk
    public void walkY(Position p, int length) {
        if (length > 0) {
            for (int dy = 0; dy <= length; dy++) {
                this.changeTile(p.shift(0, dy), Tileset.FLOOR);
            }
            for (int dy = 0; dy <= length + 1; dy++) {
                if (!this.detect(p, 1, dy).equals(Tileset.FLOOR)) {
                    this.changeTile(p.shift(1, dy), Tileset.WALL);
                }
                if (!this.detect(p, -1, dy).equals(Tileset.FLOOR)) {
                    this.changeTile(p.shift(-1, dy), Tileset.WALL);
                }
            }
        }
        if (length < 0) {
            for (int dy = 0; dy <= -length; dy++) {
                this.changeTile(p.shift(0, -dy), Tileset.FLOOR);
            }
            for (int dy = 0; dy <= -length + 1; dy++) {
                if (!this.detect(p, 1, -dy).equals(Tileset.FLOOR)) {
                    this.changeTile(p.shift(1, -dy), Tileset.WALL);
                }
                if (!this.detect(p, -1, -dy).equals(Tileset.FLOOR)) {
                    this.changeTile(p.shift(-1, -dy), Tileset.WALL);
                }
            }
        }
    }

    // random room generator
    public static void generateRooms(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = null;
            }
        }
    }

    // get method for private instance variables
    public TETile[][] getWorld() {
        return this.world;
    }

    public Position findAvatar(boolean first) {
        if (first) {
            return avatarPos;
        } else {
            return avatar2Pos;
        }
    }

    public void moveAvatar(boolean first, Position to) {
        if (first) {
            changeTile(avatarPos, Tileset.FLOOR);
            avatarPos = to;
        } else {
            changeTile(avatar2Pos, Tileset.FLOOR);
            avatar2Pos = to;
        }
        changeTile(avatarPos, Tileset.AVATAR);
        if (avatar2Pos != null) {
            changeTile(avatar2Pos, Tileset.AVATAR2);
        }
    }
}
