package cs3500.threetrios;

import cs3500.threetrios.adapter.ControllerAdapter;
import cs3500.threetrios.adapter.ReadOnlyTTAdapter;
import cs3500.threetrios.controller.AIPlayer;
import cs3500.threetrios.controller.ThreeTriosController;
import cs3500.threetrios.model.FallenAceDecorator;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.PlusRuleDecorator;
import cs3500.threetrios.model.ReverseRuleDecorator;
import cs3500.threetrios.model.SameRuleDecorator;
import cs3500.threetrios.model.ThreeTriosGameModel;
import cs3500.threetrios.model.Grid;
import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.model.ThreeTriosGrid;
import cs3500.threetrios.model.ThreeTriosCard;
import cs3500.threetrios.strategy.CornerStrat;
import cs3500.threetrios.strategy.DefensiveStrat;
import cs3500.threetrios.strategy.MaxFlipsStrat;
import cs3500.threetrios.strategy.MinimaxStrat;
import cs3500.threetrios.view.ThreeTriosSwingView;
import cs3500.threetrios.adapter.ViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * java -jar threetrios.jar [player1] [player2] [variant rules...] [provider]
 *
 * java -jar threetrios.jar human human
 * java -jar threetrios.jar human human reverse fallenace
 * java -jar threetrios.jar human cornerstrat same
 * java -jar threetrios.jar human human reverse provider
 *
 */

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
    List<String> variantArgs = new ArrayList<>();
    List<String> playerArgs = new ArrayList<>();
    boolean useProviderView = false;

    for (String arg : args) {
      switch (arg.toLowerCase()) {
        case "reverse":
        case "fallenace":
        case "same":
        case "plus":
          variantArgs.add(arg.toLowerCase());
          break;
        case "provider":
          useProviderView = true;
          break;
        default:
          playerArgs.add(arg.toLowerCase());
          break;
      }
    }

    if (variantArgs.contains("same") && variantArgs.contains("plus")) {
      throw new IllegalArgumentException("Cannot use Same and Plus rules together");
    }

    MainModelInterface model = new ThreeTriosGameModel();
    boolean reverseActive = variantArgs.contains("reverse");

    // Apply decorators in order
    if (reverseActive) {
      model = new ReverseRuleDecorator(model);
    }
    if (variantArgs.contains("fallenace")) {
      model = new FallenAceDecorator(model, reverseActive);  // Pass whether reverse rule is active
    }
    if (variantArgs.contains("same")) {
      model = new SameRuleDecorator(model);
    } else if (variantArgs.contains("plus")) {
      model = new PlusRuleDecorator(model);
    }
    setupGame(model);

    String redType = playerArgs.size() > 0 ? playerArgs.get(0) : "human";
    String blueType = playerArgs.size() > 1 ? playerArgs.get(1) : "human";

    Player redPlayer = model.getPlayers().get(0);
    Player bluePlayer = model.getPlayers().get(1);

    redPlayer = createPlayer(redType, redPlayer);
    bluePlayer = createPlayer(blueType, bluePlayer);

    ThreeTriosSwingView redView = new ThreeTriosSwingView(model);
    ThreeTriosController redController = new ThreeTriosController(model, redView, redPlayer);
    redView.setLocation(100, 100);

    if (useProviderView) {
      ReadOnlyTTAdapter modelAdapter = new ReadOnlyTTAdapter(model);
      ViewAdapter blueView = new ViewAdapter(modelAdapter, "Blue Player");
      ControllerAdapter blueController = new ControllerAdapter(model, blueView, bluePlayer);
      blueView.addClickListener(blueController);
      blueView.setLocation(700, 100);

      model.addFeaturesListener(redController);
      model.addFeaturesListener(blueController);
      redController.startGame();
      blueView.makeVisible();

      if (model.getCurrentPlayer().getColor().equals("BLUE") &&
              bluePlayer instanceof AIPlayer) {
        blueController.notifyTurnChange(model.getCurrentPlayer());
      }
    } else {
      ThreeTriosSwingView blueView = new ThreeTriosSwingView(model);
      ThreeTriosController blueController = new ThreeTriosController(model, blueView, bluePlayer);
      blueView.setLocation(700, 100);
      redController.startGame();
      blueController.startGame();
    }
  }

  private static Player createPlayer(String type, Player basePlayer) {
    switch (type.toLowerCase()) {
      case "human":
        return basePlayer;
      case "cornerstrat":
        AIPlayer ai1 = new AIPlayer(basePlayer);
        ai1.setStrategy(new CornerStrat());
        return ai1;
      case "defensivestrat":
        AIPlayer ai2 = new AIPlayer(basePlayer);
        ai2.setStrategy(new DefensiveStrat());
        return ai2;
      case "minimaxstrat":
        AIPlayer ai3 = new AIPlayer(basePlayer);
        ai3.setStrategy(new MinimaxStrat(new DefensiveStrat()));
        return ai3;
      case "maxflipsstrat":
        AIPlayer ai4 = new AIPlayer(basePlayer);
        ai4.setStrategy(new MaxFlipsStrat());
        return ai4;
      default:
        throw new IllegalArgumentException("Unknown player type");
    }
  }

  private static void setupGame(MainModelInterface model) {
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
          case 0:
            north = 10;
            break;
          case 1:
            south = 10;
            break;
          case 2:
            east = 10;
            break;
          case 3:
            west = 10;
            break;
          default:
            break;
        }
      }
      deck.add(new ThreeTriosCard("card" + i, north, south, east, west));
    }
    return deck;
  }
}