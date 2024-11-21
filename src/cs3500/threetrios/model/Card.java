package cs3500.threetrios.model;

/**
 * Behaviors for a Card in the Game of ThreeTrios.
 * Any additional behaviors for cards must be made
 * creating a new interface that extends this one.
 * Interface for a Card in the ThreeTrios game.
 * A ThreeTrios card is different from a RedSeven card in which it not only has an attack power,
 * but 4 directions which has its distinct directional power.
 */
public interface Card {

  /**
   * A method to return the name of the card.
   *
   * @return the name of the card
   */
  String getName();

  /**
   * A method to return the value of the card's direction.
   *
   * @param direction the direction we want to use
   * @return the value, or power, of the card's direction
   */
  int getAttackPower(Direction direction);

  Player getOwner();

  /**
   * A method to set the Card's owner, mainly focusing on the color
   * to determine its owner.
   *
   * @param newOwner the new Owner of the card
   */
  void setOwner(Player newOwner);
}
