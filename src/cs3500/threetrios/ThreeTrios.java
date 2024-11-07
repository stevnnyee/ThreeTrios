package cs3500.threetrios;

import cs3500.threetrios.model.ThreeTriosGameModel;
import cs3500.threetrios.model.Grid;
import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.ThreeTriosGrid;
import cs3500.threetrios.model.ThreeTriosCard;
import cs3500.threetrios.view.ThreeTriosSwingView;
import java.util.ArrayList;
import java.util.List;

public final class ThreeTrios {
  public static void main(String[] args) {
    // Create the model
    ThreeTriosGameModel model = new ThreeTriosGameModel();

    // Create an 8x7 grid
    int rows = 8;
    int cols = 7;
    boolean[][] holes = new boolean[rows][cols];

    // Set left column as holes
    for (int i = 0; i < rows; i++) {
      holes[i][0] = true;
    }

    // Set right column as holes
    for (int i = 0; i < rows; i++) {
      holes[i][cols-1] = true;
    }

    // Set diagonal holes (offset by 1 from left edge)
    for (int i = 0; i < Math.min(rows, cols-2); i++) {
      holes[i][i+1] = true;
    }

    Grid grid = new ThreeTriosGrid(rows, cols, holes);

    // Create cards - adding a lot more to ensure we have enough
    List<Card> deck = new ArrayList<>();

    // Add card2-EnoughCards multiple times
    for (int i = 0; i < 3; i++) { // Adding cards three times to ensure we have enough
      deck.add(new ThreeTriosCard("BlackKnight" + i, 8, 6, 9, 7));
      deck.add(new ThreeTriosCard("BabyDragon" + i, 7, 8, 6, 5));
      deck.add(new ThreeTriosCard("IceWizard" + i, 6, 5, 8, 7));
      deck.add(new ThreeTriosCard("EliteBarbs" + i, 9, 8, 7, 9));
      deck.add(new ThreeTriosCard("Archer" + i, 6, 7, 8, 5));
      deck.add(new ThreeTriosCard("Witch" + i, 7, 5, 8, 6));
      deck.add(new ThreeTriosCard("Goblin" + i, 5, 6, 7, 4));
      deck.add(new ThreeTriosCard("Princess" + i, 6, 4, 8, 5));
      deck.add(new ThreeTriosCard("Prince" + i, 9, 7, 8, 8));
      deck.add(new ThreeTriosCard("Valkyrie" + i, 8, 8, 7, 7));
      deck.add(new ThreeTriosCard("Pekka" + i, 10, 9, 8, 9));
      deck.add(new ThreeTriosCard("Warden" + i, 8, 8, 9, 7));
      deck.add(new ThreeTriosCard("King" + i, 9, 8, 8, 9));
      deck.add(new ThreeTriosCard("Queen" + i, 10, 8, 9, 8));
      deck.add(new ThreeTriosCard("Yeti" + i, 8, 9, 7, 8));
      deck.add(new ThreeTriosCard("HogRider" + i, 9, 6, 10, 7));
      deck.add(new ThreeTriosCard("Skeleton" + i, 4, 3, 5, 4));
      deck.add(new ThreeTriosCard("Balloon" + i, 6, 8, 5, 7));
      deck.add(new ThreeTriosCard("Dragon" + i, 9, 8, 10, 8));
      deck.add(new ThreeTriosCard("Minion" + i, 5, 6, 7, 5));
      deck.add(new ThreeTriosCard("Snowman" + i, 6, 7, 6, 8));
      deck.add(new ThreeTriosCard("Golem" + i, 9, 10, 7, 8));
      deck.add(new ThreeTriosCard("Healer" + i, 5, 7, 6, 5));
      deck.add(new ThreeTriosCard("Miner" + i, 7, 6, 8, 9));
      deck.add(new ThreeTriosCard("ElectroWizard" + i, 8, 7, 9, 8));
      deck.add(new ThreeTriosCard("Mammoth" + i, 5, 8, 9, 2));
    }

    try {
      // Start the game with the grid and deck
      model.startGame(grid, deck);
      // Create and display the view
      ThreeTriosSwingView view = new ThreeTriosSwingView(model);
      view.display();
    } catch (IllegalArgumentException e) {
      System.out.println("Error starting game: " + e.getMessage());
      e.printStackTrace();
    }
  }
}