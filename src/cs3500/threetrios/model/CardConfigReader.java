package cs3500.threetrios.model;

import java.util.List;

public interface CardConfigReader {
  List<Card> readCards(String filename);
}