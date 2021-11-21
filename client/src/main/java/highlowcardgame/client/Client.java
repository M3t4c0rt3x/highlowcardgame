package highlowcardgame.client;

import highlowcardgame.communication.messages.GameStateNotification;
import highlowcardgame.communication.messages.GuessRequest;
import highlowcardgame.communication.messages.HandleJson;
import highlowcardgame.communication.messages.JoinGameRequest;
import highlowcardgame.communication.messages.Message;
import highlowcardgame.communication.messages.PlayerGuessedNotification;
import highlowcardgame.communication.messages.PlayerJoinedNotification;
import highlowcardgame.communication.messages.PlayerLeftNotification;
import highlowcardgame.game.HighLowCardGame.Guess;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/** Network client to play a {@link highlowcardgame.game.HighLowCardGame}. */
public final class Client {
  private static final int DEFAULT_PORT = 4441;
  private static final String DEFAULT_ADDRESS = "localhost";
  private static final String DEFAULT_USERNAME = System.getProperty("user.name");

  Shell shell = new Shell(new Scanner(System.in, StandardCharsets.UTF_8), System.out);

  /**
   * Entry to <code>Client</code>.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    // parse arguments
    String username = DEFAULT_USERNAME;
    String serverAddress = DEFAULT_ADDRESS;
    int port = DEFAULT_PORT;
    for (int i = 0; i < args.length; ++i) {
      switch (args[i]) {
        case "--username":
          if (isLastArgument(i, args)) {
            printErrorMessage("Please specify the username.");
            return;
          }
          ++i;
          username = args[i];
          break;
        case "--address":
          if (isLastArgument(i, args)) {
            printErrorMessage("Please specify the server address.");
            return;
          }
          ++i;
          serverAddress = args[i];
          break;
        case "--port":
          if (isLastArgument(i, args)) {
            printErrorMessage("Please specify the port number.");
            return;
          }
          try {
            ++i;
            port = Integer.parseInt(args[i]);
          } catch (NumberFormatException e) {
            printErrorMessage("Invalid port number: " + args[i]);
            return;
          }
          break;
        case "--help":
        default:
          printHelpMessage();
          return;
      }
    }

    // check validity
    if (!isValidName(username)) {
      printErrorMessage("Invalid username: " + username);
      return;
    }

    InetAddress inetAddress = null;
    try {
      inetAddress = InetAddress.getByName(serverAddress);
    } catch (UnknownHostException e) {
      printErrorMessage("Invalid server address: " + serverAddress);
      return;
    }
    assert inetAddress != null;

    if (!isValidPort(port)) {
      printErrorMessage("The port number should be in the range of 1024~65535.");
      return;
    }

    // start a client
    InetSocketAddress address = new InetSocketAddress(inetAddress, port);

    Client client = new Client();
    try (Socket socket = new Socket(address.getAddress(), address.getPort())) {
      client.start(username, socket);
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

  private static boolean isValidName(String username) {
    return username != null && !username.isBlank();
  }

  public static void printHelpMessage() {
    System.out.println(
        "java Client [--username <String>] [--address <String>] [--port <int>] [--help]");
  }

  private static void printErrorMessage(String str) {
    System.out.println("Error! " + str);
  }

  /**
   * Clients start method, handling all the incoming and outgoing messages to/from the corresponding
   * PlayerConnection class.
   *
   * @param username is the name for the registered user of this client
   * @param socket is the socket (of the server), the client connects to
   * @throws IOException collecting all problems when receiving/sending a message
   */
  public void start(String username, Socket socket) throws IOException {
    PrintWriter clientOutput =
        new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
    BufferedReader clientInput =
        new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
    clientOutput.println(HandleJson.encode(new JoinGameRequest(username)));

    boolean gameIsRunning = true;

    while (gameIsRunning) {
      try {
        String clientIn = clientInput.readLine();
        if (clientIn == null) {
          break;
        }
        Message decodedString = HandleJson.decode(clientIn);

        if (decodedString instanceof PlayerJoinedNotification) {
          handlePlayerJoinedNotification(decodedString);
        } else if (decodedString instanceof PlayerLeftNotification) {
          handlePlayerLeftNotification(decodedString);
        } else if (decodedString instanceof PlayerGuessedNotification) {
          handlePlayerGuessedNotification(decodedString);
        } else if (decodedString instanceof GameStateNotification) {
          handleGameStateNotification(decodedString, clientOutput, username);
        } else {
          throw new AssertionError("Unknown message type!");
        }

      } catch (IOException e) {
        gameIsRunning = false;
      }
    }
  }

  private void handlePlayerJoinedNotification(Message decodedString) {
    shell.showServerMessage(decodedString);
  }

  private void handleGameStateNotification(
      Message decodedString, PrintWriter clientOutput, String username) {
    shell.showServerMessage(decodedString);
    Guess actualGuess = shell.getUserInputGuess();
    clientOutput.println(HandleJson.encode(new GuessRequest(actualGuess, username)));
  }

  private void handlePlayerLeftNotification(Message decodedString) {
    shell.showServerMessage(decodedString);
  }

  private void handlePlayerGuessedNotification(Message decodedString) {
    shell.showServerMessage(decodedString);
  }
}
