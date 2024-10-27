package cs3500.threetrios.model;

public enum Direction {
  NORTH, SOUTH, EAST, WEST;

  public Direction getOpposite() {
    switch (this) {
      case NORTH: return SOUTH;
      case SOUTH: return NORTH;
      case EAST: return WEST;
      case WEST: return EAST;
      default: throw new IllegalStateException("Unknown direction");
    }
  }
}
