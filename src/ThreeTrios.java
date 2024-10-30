import cs3500.threetrios.model.*;
import cs3500.threetrios.view.ThreeTriosView;
import cs3500.threetrios.view.ThreeTriosViewImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The ThreeTrios class, the main class to run the game.
 */
public class ThreeTrios {
  public static void main(String[] args) {
    String configPath = "src/cs3500/threetrios/model/configs/";

    try {
      ThreeTriosGameModel model = new ThreeTriosGameModel();
      ThreeTriosViewImpl view = new ThreeTriosViewImpl(model);

      // Test first configuration
      System.out.println("Testing board with no holes (board1) with enough cards (card2):");
      model.startGameFromConfig(
              configPath + "board1-NoHoles",
              configPath + "card2-EnoughCards");
      System.out.println(view.toString());

      // Debug: Print hand size
      System.out.println("DEBUG - Current player hand size: " +
              model.getPlayerHand(model.getCurrentPlayer()).size());
      System.out.println("\n----------------------------------------\n");

      // Continue with other tests...

    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      e.printStackTrace();
    }
  }
}