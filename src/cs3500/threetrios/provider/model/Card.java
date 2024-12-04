package cs3500.threetrios.provider.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a card in a Trios game. The card has 4 numbers and a color.
 */
public class Card implements TriosCard {

  private final String name;
  private final int north;
  private final int east;
  private final int south;
  private final int west;
  private PlayerColor cardColor;

  /**
   * Constructs a Card for the ThreeTriosModel Game.
   *
   * @param north the top number of the card
   * @param south the bottom number of the card
   * @param east  the right number of the card
   * @param west  the left number of the card
   * @throws IllegalArgumentException if card numbers are invalid
   * @throws IllegalArgumentException if name is invalid
   */
  public Card(String name, int north, int south, int east, int west) {
    if (north < 0 || north > 10
            || south < 0 || south > 10
            || east < 0 || east > 10
            || west < 0 || west > 10) {
      throw new IllegalArgumentException("Card values must be between 1 and 10");
    }
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Card name cannot be empty");
    }

    this.name = name;
    this.north = north;
    this.east = east;
    this.south = south;
    this.west = west;
  }

  /**
   * Constructs a Card for the ThreeTriosModel Game with a pre-defined color.
   *
   * @param north the top number of the card
   * @param south the bottom number of the card
   * @param east  the right number of the card
   * @param west  the left number of the card
   * @param color the color of the card
   * @throws IllegalArgumentException if card numbers are invalid
   * @throws IllegalArgumentException if name is invalid
   */
  public Card(String name, int north, int south, int east, int west, PlayerColor color) {
    if (north < 0 || north > 10
            || south < 0 || south > 10
            || east < 0 || east > 10
            || west < 0 || west > 10) {
      throw new IllegalArgumentException("Card values must be between 1 and 10");
    }
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Card name cannot be empty");
    }

    this.name = name;
    this.north = north;
    this.east = east;
    this.south = south;
    this.west = west;
    this.cardColor = color;
  }

  /**
   * Creates a card identical to the given card.
   *
   * @param c the card to copy
   */
  protected Card(Card c) {
    this.name = c.name;
    this.north = c.north;
    this.east = c.east;
    this.south = c.south;
    this.west = c.west;
    this.cardColor = c.cardColor;
  }

  @Override
  public String toString() {
    if (cardColor == null) {
      throw new IllegalStateException("This card is in the deck.");
    }

    String start = String.format("%c %s ", cardColor.toString().charAt(0), name);
    String end = String.format("%d %d %d %d", north, south,
            east, west).replaceAll("10", "A");

    return start + end;
  }

  // changes the color of the card to the given color
  protected void setColor(PlayerColor c) {
    this.cardColor = c;
  }

  /**
   * Returns the color of this card.
   *
   * @return the color of this card
   */
  public PlayerColor getColor() {
    if (cardColor == null) {
      throw new IllegalStateException("This card is currently in the deck.");
    }
    return this.cardColor;
  }

  // changes the color of the cell
  protected void flipColor() {
    if (this.cardColor == PlayerColor.RED) {
      this.cardColor = PlayerColor.BLUE;
    } else {
      this.cardColor = PlayerColor.RED;
    }
  }

  /**
   * Compares this card's values with its neighbors and flips their color if this card has a
   * higher number in that direction.
   *
   * @param neighbors a map containing this card's neighbors and their relative direction
   */
  @Override
  public List<Card> battle(Map<String, Card> neighbors) {

    List<Card> result = new ArrayList<>();

    if (neighbors.containsKey("north")) {
      Card other = neighbors.get("north");
      if (other.compare("south", this.north) && other.getColor() != this.cardColor) {
        other.flipColor();

        result.add(other);
      }
    }

    if (neighbors.containsKey("south")) {
      Card other = neighbors.get("south");
      if (other.compare("north", this.south) && other.getColor() != this.cardColor) {
        other.flipColor();
        result.add(other);
      }
    }

    if (neighbors.containsKey("east")) {
      Card other = neighbors.get("east");
      if (other.compare("west", this.east) && other.getColor() != this.cardColor) {
        other.flipColor();
        result.add(other);

      }
    }

    if (neighbors.containsKey("west")) {
      Card other = neighbors.get("west");
      if (other.compare("east", this.west) && other.getColor() != this.cardColor) {
        other.flipColor();
        result.add(other);
      }
    }
    return result;
  }

  /**
   * Compares this card's value in the given direction with the provided value.
   *
   * @param direction the direction to compare (north, south, east, west)
   * @param value     the value to compare against
   * @return true if this card's value in the given direction is less than
   *     the provided value, and false otherwise
   */
  @Override
  public boolean compare(String direction, int value) {
    switch (direction.toLowerCase()) {
      case "north":
        return this.north < value;
      case "south":
        return this.south < value;
      case "east":
        return this.east < value;
      case "west":
        return this.west < value;
      default:
        throw new IllegalArgumentException("Invalid direction: " + direction);
    }
  }

  /**
   * Returns the value of this card in the given direction.
   *
   * @param direction the direction to compare (north, south, east, west)
   * @return the value of this card in the given direction
   * @throws IllegalArgumentException if the direction is invalid
   */
  @Override
  public int getDirection(String direction) {
    switch (direction.toLowerCase()) {
      case "north":
        return this.north;
      case "south":
        return this.south;
      case "east":
        return this.east;
      case "west":
        return this.west;
      default:
        throw new IllegalArgumentException("Invalid direction: " + direction);
    }
  }


  /**
   * Returns the name of this card.
   *
   * @return the card's name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns a string with this card's attack values, formatted as [NORTH[SOUTH][EAST][WEST].
   *
   * @return string containing the attack values
   */
  public String getAttackValues() {
    String result = String.format("%d%d%d%d", this.north, this.south, this.east, this.west);
    return result.replaceAll("10", "A");
  }

  /**
   * Hashes the name of this card. Overridden to ensure no two cards can have the same name.
   *
   * @return the hash of this Card.
   */
  @Override
  public int hashCode() {
    return this.name.hashCode();
  }

  /**
   * As necessitated, overrides the equals method. Only need to compare names here.
   *
   * @param obj to compare to
   * @return boolean representing if this card equals another object.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Card) {
      Card other = (Card) obj;
      return (this.name.equals(other.name));
    }
    return false;
  }
}
