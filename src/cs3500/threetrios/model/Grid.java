package cs3500.threetrios.model;

import java.util.List;

import javax.swing.text.Position;

public interface Grid {
  int getRows();
  int getCols();
  boolean isHole(int row, int col);
  boolean isEmpty(int row, int col);
  Card getCard(int row, int col);
  void placeCard(int row, int col, Card card);
  int getCardCellCount();
}