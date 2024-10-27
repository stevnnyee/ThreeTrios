package cs3500.threetrios.model;

import java.util.List;

public interface Player {
  String getColor();
  List<Card> getHand();
  void addCardToHand(Card card);
  void removeCardFromHand(Card card);
  int countOwnedCards(Grid grid);
}