package highlowcardgame.server;

import highlowcardgame.game.Card;
import highlowcardgame.game.Deck.NoNextCardException;
import highlowcardgame.game.HighLowCardGame;
import highlowcardgame.game.InfiniteShuffledDeck;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Main class for the game server. The class starts the server sockets and delegates connection and
 * game handling to {@link ConnectionManager}.
 */
public class Server {
  private static final int DEFAULT_PORT = 4441;

  /**
   * Main method for the server.
   *
   * @param args Commandline arguments
   */
  public static void main(final String[] args) {
    int port = DEFAULT_PORT;
    for (int i = 0; i < args.length; ++i) {
      switch (args[i]) {
        case "--port":
          if (isLastArgument(i, args)) {
            printErrorMessage("Please specify the port number.");
            return;
          }
          try {
            i++;
            port = Integer.parseInt(args[i]);
          } catch (NumberFormatException e) {
            printErrorMessage("Invalid port number: " + args[i]);
            return;
          }
          if (!isValidPort(port)) {
            printErrorMessage("The port number should be in the range of 1024~65535.");
            return;
          }
          break;
        case "--help":
        default:
          printHelpMessage();
          return;
      }
    }

    try (ServerSocket socket = new ServerSocket(port)) {
      Server server = new Server();
      server.start(socket);
    } catch (IOException e) {
      System.out.println("Connection lost. Shutting down: " + e.getMessage());
    }
  }

  private static boolean isLastArgument(int i, final String[] args) {
    return i == args.length - 1;
  }

  private static boolean isValidPort(int port) {
    return port >= 1024 && port <= 65535;
  }

  private static void printHelpMessage() {
    System.out.println("java Server [--port <int>] [--help]");
  }

  private static void printErrorMessage(String str) {
    System.out.println("Error! " + str);
  }

  /**
   * method for listening to a socket for establishing a client-server-connection and handling this.
   *
   * @param socket is the socket, the server listens to
   * @throws IOException is thrown, when something goes wrong with the connection via the socket
   */
  public void start(ServerSocket socket) throws IOException {
    int count = 0;
    // ConnectionManager cm = new ConnectionManager();
    System.out.println("Server is Started ....");

    // starting a game
    HighLowCardGame game = new HighLowCardGame(new InfiniteShuffledDeck(Card.getAllValidCards()));
    try {
      game.start();
      while (count != -1) {
        count++;
        Socket acceptedSocket = socket.accept();
        PlayerConnection pc = new PlayerConnection(acceptedSocket, game);
        new Thread(pc).start();
      }
    } catch (IOException | NoNextCardException e) {
      System.err.println(e);
    }
    finally {
      if (socket != null) {
        try {
          socket.close();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
