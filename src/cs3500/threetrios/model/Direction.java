package cs3500.threetrios.model;

/**
 * An enumeration for the directions of the Card: North, South, East. or West.
 */
public enum Direction {
  NORTH, SOUTH, EAST, WEST;

  /**
   * Returns the opposite direction of the given direction,
   * which will be useful in the battle phase.
   *
   * @return the opposite direction.
   */
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
