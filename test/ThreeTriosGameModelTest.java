import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Grid;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.model.ThreeTriosCard;
import cs3500.threetrios.model.ThreeTriosGameModel;
import cs3500.threetrios.model.ThreeTriosGrid;

public class ThreeTriosGameModelTest {
  private MainModelInterface model;
  private Grid smallGrid;
  private Grid connectedGrid;
  private Grid disconnectedGrid;
  private List<Card> smallDeck;
  private List<Card> fullDeck;

  @Before
  public void setup() {
    model = new ThreeTriosGameModel();

    // Create test grids
    boolean[][] smallGridHoles = new boolean[][]{
            {false, false, false},
            {false, false, false},
            {false, false, false}
    };
    smallGrid = new ThreeTriosGrid(3, 3, smallGridHoles);

    boolean[][] connectedGridHoles = new boolean[][]{
            {false, false, false},
            {false, true, false},
            {false, false, false},
            {false, true, false},
            {false, false, false}
    };
    connectedGrid = new ThreeTriosGrid(5, 3, connectedGridHoles);

    boolean[][] disconnectedGridHoles = new boolean[][]{
            {false, false, true, false, false},
            {false, false, true, false, false},
            {true, true, true, true, true},
            {false, false, true, false, false},
            {false, false, true, false, false}
    };
    disconnectedGrid = new ThreeTriosGrid(5, 5, disconnectedGridHoles);

    // Create card decks
    smallDeck = createSmallDeck();
    fullDeck = createFullDeck();
  }

  private List<Card> createSmallDeck() {
    List<Card> deck = new ArrayList<>();
    // 15 cards for smaller boards
    deck.add(new ThreeTriosCard("BlackKnight", 8, 6, 9, 7));
    deck.add(new ThreeTriosCard("BabyDragon", 7, 8, 6, 5));
    deck.add(new ThreeTriosCard("IceWizard", 6, 5, 8, 7));
    deck.add(new ThreeTriosCard("EliteBarbs", 9, 8, 7, 9));
    deck.add(new ThreeTriosCard("Archer", 6, 7, 8, 5));
    deck.add(new ThreeTriosCard("Witch", 7, 5, 8, 6));
    deck.add(new ThreeTriosCard("Goblin", 5, 6, 7, 4));
    deck.add(new ThreeTriosCard("Princess", 6, 4, 8, 5));
    deck.add(new ThreeTriosCard("Prince", 9, 7, 8, 8));
    deck.add(new ThreeTriosCard("Valkyrie", 8, 8, 7, 7));
    deck.add(new ThreeTriosCard("Pekka", 10, 9, 8, 9));
    deck.add(new ThreeTriosCard("Warden", 8, 8, 9, 7));
    deck.add(new ThreeTriosCard("King", 9, 8, 8, 9));
    deck.add(new ThreeTriosCard("Queen", 10, 8, 9, 8));
    deck.add(new ThreeTriosCard("Yeti", 8, 9, 7, 8));
    return deck;
  }

  private List<Card> createFullDeck() {
    List<Card> deck = new ArrayList<>(createSmallDeck());
    // Add 10 more cards for larger boards
    deck.add(new ThreeTriosCard("HogRider", 9, 6, 10, 7));
    deck.add(new ThreeTriosCard("Skeleton", 4, 3, 5, 4));
    deck.add(new ThreeTriosCard("Balloon", 6, 8, 5, 7));
    deck.add(new ThreeTriosCard("Dragon", 9, 8, 10, 8));
    deck.add(new ThreeTriosCard("Minion", 5, 6, 7, 5));
    deck.add(new ThreeTriosCard("Snowman", 6, 7, 6, 8));
    deck.add(new ThreeTriosCard("Golem", 9, 10, 7, 8));
    deck.add(new ThreeTriosCard("Healer", 5, 7, 6, 5));
    deck.add(new ThreeTriosCard("Miner", 7, 6, 8, 9));
    deck.add(new ThreeTriosCard("ElectroWizard", 8, 7, 9, 8));
    return deck;
  }

