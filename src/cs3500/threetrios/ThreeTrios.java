package cs3500.threetrios;

import cs3500.threetrios.model.ThreeTriosGameModel;
import cs3500.threetrios.model.Grid;
import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.ThreeTriosGrid;
import cs3500.threetrios.model.ThreeTriosCard;
import cs3500.threetrios.view.ThreeTriosSwingView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class ThreeTrios {
  public static void main(String[] args) {
    ThreeTriosGameModel model = new ThreeTriosGameModel();
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

    try {
      model.startGame(grid, deck);
      ThreeTriosSwingView view = new ThreeTriosSwingView(model);
      view.setTitle("Selecting First Move...");
      view.display();

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      model.setCurrentPlayer(Math.random() < 0.5 ? "RED" : "BLUE");
      view.refresh();

    } catch (IllegalArgumentException e) {
      System.out.println("Error starting game: " + e.getMessage());
      e.printStackTrace();
    }
  }
}