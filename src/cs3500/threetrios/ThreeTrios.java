package cs3500.threetrios;

import cs3500.threetrios.controller.AIPlayer;
import cs3500.threetrios.controller.HumanPlayer;
import cs3500.threetrios.controller.ThreeTriosController;
import cs3500.threetrios.model.ThreeTriosGameModel;
import cs3500.threetrios.model.Grid;
import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.model.ThreeTriosGrid;
import cs3500.threetrios.model.ThreeTriosCard;
import cs3500.threetrios.strategy.CornerStrat;
import cs3500.threetrios.strategy.DefensiveStrat;
import cs3500.threetrios.strategy.MinimaxStrat;
import cs3500.threetrios.view.ThreeTriosSwingView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Main class for the ThreeTrios game. This class initializes and starts the game with
 * a predefined board layout and randomly generated cards.
 * The board is a 5x7 grid with specific holes pattern, and the deck contains 35 cards
 * with random attack values, including 6 special cards with maximum attack values.
 */
public final class ThreeTrios {
  /**
   * Main method that initializes and starts the ThreeTrios game.
   * Creates a game board with a specific hole pattern, generates a deck of cards with
   * random attack values and initializes the game model and view,
   * and randomly selects the starting player.
   *
   * @param args command line arguments.
   * @throws IllegalArgumentException if there's an error starting the game or if the thread
   *                                  is interrupted while waiting for the first move
   */
  public static void main(String[] args) {
    // Get player types with default to human
    String redType = args.length > 0 ? args[0].toLowerCase() : "human";
    String blueType = args.length > 1 ? args[1].toLowerCase() : "human";

    System.out.println("Starting game with Red=" + redType + ", Blue=" + blueType);

    // Create and setup model
    ThreeTriosGameModel model = new ThreeTriosGameModel();
    setupGame(model);

    // Get initial players
    Player redPlayer = model.getPlayers().get(0);
    Player bluePlayer = model.getPlayers().get(1);

    // Create wrapped players
    redPlayer = createPlayer(redType, redPlayer);
    bluePlayer = createPlayer(blueType, bluePlayer);

    // Create views
    ThreeTriosSwingView redView = new ThreeTriosSwingView(model);
    ThreeTriosSwingView blueView = new ThreeTriosSwingView(model);

    // Create controllers
    ThreeTriosController redController = new ThreeTriosController(model, redView, redPlayer);
    ThreeTriosController blueController = new ThreeTriosController(model, blueView, bluePlayer);

    // Set up views
    redView.setLocation(100, 100);
    blueView.setLocation(700, 100);

    // Start the controllers
    redController.startGame();
    blueController.startGame();

    // Print status
    System.out.println("Game initialized with:");
    System.out.println("Red player: " + redPlayer.getColor() + " (" + redType + ")");
    System.out.println("Blue player: " + bluePlayer.getColor() + " (" + blueType + ")");
    System.out.println("Current player: " + model.getCurrentPlayer().getColor());
  }

  private static Player createPlayer(String type, Player basePlayer) {
    System.out.println("Creating player of type: " + type);
    switch (type.toLowerCase()) {
      case "human":
        return basePlayer;
      case "cornerstrat":
        AIPlayer ai1 = new AIPlayer(basePlayer);
        ai1.setStrategy(new CornerStrat());
        System.out.println("Created AI player with CornerStrat for " + basePlayer.getColor());
        return ai1;
      case "defensivestrat":
        AIPlayer ai2 = new AIPlayer(basePlayer);
        ai2.setStrategy(new DefensiveStrat());
        System.out.println("Created AI player with DefensiveStrat for " + basePlayer.getColor());
        return ai2;
      case "minimaxstrat":
        AIPlayer ai3 = new AIPlayer(basePlayer);
        ai3.setStrategy(new MinimaxStrat(new DefensiveStrat()));
        System.out.println("Created AI player with MinimaxStrat for " + basePlayer.getColor());
        return ai3;
      default:
        throw new IllegalArgumentException("Unknown player type: " + type + ". Valid types are: human, cornerstrat, defensivestrat, minimaxstrat");
    }
  }

  private static void setupGame(ThreeTriosGameModel model) {
    // Your existing setup code
    int rows = 5;
    int cols = 7;
    boolean[][] holes = new boolean[rows][cols];

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        holes[i][j] = true;
      }
    }

    for (int i = 0; i < rows; i++) {
      holes[i][0] = false;
      holes[i][6] = false;
    }

    holes[0][1] = false;
    holes[1][2] = false;
    holes[2][3] = false;
    holes[3][4] = false;
    holes[4][5] = false;

    Grid grid = new ThreeTriosGrid(rows, cols, holes);
    List<Card> deck = createDeck();

    model.startGame(grid, deck);
    model.setCurrentPlayer(Math.random() < 0.5 ? "RED" : "BLUE");
  }

  private static List<Card> createDeck() {
    List<Card> deck = new ArrayList<>();
    Random random = new Random();

    for (int i = 1; i <= 35; i++) {
      int north = random.nextInt(10) + 1;
      int south = random.nextInt(10) + 1;
      int east = random.nextInt(10) + 1;
      int west = random.nextInt(10) + 1;

      if (i <= 6) {
        switch (i % 4) {
          case 0: north = 10; break;
          case 1: south = 10; break;
          case 2: east = 10; break;
          case 3: west = 10; break;
        }
      }
      deck.add(new ThreeTriosCard("card" + i, north, south, east, west));
    }
    return deck;
  }
}