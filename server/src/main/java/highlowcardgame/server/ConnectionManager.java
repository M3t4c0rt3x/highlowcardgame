package highlowcardgame.server;

import java.util.ArrayList;
import java.util.List;

/** Class for managing multiple PlayerConnections. */
public class ConnectionManager {

  List<PlayerConnection> managedClients = new ArrayList<>();

  public ConnectionManager() {}

  /**
   * Method for adding a (new) PlayerConnection to the managed List.
   *
   * @param playerConnection is the reference to the instanciated playerConnection
   */
  public void addClient(PlayerConnection playerConnection) {
    managedClients.add(playerConnection);
  }

  /**
   * Method for removing a PlayerConnection from the managed List.
   *
   * @param playerConnection is the reference to the instanciated playerConnection.
   */
  public void removeClient(PlayerConnection playerConnection) {
    if (managedClients.contains(playerConnection)) {
      managedClients.remove(playerConnection);
    }
  }

  /**
   * Method for returning the whole List of managed PlayerConnections.
   *
   * @return is the List of managed PlayerConnections.
   */
  public List<PlayerConnection> getAllClients() {
    return managedClients;
  }
}
