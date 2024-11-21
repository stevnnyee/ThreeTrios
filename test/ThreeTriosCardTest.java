import org.junit.Test;


import java.util.List;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.ThreeTriosCard;
import cs3500.threetrios.model.ThreeTriosGrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Class containing all the tests regarding the card methods.
 */
public class ThreeTriosCardTest {

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidGridDimensions() {
    new ThreeTriosGrid(0, 3, new boolean[0][3]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMismatchedHolesArray() {
    boolean[][] holes = {{false, false}};
    new ThreeTriosGrid(2, 2, holes);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEvenNumberOfCardCells() {
    boolean[][] holes = {
            {false, false},
            {false, false}
    };
    new ThreeTriosGrid(2, 2, holes);
  }

  @Test
  public void testIsHole() {
    boolean[][] holes = {
            {false, false, false},
            {false, true, true},
            {false, false, false}
    };
    ThreeTriosGrid grid = new ThreeTriosGrid(3, 3, holes);
    assertTrue(grid.isHole(1, 1));
    assertTrue(grid.isHole(1, 2));
    assertFalse(grid.isHole(0, 0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIsHoleOutOfBounds() {
    boolean[][] holes = {{false}};
    ThreeTriosGrid grid = new ThreeTriosGrid(1, 1, holes);
    grid.isHole(1, 1);
  }

  @Test
  public void testGetCard() {
    boolean[][] holes = {{false}};
    ThreeTriosGrid grid = new ThreeTriosGrid(1, 1, holes);
    assertNull(grid.getCard(0, 0));
  }

  @Test
  public void testPlaceCard() {
    boolean[][] holes = {{false}};
    ThreeTriosGrid grid = new ThreeTriosGrid(1, 1, holes);
    Card card = new ThreeTriosCard("Test", 1, 1, 1, 1);
    grid.placeCard(0, 0, card);
    assertEquals(card, grid.getCard(0, 0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlaceCardInHole() {
    boolean[][] holes = {{true}};
    ThreeTriosGrid grid = new ThreeTriosGrid(1, 1, holes);
    Card card = new ThreeTriosCard("Test", 1, 1, 1, 1);
    grid.placeCard(0, 0, card);
  }

  @Test(expected = IllegalStateException.class)
  public void testPlaceCardInOccupiedCell() {
    boolean[][] holes = {{false}};
    ThreeTriosGrid grid = new ThreeTriosGrid(1, 1, holes);
    Card card1 = new ThreeTriosCard("Test1", 1, 1, 1, 1);
    Card card2 = new ThreeTriosCard("Test2", 2, 2, 2, 2);
    grid.placeCard(0, 0, card1);
    grid.placeCard(0, 0, card2);
  }

  @Test
  public void testIsFull() {
    boolean[][] holes = {{false}};
    ThreeTriosGrid grid = new ThreeTriosGrid(1, 1, holes);
    assertFalse(grid.isFull());
    Card card = new ThreeTriosCard("Test", 1, 1, 1, 1);
    grid.placeCard(0, 0, card);
    assertTrue(grid.isFull());
  }

  @Test
  public void testGetEmptyCells() {
    boolean[][] holes = {
            {false, false},
            {false, true}
    };
    ThreeTriosGrid grid = new ThreeTriosGrid(2, 2, holes);
    List<int[]> emptyCells = grid.getEmptyCells();
    assertEquals(3, emptyCells.size());

    Card card = new ThreeTriosCard("Test", 1, 1, 1, 1);
    grid.placeCard(0, 0, card);
    emptyCells = grid.getEmptyCells();
    assertEquals(2, emptyCells.size());
  }
}