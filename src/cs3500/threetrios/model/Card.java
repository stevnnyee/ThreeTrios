package cs3500.threetrios.model;

/**
 * Behaviors for a Card in the Game of ThreeTrios.
 * Any additional behaviors for cards must be made
 * creating a new interface that extends this one.
 */
public interface Card {
  String getName();

  int getAttackPower(Direction direction);
  Player getOwner();

  void setOwner(Player newOwner);
}
