package highlowcardgame.communication.messages;

import highlowcardgame.game.HighLowCardGame.Guess;

/** Class representing a guess request message. */
public final class GuessRequest implements Message {

  // private final String messageType = "GuessRequest";
  private final String playerName;
  private final Guess guess;

  public GuessRequest(Guess guess, String playerName) {
    this.playerName = playerName;
    this.guess = guess;
  }

  public String getPlayerName() {
    return playerName;
  }

  public Guess getGuess() {
    return guess;
  }
}
