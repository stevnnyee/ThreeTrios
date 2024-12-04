package cs3500.threetrios.provider.model;

/**
 * Represents the possible options for a player's color.
 */
public enum PlayerColor {
  RED("RED"), BLUE("BLUE");

  private String color;

  PlayerColor(String color) {
    this.color = color;
  }

  /**
   * Return the string representation of this player.
   *
   * @return the color.
   */
  public String toString() {
    return color;
  }
}
