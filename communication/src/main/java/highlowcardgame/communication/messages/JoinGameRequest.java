package highlowcardgame.communication.messages;

/** Class representing a join game request message. */
public final class JoinGameRequest implements Message {
  private final String playerName;

  public JoinGameRequest(String playerName) {
    this.playerName = playerName;
  }

  public String getPlayerName() {
    return playerName;
  }
}
