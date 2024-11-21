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
    // Create model and initial setup
    ThreeTriosGameModel model = new ThreeTriosGameModel();
    setupGame(model);

    // Create views for both players
    ThreeTriosSwingView redView = new ThreeTriosSwingView(model);
    ThreeTriosSwingView blueView = new ThreeTriosSwingView(model);

    // Configure players based on arguments
    String redType = args.length > 0 ? args[0] : "human";
    String blueType = args.length > 1 ? args[1] : "human";

    Player redPlayer = model.getPlayers().get(0);
    Player bluePlayer = model.getPlayers().get(1);

    // Create wrapped players based on type
    Player redWrapped = createPlayer(redType, redPlayer);
    Player blueWrapped = createPlayer(blueType, bluePlayer);

    // Create and setup controllers
    ThreeTriosController redController = new ThreeTriosController(model, redView, redWrapped);
    ThreeTriosController blueController = new ThreeTriosController(model, blueView, blueWrapped);

    // Add feature listeners
    model.addFeaturesListener(redController);
    model.addFeaturesListener(blueController);
    redView.addViewFeatures(redController);
    blueView.addViewFeatures(blueController);

    // Position windows side by side
    redView.setLocation(100, 100);
    blueView.setLocation(700, 100);

    // Start game
    redController.startGame();
    blueController.startGame();
  }

  private static Player createPlayer(String type, Player basePlayer) {
    switch (type.toLowerCase()) {
      case "human":
        return basePlayer;
      case "strategy1":
        AIPlayer ai1 = new AIPlayer(basePlayer);
        ai1.setStrategy(new CornerStrat());
        return ai1;
      case "strategy2":
        AIPlayer ai2 = new AIPlayer(basePlayer);
        ai2.setStrategy(new DefensiveStrat());
        return ai2;
      case "strategy3":
        AIPlayer ai3 = new AIPlayer(basePlayer);
        ai3.setStrategy(new MinimaxStrat(new DefensiveStrat())); // Using DefensiveStrat as opponent strategy
        return ai3;
      default:
        throw new IllegalArgumentException("Unknown player type: " + type);
    }
  }

  private static void setupGame(ThreeTriosGameModel model) {
    // Your existing setup code
    int rows = 5;
    int cols = 7;
    boolean[][] holes = new boolean[rows][cols];

    // ... rest of your setup code ...
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