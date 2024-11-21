package cs3500.threetrios.features;


import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Player;

public interface ViewFeatures {
  void onCardSelected(Player player, Card card);
  void onCellSelected(int row, int col);
}