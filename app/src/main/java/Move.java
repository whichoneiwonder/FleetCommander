
public class Move {
    private int x;
    private int y;
    private int direction;

    public Move(int x, int y, int dir) {
        this.x = x;
        this.y = y;
        direction = dir;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDirection() {
        return direction;
    }
}
