package cs3500.threetrios.strategy;

import java.util.Comparator;

/**
 *  A comparator class that orders AIMove objects primarily by score and secondarily by position.
 *  This comparator creates a total ordering of moves that prioritizes higher scores.
 */
public class MoveComparator implements Comparator<AIMove> {

  @Override
  public int compare(AIMove a, AIMove b) {
    if (a.getScore() != b.getScore()) {
      return Integer.compare(b.getScore(), a.getScore());
    }
    return a.comparePosition(b);
  }
}
