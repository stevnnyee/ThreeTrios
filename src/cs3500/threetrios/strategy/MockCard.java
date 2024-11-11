package cs3500.threetrios.strategy;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Direction;
import cs3500.threetrios.model.Player;

public class MockCard implements Card {
  private final String name;
  private final int north, south, east, west;
  private Player owner;

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
      case NORTH: return north;
      case SOUTH: return south;
      case EAST: return east;
      case WEST: return west;
      default: return 0;
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