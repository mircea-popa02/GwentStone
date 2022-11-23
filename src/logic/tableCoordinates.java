package logic;

import fileio.Coordinates;

class TableCoordinates {
  public int x;
  public int y;

  public TableCoordinates(Coordinates coordinates) {
    this.x = coordinates.getX();
    this.y = coordinates.getY();
  }
}
