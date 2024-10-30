

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Grid;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.model.ThreeTriosGameModel;
import cs3500.threetrios.model.ThreeTriosGrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Test class for ThreeTrios game functionality.
 */
public class ThreeTriosGameModelTest {
  private Path tempDir;
  private ThreeTriosGameModel game;
  private String boardPath;
  private String cardPath;

  @Before
  public void setup() throws IOException {
    tempDir = Files.createTempDirectory("threetrios-test");
    game = new ThreeTriosGameModel();

    createTestFile("board2-CellsReachWithHoles",
            "// 5x3 board with holes and cells connected to each other.\n" +
                    "5 3\n" +
                    "CCC\n" +
                    "CXC\n" +
                    "CCC\n" +
                    "CXC\n" +
                    "CCC");

    createTestFile("card2-EnoughCards",
            "// Cards for testing\n" +
                    "BlackKnight 8 6 9 7\n" +
                    "BabyDragon 7 8 6 5\n" +
                    "IceWizard 6 5 8 7\n" +
                    "EliteBarbs 9 8 7 9\n" +
                    "Archer 6 7 8 5\n" +
                    "Witch 7 5 8 6\n" +
                    "Goblin 5 6 7 4\n" +
                    "Princess 6 4 8 5\n" +
                    "Prince 9 7 8 8\n" +
                    "Valkyrie 8 8 7 7\n" +
                    "Pekka 9 9 8 9\n" +
                    "Warden 8 8 9 7\n" +
                    "King 9 8 8 9\n" +
                    "Queen 9 8 9 8\n" +
                    "Yeti 8 9 7 8");
  }

  @After
  public void cleanup() throws IOException {
    if (tempDir != null) {
      deleteDirectory(tempDir.toFile());
    }
  }

