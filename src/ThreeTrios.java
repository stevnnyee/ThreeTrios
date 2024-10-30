import cs3500.threetrios.model.*;
import cs3500.threetrios.view.ThreeTriosView;
import cs3500.threetrios.view.ThreeTriosViewImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ThreeTrios {
  public static void main(String[] args) {
    MainModelInterface model = new ThreeTriosGameModel();
    ThreeTriosView view = new ThreeTriosViewImpl(model);
    Scanner scanner = new Scanner(System.in);

    try {
      // Reading configurations from files
      File gridFile = new File("path/to/grid/configuration/file.txt");
      File cardFile = new File("path/to/card/configuration/file.txt");

      Grid grid = createGridFromConfig(gridFile);
      List<Card> deck = createDeckFromConfig(cardFile);

      model.startGame(grid, deck);

      // Main game loop
      while (!model.isGameOver()) {
        System.out.println("\n" + view.toString());
        Player currentPlayer = model.getCurrentPlayer();
        System.out.println(currentPlayer.getColor() + "'s turn");
        handlePlayerTurn(scanner, model, currentPlayer);
      }
      displayGameOver(model);
    } catch (FileNotFoundException e) {
      System.out.println("Configuration file not found: " + e.getMessage());
    } finally {
      scanner.close();
    }
  }

  private static void handlePlayerTurn(Scanner scanner, MainModelInterface model, Player currentPlayer) {
    int row = getIntFromUser(scanner, "Enter row (0-2): ", 0, 2);
    int col = getIntFromUser(scanner, "Enter column (0-2): ", 0, 2);

    System.out.println("\nYour cards:");
    List<Card> hand = currentPlayer.getHand();
    for (int i = 0; i < hand.size(); i++) {
      Card card = hand.get(i);
      System.out.printf("%d: %s (N:%d S:%d E:%d W:%d)\n", i, card.getName(),
              card.getAttackPower(Direction.NORTH), card.getAttackPower(Direction.SOUTH),
              card.getAttackPower(Direction.EAST), card.getAttackPower(Direction.WEST));
    }

    int cardIndex = getIntFromUser(scanner, "Choose card (0-" + (hand.size() - 1) + "): ", 0, hand.size() - 1);
    try {
      model.placeCard(currentPlayer, row, col, hand.get(cardIndex));
    } catch (IllegalArgumentException | IllegalStateException e) {
      System.out.println("Invalid move: " + e.getMessage());
    }
  }

  private static int getIntFromUser(Scanner scanner, String prompt, int min, int max) {
    int input = -1;
    while (input < min || input > max) {
      System.out.print(prompt);
      if (scanner.hasNextInt()) {
        input = scanner.nextInt();
        if (input < min || input > max) {
          System.out.println("Invalid input. Number must be between " + min + " and " + max + ".");
        }
      } else {
        System.out.println("Invalid input.");
        scanner.next(); // consume the invalid input
      }
    }
    return input;
  }

  private static void displayGameOver(MainModelInterface model) {
    System.out.println("\nGame Over!");
    Player winner = model.getWinner();
    if (winner != null) {
      System.out.println("Winner: " + winner.getColor());
    } else {
      System.out.println("It's a tie!");
    }
  }

  private static Grid createGridFromConfig(File gridFile) throws FileNotFoundException {
    Scanner scanner = new Scanner(gridFile);
    int rows = scanner.nextInt();
    int cols = scanner.nextInt();
    boolean[][] holes = new boolean[rows][cols];

    for (int i = 0; i < rows; i++) {
      String line = scanner.next();
      for (int j = 0; j < cols; j++) {
        holes[i][j] = line.charAt(j) == 'X';
      }
    }
    scanner.close();
    return new ThreeTriosGrid(rows, cols, holes);
  }

  private static List<Card> createDeckFromConfig(File cardFile) throws FileNotFoundException {
    Scanner scanner = new Scanner(cardFile);
    List<Card> deck = new ArrayList<>();
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] parts = line.split(" ");
      String cardName = parts[0];
      int north = Integer.parseInt(parts[1]);
      int south = Integer.parseInt(parts[2]);
      int east = Integer.parseInt(parts[3]);
      int west = Integer.parseInt(parts[4]);
      deck.add(new ThreeTriosCard(cardName, north, south, east, west));
    }
    scanner.close();
    return deck;
  }
}
