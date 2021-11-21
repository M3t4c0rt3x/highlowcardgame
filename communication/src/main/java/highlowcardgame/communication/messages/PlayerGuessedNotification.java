package highlowcardgame.communication.messages;

/** Class representing a player guessed notification message. */
public final class PlayerGuessedNotification implements Message {

  private final String playerGuessed;
  private int numNotGuessedPlayers;

  public PlayerGuessedNotification(int numNotGuessedPlayers, String playerName) {
    this.playerGuessed = playerName;
    this.numNotGuessedPlayers = numNotGuessedPlayers;
  }

  public String getPlayerName() {
    return playerGuessed;
  }

  public int getNumNotGuessedPlayers() {
    return numNotGuessedPlayers;
  }
}