  @Test
  public void testGameInitialization() {
    model.startGame(smallGrid, fullDeck);
    Assert.assertNotNull("Grid should be initialized", model.getGrid());
    Assert.assertNotNull("Current player should be set", model.getCurrentPlayer());
    Assert.assertEquals("Game should start with RED player", "RED", model.getCurrentPlayer().getColor());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidGridInitialization() {
    model.startGame(null, fullDeck);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidDeckInitialization() {
    model.startGame(smallGrid, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInsufficientCards() {
    model.startGame(disconnectedGrid, smallDeck);
  }

  @Test
  public void testCardPlacement() {
    model.startGame(smallGrid, fullDeck);
    Player firstPlayer = model.getCurrentPlayer();
    Card cardToPlace = model.getPlayerHand(firstPlayer).get(0);

    model.placeCard(firstPlayer, 0, 0, cardToPlace);

    Assert.assertNotNull("Card should be placed on grid", model.getGrid().getCard(0, 0));
    Assert.assertFalse("Card should be removed from hand",
            model.getPlayerHand(firstPlayer).contains(cardToPlace));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlacementInHole() {
    model.startGame(connectedGrid, fullDeck);
    Player firstPlayer = model.getCurrentPlayer();
    Card cardToPlace = model.getPlayerHand(firstPlayer).get(0);

    // Try to place card in a hole
    model.placeCard(firstPlayer, 1, 1, cardToPlace);
  }

  @Test
  public void testCardBattle() {
    model.startGame(smallGrid, fullDeck);
    Player firstPlayer = model.getCurrentPlayer();
    Card pekka = new ThreeTriosCard("Pekka", 10, 9, 8, 9);
    Card goblin = new ThreeTriosCard("Goblin", 5, 6, 7, 4);

    // Place stronger card first
    model.placeCard(firstPlayer, 0, 0, pekka);

    // Place weaker card adjacent
    Player secondPlayer = model.getCurrentPlayer();
    model.placeCard(secondPlayer, 0, 1, goblin);

    // Verify battle outcome
    Assert.assertEquals("Stronger card should maintain ownership",
            firstPlayer, model.getGrid().getCard(0, 0).getOwner());
  }

  @Test
  public void testGameCompletion() {
    model.startGame(smallGrid, fullDeck);

    // Fill all cells
    for (int i = 0; i < smallGrid.getRows(); i++) {
      for (int j = 0; j < smallGrid.getCols(); j++) {
        if (!model.getGrid().isHole(i, j) && model.getGrid().getCard(i, j) == null) {
          Player currentPlayer = model.getCurrentPlayer();
          Card cardToPlace = model.getPlayerHand(currentPlayer).get(0);
          model.placeCard(currentPlayer, i, j, cardToPlace);
        }
      }
    }

    Assert.assertTrue("Game should end when grid is full", model.isGameOver());
    Assert.assertNotNull("Winner should be determined", model.getWinner());
  }

  @Test
  public void testPlayerScoring() {
    model.startGame(smallGrid, fullDeck);
    Player firstPlayer = model.getCurrentPlayer();
    int initialHandSize = model.getPlayerHand(firstPlayer).size();

    // Place a card and verify score changes
    Card cardToPlace = model.getPlayerHand(firstPlayer).get(0);
    model.placeCard(firstPlayer, 0, 0, cardToPlace);

    Assert.assertEquals("Hand size should decrease by 1",
            initialHandSize - 1, model.getPlayerHand(firstPlayer).size());
  }

  @Test
  public void testTurnAlternation() {
    model.startGame(smallGrid, fullDeck);
    Player firstPlayer = model.getCurrentPlayer();
    Card cardToPlace = model.getPlayerHand(firstPlayer).get(0);

    model.placeCard(firstPlayer, 0, 0, cardToPlace);

    Assert.assertNotEquals("Turn should alternate between players",
            firstPlayer, model.getCurrentPlayer());
  }
}
