package cs3500.threetrios.strategy;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Direction;
import cs3500.threetrios.model.Player;

/**
 * A mock implementation of the Card interface used for testing strategies and game logic.
 * This class provides a simplified card representation with predefined attack powers
 * in each direction.
 */
public class MockCard implements Card {
  private final String name;
  private final int north, south, east, west;
  private Player owner;

  /**
   * Constructs a new MockCard with a specified name and a power for each direction.
   *
   * @param name
   * @param north
   * @param south
   * @param east
   * @param west
   */
  public MockCard(String name, int north, int south, int east, int west) {
    this.name = name;
    this.north = north;
    this.south = south;
    this.east = east;
    this.west = west;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getAttackPower(Direction dir) {
    switch (dir) {
      case NORTH:
        return north;
      case SOUTH:
        return south;
      case EAST:
        return east;
      case WEST:
        return west;
      default:
        return 0;
    }
  }

  @Override
  public Player getOwner() {
    return owner;
  }

  @Override
  public void setOwner(Player player) {
    this.owner = player;
  }
}