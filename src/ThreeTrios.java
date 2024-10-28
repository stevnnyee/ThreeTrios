import cs3500.threetrios.model.*;
import cs3500.threetrios.view.ThreeTriosView;
import cs3500.threetrios.view.ThreeTriosViewImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ThreeTrios {
  public static void main(String[] args) {
    MainModelInterface model = new MainModelImpl();
    ThreeTriosView view = new ThreeTriosViewImpl(model);
    Scanner scanner = new Scanner(System.in);

    boolean[][] holes = new boolean[3][3];
    Grid grid = new GridImpl(3, 3, holes);

    List<Card> deck = createDeck();

    try {
      model.startGame(grid, deck);

      while (!model.isGameOver()) {
        System.out.println("\n" + view.toString());
        Player currentPlayer = model.getCurrentPlayer();
        System.out.println(currentPlayer.getColor() + "'s turn");
        System.out.print("Enter row (0-2): ");
        int row = scanner.nextInt();
        System.out.print("Enter column (0-2): ");
        int col = scanner.nextInt();
        List<Card> hand = currentPlayer.getHand();
        System.out.println("\nYour cards:");
        for (int i = 0; i < hand.size(); i++) {
          Card card = hand.get(i);
          System.out.printf("%d: %s (N:%d S:%d E:%d W:%d)\n",
                  i, card.getName(),
                  card.getAttackPower(Direction.NORTH),
                  card.getAttackPower(Direction.SOUTH),
                  card.getAttackPower(Direction.EAST),
                  card.getAttackPower(Direction.WEST));
        }

        System.out.print("Choose card (0-" + (hand.size() - 1) + "): ");
        int cardIndex = scanner.nextInt();
        try {
          model.makeMove(row, col, hand.get(cardIndex));
        } catch (IllegalArgumentException | IllegalStateException e) {
          System.out.println("Invalid move: " + e.getMessage());
        }
      }
      System.out.println("\nGame Over!");
      Player winner = model.getWinner();
      if (winner != null) {
        System.out.println("Winner: " + winner.getColor());
      } else {
        System.out.println("It's a tie!");
      }

    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    } finally {
      scanner.close();
    }
  }

  private static List<Card> createDeck() {
    List<Card> deck = new ArrayList<>();

    String[] cardNames = {"Knight", "Dragon", "Wizard", "Warrior", "Archer",
            "Mage", "Rogue", "Paladin", "Monk", "Ninja"};

    for (int i = 0; i < 10; i++) {
      int north = 1 + (int)(Math.random() * 10);
      int south = 1 + (int)(Math.random() * 10);
      int east = 1 + (int)(Math.random() * 10);
      int west = 1 + (int)(Math.random() * 10);

      deck.add(new CardImpl(cardNames[i], north, south, east, west));
    }

    return deck;
  }
}