package highlowcardgame.communication.messages;

import highlowcardgame.game.Card;

/** Class representing a Notification for a changed GameState. */
public final class GameStateNotification implements Message {

  private final String playerName;
  private int numRounds;
  private Card currentCard;
  private int score;

  /**
   * Constructor for initializing all relevant GameState values.
   *
   * @param currentCard represents the current card
   * @param numRounds represents the actual count of rounds
   * @param playerName represents the player name
   * @param score represents the actual score
   */
  public GameStateNotification(Card currentCard, int numRounds, String playerName, int score) {
    this.playerName = playerName;
    this.numRounds = numRounds;
    this.currentCard = currentCard;
    this.score = score;
  }

  public String getPlayerName() {
    return playerName;
  }

  public int getNumRounds() {
    return numRounds;
  }

  public Card getCurrentCard() {
    return currentCard;
  }

  public int getScore() {
    return score;
  }
}
