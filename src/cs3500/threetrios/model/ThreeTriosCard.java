package cs3500.threetrios.model;

/**
 * Class to represent a Card in the ThreeTrios Game,
 */
public class ThreeTriosCard implements Card {
  private final String name;
  private final int[] values; // [NORTH, SOUTH, EAST, WEST]
  private Player owner;

  public ThreeTriosCard(String name, int north, int south, int east, int west) {
    this.name = name;
    this.values = new int[]{north, south, east, west};
    validateValues();
  }

  private void validateValues() {
    for (int value : values) {
      if (value < 1 || value > 10) {
        throw new IllegalArgumentException("Card values must be between 1 and 10");
      }
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getAttackPower(Direction direction) {
    return values[direction.ordinal()];
  }

  @Override
  public Player getOwner() {
    return owner;
  }

  @Override
  public void setOwner(Player newOwner) {
    if (newOwner == null) {
      throw new IllegalArgumentException("Owner cannot be null");
    }
    this.owner = newOwner;
  }
}
