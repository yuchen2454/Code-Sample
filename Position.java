package byow.Core;

public class Position implements Comparable<Position> {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean equals(Position p) {
        if (this.x == p.x & this.y == p.y) {
            return true;
        }
        return false;
    }

    public boolean inRange(int width, int height) {
        if (x < 0 | y < 0 | x >= width | y >= height) {
            return false;
        }
        return true;
    }

    public int distanceFrom(Position p) {
        return (this.x - p.x) * (this.x - p.x) + (this.y - p.y) * (this.y - p.y);
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ") ";
    }

    public Position shift(int dx, int dy) {
        return new Position(this.x + dx, this.y + dy);
    }

    @Override
    public int compareTo(Position o) {
        return (this.x * this.x) + (this.y * this.y) - (o.x * o.x) - (o.y * o.y);
    }
}

