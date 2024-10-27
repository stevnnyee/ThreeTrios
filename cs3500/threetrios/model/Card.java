package cs3500.threetrios.model;

public interface Card {
  String getName();

  int getAttackPower(Direction direction);
  Player getOwner();

  void setOwner(Player newOwner);
}
