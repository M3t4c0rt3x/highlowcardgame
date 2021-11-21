package highlowcardgame.communication.messages;

/** Class representing a player joined notification message. */
public final class PlayerJoinedNotification implements Message {

  private final String newPlayerName;
  private int numPlayers;

  public PlayerJoinedNotification(String newPlayerName, int numPlayers) {
    this.newPlayerName = newPlayerName;
    this.numPlayers = numPlayers;
  }

  public String getNewPlayerName() {
    return newPlayerName;
  }

  public int getNumPlayers() {
    return numPlayers;
  }
}
