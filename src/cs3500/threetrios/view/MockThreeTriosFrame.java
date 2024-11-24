package cs3500.threetrios.view;

import cs3500.threetrios.features.ViewFeatures;
import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.view.ThreeTriosFrame;

// Mock ThreeTriosFrame for testing
public class MockThreeTriosFrame implements ThreeTriosFrame {
  private final StringBuilder log;

  public MockThreeTriosFrame(StringBuilder log) {
    this.log = log;
  }

  @Override
  public void refresh() {
    log.append("View refreshed\n");
  }

  @Override
  public void display() {
    log.append("View displayed\n");
  }

  @Override
  public void setTitle(String title) {
    log.append("Set title to: ").append(title).append("\n");
  }

  @Override
  public void setSelectedCard(Card card, Player player) {
    if (card == null) {
      log.append("Set selected card to null\n");
    } else {
      log.append("Set selected card to: ").append(card.getName()).append("\n");
    }
  }

  @Override
  public void addViewFeatures(ViewFeatures features) {
    log.append("Added view features\n");
  }
}
