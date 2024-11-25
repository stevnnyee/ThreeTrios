import cs3500.threetrios.controller.ThreeTriosController;
import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.strategy.MockCard;
import cs3500.threetrios.strategy.MockPlayer;
import cs3500.threetrios.strategy.MockThreeTriosModel;
import cs3500.threetrios.view.MockThreeTriosFrame;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Tests the functionality of the methods in the ThreeTriosController class.
 */
public class ThreeTriosControllerTest {
  private StringBuilder log;
  private MockThreeTriosModel model;
  private MockThreeTriosFrame view;
  private Player player;
  private ThreeTriosController controller;
  private Card testCard;

  @Before
  public void setUp() {
    log = new StringBuilder();
    model = new MockThreeTriosModel(log);
    view = new MockThreeTriosFrame(log);
    player = new MockPlayer("RED");
    testCard = new MockCard("test", 5, 5, 5, 5);

    List<Player> players = new ArrayList<>();
    players.add(player);
    players.add(new MockPlayer("BLUE"));

    model.setCurrentPlayer(player);
    controller = new ThreeTriosController(model, view, player);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullModel() {
    new ThreeTriosController(null, view, player);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullView() {
    new ThreeTriosController(model, null, player);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullPlayer() {
    new ThreeTriosController(model, view, null);
  }

  @Test
  public void testOnCardSelectedUpdatesView() {
    controller.onCardSelected(player, testCard);
    assertTrue(log.toString().contains("Set selected card"));
  }

  @Test
  public void testOnCardSelectedWrongPlayer() {
    Player wrongPlayer = new MockPlayer("BLUE");
    controller.onCardSelected(wrongPlayer, testCard);
    assertTrue(log.toString().contains("Set selected card to null"));
  }

  @Test
  public void testOnCellSelectedWithValidMove() {
    controller.onCardSelected(player, testCard);
    controller.onCellSelected(0, 0);

    String logStr = log.toString();
    assertTrue(logStr.contains("Checking if can place card at (0,0)"));
    assertTrue(logStr.contains("Card placed at (0,0)"));
    assertTrue(logStr.contains("View refreshed"));
  }

  @Test
  public void testNotifyTurnChangeUpdatesView() {
    Player nextPlayer = new MockPlayer("BLUE");
    controller.notifyTurnChange(nextPlayer);

    String logStr = log.toString();
    assertTrue(logStr.contains("Set title to: Waiting"));
    assertTrue(logStr.contains("View refreshed"));
  }

  @Test
  public void testNotifyGameOverUpdatesView() {
    controller.notifyGameOver(player);
    String logStr = log.toString();
    assertTrue(logStr.contains("Set selected card to null"));
    assertTrue(logStr.contains("View refreshed"));
  }
}
