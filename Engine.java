package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.*;
import java.awt.*;
import java.io.File;
import java.util.Random;

public class Engine {
    private static final TERenderer TER = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private boolean initialized = false;
    private World worldObj = new World();
    private World savedWorld;
    private boolean gameOver = false;
    public static final Font NORMAL_FONT = new Font("Georgia", Font.PLAIN, 17);
    private long SEED;
    InputSource input  = new KeyboardInputSource();
    private String inputs = "";
    private String mousePos = "";
    private boolean player2 = false;
    private boolean encountered = false;
    private int coin1 = 0;
    private int coin2 = 0;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        TER.initialize(WIDTH, HEIGHT + 3);
        TER.renderMenu(getWorld());
        while (!initialized) {
            char key = input.getNextKey();
            if (key == 'N') { // setup new game with keyboard: read seed and build world
                setWorldObj();
                TER.update(this);
            } else if (key == 'L') { // load previous game and set up for keyboard play
                loadPrevGame();
                TER.update(this);
            } else if (key == 'Q') { // quit w/o changing savings
                System.exit(0);
            }
        }
        while (!gameOver) {
            dealWithKeyboardInput();
        }
        saveGame();
        System.exit(0);
    }


    public void setWorldObj() {
        String seed = "";
        TER.renderSeedPromptScreen(getWorld(), seed);
        char nextKey = input.getNextKey();
        while (nextKey != 'S' | seed.length() == 0) {
            if (Character.isDigit(nextKey)) {
                seed += nextKey;
                TER.renderSeedPromptScreen(getWorld(), seed);
            }
            nextKey = input.getNextKey();
        }
        SEED = Long.parseLong(seed);
        this.worldObj = new World(WIDTH, HEIGHT, new Random(SEED));
        initialized = true;
    }

    public void dealWithKeyboardInput() {
        if (TER.hasNextKey()) {
            char key = input.getNextKey();
            if (key != ':') {
                inputs += key;
                action(key);
            } else {
                char next = input.getNextKey();
                if (next == 'Q') {
                    gameOver = true;
                }
            }
            TER.update(this);
        } else {
            if (!mousePos.equals(TER.mousePosition(getWorld()))) {
                mousePos = TER.mousePosition(getWorld());
                TER.update(this);
            }
        }
    }

    public TETile[][] getWorld() {
        return worldObj.getWorld();
    }

    public void action(char key) {
        Position pos = worldObj.findAvatar(true);
        switch (key) {
            case 'W':
                move(true, pos.shift(0, 1));
                break;
            case 'A':
                move(true, pos.shift(-1, 0));
                break;
            case 'S':
                move(true, pos.shift(0, -1));
                break;
            case 'D':
                move(true, pos.shift(1, 0));
                break;
            case 'N':
                if (!player2) {
                    worldObj.addAvatar(false);
                    player2 = true;
                }
                if (savedWorld != null) {
                    savedWorld.addAvatar(false);
                }
                break;
            case 'I':
                if (player2) {
                    Position pos2 = worldObj.findAvatar(false);
                    move(false, pos2.shift(0, 1));
                }
                break;
            case 'J':
                if (player2) {
                    Position pos2 = worldObj.findAvatar(false);
                    move(false, pos2.shift(-1, 0));
                }
                break;
            case 'K':
                if (player2) {
                    Position pos2 = worldObj.findAvatar(false);
                    move(false, pos2.shift(0, -1));
                }
                break;
            case 'L':
                if (player2) {
                    Position pos2 = worldObj.findAvatar(false);
                    move(false, pos2.shift(1, 0));
                }
                break;
            default:
                break;
        }
    }

    public void move(boolean first, Position to) {
        TETile t = getWorld()[to.getX()][to.getY()];
        if (t.equals(Tileset.WALL) | t.equals(Tileset.LOCKED_DOOR)) {
            return;
        } else if (t.equals(Tileset.UNLOCKED_DOOR)) {
            worldObj.changeTile(to, Tileset.LOCKED_DOOR);
            encounter();
        } else {
            if (t.equals(Tileset.COIN)) {
                if (first) {
                    coin1 += 1;
                } else {
                    coin2 += 1;
                }
            }
            worldObj.moveAvatar(first, to);
        }
    }

    private void encounter() {
        if (encountered) {
            worldObj = savedWorld;
        } else {
            savedWorld = worldObj;
            worldObj = new World(WIDTH, HEIGHT, player2);
            Random r = new Random(SEED);
            for (int i = 0; i < 100; i++) {
                int x = r.nextInt(18) + WIDTH / 2 - 9;
                int y = r.nextInt(8) + 1;
                Position p = new Position(x, y);
                worldObj.changeTile(p, Tileset.COIN);
            }
            encountered = true;
        }
    }

    // save the user inputs as a string to a .txt file
    public void saveGame() {
        File f = new File("seed.txt");
        Utils.writeContents(f, String.valueOf(SEED));
        File g = new File("inputs.txt");
        Utils.writeContents(g, inputs);
    }

    public void loadPrevGame() {
        File f = new File("seed.txt");
        String seedStr = Utils.readContentsAsString(f);
        SEED = Long.parseLong(seedStr);
        this.worldObj = new World(WIDTH, HEIGHT, new Random(SEED));
        gameOver = false;
        initialized = true;
        File g = new File("inputs.txt");
        inputs = Utils.readContentsAsString(g);
        for (char i : inputs.toCharArray()) {
            action(i);
        }
    }

    public TETile[][] interactWithInputString(String operations) {
        InputSource inputSource = new StringInputDevice(operations);
        String seed = "";
        boolean readyToQuitNSave = false;

        while (inputSource.possibleNextInput()) {
            char key = inputSource.getNextKey();
            if (!initialized) {
                if (key == 'N') {
                    key = inputSource.getNextKey();
                    while (key != 'S') {
                        seed += key;
                        key = inputSource.getNextKey();
                    }
                    SEED = Long.parseLong(seed);
                    worldObj = new World(WIDTH, HEIGHT, new Random(SEED));
                    initialized = true;
                } else if (key == 'L') {
                    loadPrevGame();
                } else if (key == 'Q') {
                    System.exit(0);
                }
            } else if (key == ':') {
                readyToQuitNSave = true;
            } else if (readyToQuitNSave) {
                if (key == 'Q') {
                    saveGame();
                } else {
                    readyToQuitNSave = false;
                }
            } else if (!gameOver) {
                inputs += key;
                action(key);
            }
        }
        return getWorld();
    }

    public int getScore(boolean first) {
        if (first) {
            return coin1;
        } else {
            return coin2;
        }
    }

    public boolean twoPlayer() {
        return player2;
    }
}

