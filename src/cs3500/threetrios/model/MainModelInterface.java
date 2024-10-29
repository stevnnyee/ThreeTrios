package cs3500.threetrios.model;

import java.util.List;

import javax.swing.text.Position;

public interface MainModelInterface {

  void startGame(Grid grid, List<Card> deck);

  void makeMove(int row, int col, Card card);

  Grid getGrid();

  Player getCurrentPlayer();


  void executeBattlePhase(MainModelImpl.Position newCardPosition);

  boolean isGameOver();

  Player getWinner();

  boolean placeCard(int row, int col, Card card);

  Player determineWinner();

  void dealCards(List<Card> deck);

  void initialize(Grid grid);
}