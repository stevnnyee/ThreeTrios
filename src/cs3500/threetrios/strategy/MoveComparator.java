package cs3500.threetrios.strategy;

import java.util.Comparator;

public class MoveComparator implements Comparator<AIMove> {
  @Override
  public int compare(AIMove a, AIMove b) {
    if (a.getScore() != b.getScore()) {
      return Integer.compare(b.getScore(), a.getScore());
    }
    return a.comparePosition(b);
  }
}
