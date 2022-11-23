package logic;

import fileio.Coordinates;

public class tableCoordinates {
    public int x;
    public int y;

    public tableCoordinates(Coordinates coordinates) {
        this.x = coordinates.getX();
        this.y = coordinates.getY();
    }
}
