package highlowcardgame.communication.messages;

/** Class representing a player left notification message. */
public final class PlayerLeftNotification implements Message {

  private final String playerName;
  private int numPlayers;

  public PlayerLeftNotification(int numPlayers, String playerName) {
    this.playerName = playerName;
    this.numPlayers = numPlayers;
  }

  public String getPlayerName() {
    return playerName;
  }

  public int getNumPlayers() {
    return numPlayers;
  }
}
