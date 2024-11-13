package cs3500.threetrios.model;

import java.util.EnumMap;

/**
 * Class to represent a Card in the ThreeTrios Game.
 */
public class ThreeTriosCard implements Card {
  private final String name;
  private final EnumMap<Direction, Integer> values; // [NORTH, SOUTH, EAST, WEST]
  private Player owner;

  /**
   * Constructor for a card in the ThreeTrios game, throwing exceptions if name is null.
   *
   * @param name  name of card
   * @param north north value
   * @param south south value
   * @param east  east value
   * @param west  west value
   */
  public ThreeTriosCard(String name, int north, int south, int east, int west) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Card name cannot be null or empty");
    }
    this.name = name;
    this.values = new EnumMap<>(Direction.class);
    values.put(Direction.NORTH, north);
    values.put(Direction.SOUTH, south);
    values.put(Direction.EAST, east);
    values.put(Direction.WEST, west);
    validateValues();
  }

  /**
   * A method that validates the value of a card, throwing an exceptions if the value
   * is less than one or greater than ten.
   */
  private void validateValues() {
    for (Direction direction : Direction.values()) {
      int value = values.get(direction);
      if (value < 1 || value > 10) {
        throw new IllegalArgumentException("Card value for " + direction + " "
                + "must be between 1 and 10, but was " + value);
      }
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getAttackPower(Direction direction) {
    return values.get(direction);
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
