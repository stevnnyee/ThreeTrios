import org.junit.Before;
import org.junit.Test;

import java.util.List;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.ThreeTriosCard;
import cs3500.threetrios.model.ThreeTriosGrid;
import cs3500.threetrios.model.ThreeTriosPlayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Class containing the tests regarding the methods in player classes.
 */
public class ThreeTriosPlayerTest {
  private ThreeTriosPlayer player;
  private ThreeTriosCard testCard;
  private ThreeTriosGrid grid;

  @Before
  public void setup() {
    player = new ThreeTriosPlayer("RED");
    testCard = new ThreeTriosCard("TestCard", 5, 6, 7, 8);
    testCard.setOwner(player);

    boolean[][] standardHoles = new boolean[][]{
            {false, true, false},
            {true, false, true},
            {false, true, false}
    };
    grid = new ThreeTriosGrid(3, 3, standardHoles);
  }

  @Test
  public void testValidConstructor() {
    ThreeTriosPlayer p = new ThreeTriosPlayer("BLUE");
    assertEquals("BLUE", p.getColor());
    assertTrue(p.getHand().isEmpty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullColor() {
    new ThreeTriosPlayer(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorEmptyColor() {
    new ThreeTriosPlayer("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWhitespaceColor() {
    new ThreeTriosPlayer("   ");
  }

  @Test
  public void testGetColor() {
    assertEquals("RED", player.getColor());
  }

  @Test
  public void testGetHandInitiallyEmpty() {
    assertTrue(player.getHand().isEmpty());
  }

  @Test
  public void testGetHandReturnsCopy() {
    player.addCardToHand(testCard);
    List<Card> hand = player.getHand();
    hand.clear();
    assertEquals(1, player.getHand().size());
  }

  @Test
  public void testAddCardToHand() {
    player.addCardToHand(testCard);
    assertEquals(1, player.getHand().size());
    assertTrue(player.getHand().contains(testCard));
  }

  @Test
  public void testAddMultipleCardsToHand() {
    ThreeTriosCard card1 = new ThreeTriosCard("Card1", 1, 2, 3, 4);
    ThreeTriosCard card2 = new ThreeTriosCard("Card2", 5, 6, 7, 8);
    player.addCardToHand(card1);
    player.addCardToHand(card2);
    assertEquals(2, player.getHand().size());
    assertTrue(player.getHand().contains(card1));
    assertTrue(player.getHand().contains(card2));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddNullCardToHand() {
    player.addCardToHand(null);
  }

  @Test
  public void testRemoveCardFromHand() {
    player.addCardToHand(testCard);
    player.removeCardFromHand(testCard);
    assertTrue(player.getHand().isEmpty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveNullCardFromHand() {
    player.removeCardFromHand(null);
  }

  @Test(expected = IllegalStateException.class)
  public void testRemoveNonExistentCardFromHand() {
    player.removeCardFromHand(testCard);
  }

  @Test
  public void testRemoveSpecificCardFromHand() {
    ThreeTriosCard card1 = new ThreeTriosCard("Card1", 1, 2, 3, 4);
    ThreeTriosCard card2 = new ThreeTriosCard("Card2", 5, 6, 7, 8);
    player.addCardToHand(card1);
    player.addCardToHand(card2);
    player.removeCardFromHand(card1);
    assertEquals(1, player.getHand().size());
    assertFalse(player.getHand().contains(card1));
    assertTrue(player.getHand().contains(card2));
  }

  @Test
  public void testCountOwnedCardsEmpty() {
    assertEquals(0, player.countOwnedCards(grid));
  }

  @Test
  public void testCountOwnedCardsWithHandOnly() {
    player.addCardToHand(testCard);
    assertEquals(1, player.countOwnedCards(grid));
  }

  @Test
  public void testCountOwnedCardsWithGridOnly() {
    ThreeTriosCard placedCard = new ThreeTriosCard("PlacedCard",
            1, 2, 3, 4);
    placedCard.setOwner(player);
    grid.placeCard(0, 0, placedCard);
    assertEquals(1, player.countOwnedCards(grid));
  }

  @Test
  public void testCountOwnedCardsWithHandAndGrid() {
    player.addCardToHand(testCard);

    ThreeTriosCard placedCard = new ThreeTriosCard("PlacedCard",
            1, 2, 3, 4);
    placedCard.setOwner(player);
    grid.placeCard(0, 0, placedCard);

    assertEquals(2, player.countOwnedCards(grid));
  }

  @Test
  public void testCountOwnedCardsIgnoresOtherPlayerCards() {
    ThreeTriosPlayer otherPlayer = new ThreeTriosPlayer("BLUE");

    player.addCardToHand(testCard);

    ThreeTriosCard otherCard = new ThreeTriosCard("OtherCard",
            1, 2, 3, 4);
    otherCard.setOwner(otherPlayer);
    grid.placeCard(0, 0, otherCard);

    assertEquals(1, player.countOwnedCards(grid));
  }

  @Test
  public void testCountOwnedCardsMultipleOnGrid() {
    ThreeTriosCard card1 = new ThreeTriosCard("Card1", 1, 2, 3, 4);
    ThreeTriosCard card2 = new ThreeTriosCard("Card2", 5, 6, 7, 8);
    card1.setOwner(player);
    card2.setOwner(player);

    grid.placeCard(0, 0, card1);
    grid.placeCard(0, 2, card2);

    assertEquals(2, player.countOwnedCards(grid));
  }

  @Test
  public void testAddMaxCardsToHand() {
    for (int i = 0; i < 100; i++) {
      ThreeTriosCard card = new ThreeTriosCard("Card" + i, 1, 2, 3, 4);
      player.addCardToHand(card);
    }
    assertEquals(100, player.getHand().size());
  }

  @Test
  public void testAddDuplicateCardsToHand() {
    player.addCardToHand(testCard);
    player.addCardToHand(testCard);
    assertEquals(2, player.getHand().size());
  }

  @Test(expected = IllegalStateException.class)
  public void testRemoveCardFromEmptyHand() {
    ThreeTriosCard card = new ThreeTriosCard("TestCard", 1, 2, 3, 4);
    player.removeCardFromHand(card);
  }

  @Test(expected = NullPointerException.class)
  public void testCountOwnedCardsNullGrid() {
    player.countOwnedCards(null);
  }

  @Test
  public void testCountOwnedCardsEmptyGrid() {
    boolean[][] emptyHoles = new boolean[][]{
            {false, false, false},
            {false, false, false},
            {false, false, false}
    };
    ThreeTriosGrid emptyGrid = new ThreeTriosGrid(3, 3, emptyHoles);
    assertEquals(0, player.countOwnedCards(emptyGrid));
  }

  @Test
  public void testCountOwnedCardsWithCardsInHolesOwned() {
    ThreeTriosCard card = new ThreeTriosCard("Card", 1, 2, 3, 4);
    card.setOwner(player);

    grid.placeCard(0, 0, card);
    assertEquals(1, player.countOwnedCards(grid));
  }

  @Test
  public void testAddCardWithNoOwner() {
    ThreeTriosCard noOwnerCard = new ThreeTriosCard("NoOwner", 1, 2, 3, 4);
    player.addCardToHand(noOwnerCard);
    assertEquals(1, player.getHand().size());
  }

  @Test
  public void testAddCardWithDifferentOwner() {
    ThreeTriosPlayer otherPlayer = new ThreeTriosPlayer("BLUE");
    ThreeTriosCard otherOwnedCard = new ThreeTriosCard("OtherOwned", 1, 2, 3, 4);
    otherOwnedCard.setOwner(otherPlayer);
    player.addCardToHand(otherOwnedCard);
    assertEquals(1, player.getHand().size());
  }

}