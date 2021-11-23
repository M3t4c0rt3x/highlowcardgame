package highlowcardgame.game;

/** Class saving and offering the score. */
public class Score {
  private volatile int score = 0;

  public synchronized void increment(int inc) {
    score += inc;
  }

  public int get() {
    return score;
  }
}
