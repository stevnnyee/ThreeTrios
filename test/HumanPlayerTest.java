import org.junit.Before;
import org.junit.Test;

import cs3500.threetrios.controller.HumanPlayer;
import cs3500.threetrios.features.MockViewFeatures;
import cs3500.threetrios.features.ViewFeatures;
import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.strategy.MockCard;
import cs3500.threetrios.strategy.MockPlayer;
import cs3500.threetrios.strategy.MockStrategy;
import cs3500.threetrios.strategy.MockThreeTriosModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HumanPlayerTest {
  private StringBuilder log;
  private Player basePlayer;
  private ViewFeatures features;
  private HumanPlayer humanPlayer;
  private Card testCard;

  @Before
  public void setUp() {
    log = new StringBuilder();
    basePlayer = new MockPlayer("BLUE");
    features = new MockViewFeatures(log);
    humanPlayer = new HumanPlayer(basePlayer, features);
    testCard = new MockCard("test", 5, 5, 5, 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullBasePlayer() {
    new HumanPlayer(null, features);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullFeatures() {
    new HumanPlayer(basePlayer, null);
  }

  @Test
  public void testGetColor() {
    assertEquals("BLUE", humanPlayer.getColor());
  }

  @Test
  public void testAddRemoveCard() {
    humanPlayer.addCardToHand(testCard);
    humanPlayer.removeCardFromHand(testCard);
    assertTrue(log.toString().contains("Added card"));
    assertTrue(log.toString().contains("Removed card"));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSetStrategyThrowsException() {
    humanPlayer.setStrategy(new MockStrategy(log));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetNextMoveThrowsException() {
    humanPlayer.getNextMove(new MockThreeTriosModel(log));
  }
}