  /**
   * Recursively deletes a directory and its contents.
   */
  private void deleteDirectory(File directory) {
    File[] files = directory.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.isDirectory()) {
          deleteDirectory(file);
        } else {
          file.delete();
        }
      }
    }
    directory.delete();
  }

  /**
   * Creates a test file given a file name and its content.
   *
   * @param filename file name
   * @param content content of the file
   * @throws IOException if the file cannot run
   */
  private void createTestFile(String filename, String content) throws IOException {
    Path filePath = tempDir.resolve(filename);
    FileWriter writer = new FileWriter(filePath.toFile());
    writer.write(content);
    writer.close();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCardPlacementInHole() throws IOException {
    String boardPath = tempDir.resolve("board2-CellsReachWithHoles").toString();
    String cardPath = tempDir.resolve("card2-EnoughCards").toString();

    game.startGameFromConfig(boardPath, cardPath);
    Player currentPlayer = game.getCurrentPlayer();
    Card card = game.getPlayerHand(currentPlayer).get(0);

    game.placeCard(currentPlayer, 1, 1, card);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlaceCardInOccupiedPosition() throws IOException {
    String boardPath = tempDir.resolve("board2-CellsReachWithHoles").toString();
    String cardPath = tempDir.resolve("card2-EnoughCards").toString();

    game.startGameFromConfig(boardPath, cardPath);
    Player firstPlayer = game.getCurrentPlayer();
    Card card = game.getPlayerHand(firstPlayer).get(0);
    game.placeCard(firstPlayer, 0, 0, card);

    game.placeCard(firstPlayer, 0, 0, game.getPlayerHand(firstPlayer).get(1));
  }

  @Test(expected = FileNotFoundException.class)
  public void testStartGameWithInvalidBoardPath() throws IOException {
    String invalidBoardPath = tempDir.resolve("nonexistent-board").toString();
    String cardPath = tempDir.resolve("card2-EnoughCards").toString();
    game.startGameFromConfig(invalidBoardPath, cardPath);
  }

  @Test(expected = FileNotFoundException.class)
  public void testStartGameWithInvalidCardPath() throws IOException {
    String boardPath = tempDir.resolve("board2-CellsReachWithHoles").toString();
    String invalidCardPath = tempDir.resolve("nonexistent-cards").toString();
    game.startGameFromConfig(boardPath, invalidCardPath);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlaceCardOutOfBounds() throws IOException {
    String boardPath = tempDir.resolve("board2-CellsReachWithHoles").toString();
    String cardPath = tempDir.resolve("card2-EnoughCards").toString();

    game.startGameFromConfig(boardPath, cardPath);
    Player currentPlayer = game.getCurrentPlayer();
    Card card = game.getPlayerHand(currentPlayer).get(0);
    game.placeCard(currentPlayer, 5, 3, card);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidGridDimensions() {
    new ThreeTriosGrid(0, -3, new boolean[][]{});
  }


  @Test
  public void testBasicGameInitialization() throws IOException {
    String boardPath = tempDir.resolve("board2-CellsReachWithHoles").toString();
    String cardPath = tempDir.resolve("card2-EnoughCards").toString();

    game.startGameFromConfig(boardPath, cardPath);

    assertFalse(game.isGameOver());
    assertNotNull(game.getCurrentPlayer());
    assertEquals(13, game.getGrid().getCardCellCount());

    Grid grid = game.getGrid();
    assertEquals(5, grid.getRows());
    assertEquals(3, grid.getCols());

    assertTrue(grid.isHole(1, 1));
    assertTrue(grid.isHole(3, 1));
    assertFalse(grid.isHole(0, 0));
  }

  @Test
  public void testPlayerHandsDistribution() throws IOException {
    String boardPath = tempDir.resolve("board2-CellsReachWithHoles").toString();
    String cardPath = tempDir.resolve("card2-EnoughCards").toString();

    game.startGameFromConfig(boardPath, cardPath);

    Player currentPlayer = game.getCurrentPlayer();
    assertEquals(7, game.getPlayerHand(currentPlayer).size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCardPlacement() throws IOException {
    String boardPath = tempDir.resolve("board2-CellsReachWithHoles").toString();
    String cardPath = tempDir.resolve("card2-EnoughCards").toString();

    game.startGameFromConfig(boardPath, cardPath);

    Player currentPlayer = game.getCurrentPlayer();
    Card card = game.getPlayerHand(currentPlayer).get(0);
    game.placeCard(currentPlayer, 1, 1, card);
  }

  @Test
  public void testValidCardPlacement() throws IOException {
    String boardPath = tempDir.resolve("board2-CellsReachWithHoles").toString();
    String cardPath = tempDir.resolve("card2-EnoughCards").toString();

    game.startGameFromConfig(boardPath, cardPath);

    Player firstPlayer = game.getCurrentPlayer();
    Card card = game.getPlayerHand(firstPlayer).get(0);
    game.placeCard(firstPlayer, 0, 0, card);

    assertNotNull(game.getGrid().getCard(0, 0));
    assertEquals(card, game.getGrid().getCard(0, 0));

    assertNotEquals(firstPlayer, game.getCurrentPlayer());
  }

  @Test
  public void testGameNotOverInitially() throws IOException {
    String boardPath = tempDir.resolve("board2-CellsReachWithHoles").toString();
    String cardPath = tempDir.resolve("card2-EnoughCards").toString();

    game.startGameFromConfig(boardPath, cardPath);
    assertFalse(game.isGameOver());
  }

  @Test
  public void testInitialPlayerHandSize() throws IOException {
    String boardPath = tempDir.resolve("board2-CellsReachWithHoles").toString();
    String cardPath = tempDir.resolve("card2-EnoughCards").toString();

    game.startGameFromConfig(boardPath, cardPath);

    Player redPlayer = game.getCurrentPlayer();
    List<Card> redHand = game.getPlayerHand(redPlayer);

    assertEquals(7, redHand.size());
  }

  @Test
  public void testEndGameWithFullBoard() throws IOException {
    String boardPath = tempDir.resolve("board2-CellsReachWithHoles").toString();
    String cardPath = tempDir.resolve("card2-EnoughCards").toString();

    game.startGameFromConfig(boardPath, cardPath);
    Player currentPlayer = game.getCurrentPlayer();

    for (int rowIndex = 0; rowIndex < 5; rowIndex++) {
      for (int columnIndex = 0; columnIndex < 3; columnIndex++) {
        if (!game.getGrid().isHole(rowIndex, columnIndex) && !game.isGameOver()) {
          Card card = game.getPlayerHand(currentPlayer).get(0);
          game.placeCard(currentPlayer, rowIndex, columnIndex, card);
          currentPlayer = game.getCurrentPlayer();
        }
      }
    }
    assertTrue(game.isGameOver());
  }

  @Test
  public void testEmptyCellsAndFullStatus() throws IOException {
    String boardPath = tempDir.resolve("board2-CellsReachWithHoles").toString();
    String cardPath = tempDir.resolve("card2-EnoughCards").toString();

    game.startGameFromConfig(boardPath, cardPath);
    Grid grid = game.getGrid();

    assertFalse(grid.isFull());
    int initialEmptyCells = grid.getEmptyCells().size();
    assertEquals(13, initialEmptyCells);

    for (int rowIndex = 0; rowIndex < grid.getRows(); rowIndex++) {
      for (int columnIndex = 0; columnIndex < grid.getCols(); columnIndex++) {
        if (!grid.isHole(rowIndex, columnIndex)) {
          game.placeCard(game.getCurrentPlayer(),
                  rowIndex, columnIndex, game.getPlayerHand(game.getCurrentPlayer()).get(0));
        }
      }
    }

    assertTrue(grid.isFull());
    assertEquals(0, grid.getEmptyCells().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartGameWithNullGrid() {
    game.startGame(null, new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartGameWithNullDeck() {
    game.startGame(new ThreeTriosGrid(3, 3, new boolean[][]{}), null);
  }


  @Test(expected = IllegalArgumentException.class)
  public void testGetPlayerScoreNullPlayer() {
    game.getPlayerScore(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetPlayerHandNullPlayer() {
    game.getPlayerHand(null);
  }
}
