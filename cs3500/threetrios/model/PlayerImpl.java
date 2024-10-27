package cs3500.threetrios.model;

import java.util.ArrayList;
import java.util.List;

public class PlayerImpl implements Player {
  private final String color;
  private final List<Card> hand;

    public PlayerImpl(String color) {
    if (color == null || color.trim().isEmpty()) {
      throw new IllegalArgumentException("Color cannot be null or empty");
    }
    this.color = color;
    this.hand = new ArrayList<>();
  }

  @Override
  public String getColor() {
    return color;
  }

  @Override
  public List<Card> getHand() {
    return new ArrayList<>(hand);
  }

  @Override
  public void addCardToHand(Card card) {
    if (card == null) {
      throw new IllegalArgumentException("Card cannot be null");
    }
    hand.add(card);
  }

  @Override
  public void removeCardFromHand(Card card) {
    if (card == null) {
      throw new IllegalArgumentException("Card cannot be null");
    }
    if (!hand.remove(card)) {
      throw new IllegalStateException("Card not found in hand");
    }
  }

  @Override
  public int countOwnedCards(Grid grid) {
    int count = hand.size();
    for (int i = 0; i < grid.getRows(); i++) {
      for (int j = 0; j < grid.getCols(); j++) {
        Card card = grid.getCard(i, j);
        if (card != null && card.getOwner() == this) {
          count++;
        }
      }
    }

    return count;
  }
}