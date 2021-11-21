package highlowcardgame.server;

import static com.google.common.truth.Truth.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServerRequiredTest {

  private static final String USER1 = "User_1";
  private ByteArrayOutputStream out;

  @BeforeEach
  public void setUp() {
    out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
  }

  @AfterEach
  public void tearDown() throws IOException {
    out.close();
  }

  private void feedInput(String input) {
    System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
  }

  private String getOutput() {
    return out.toString();
  }

  private OutputStream getNetworkOut() {
    return new ByteArrayOutputStream();
  }

  private InputStream getNetworkIn(String input) {
    return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
  }

  private void assertThatContainsNKeyValuePairs(String message, int numberOfPairsExpected) {
    assertThat(message).matches("[^:]*:[^:]*".repeat(numberOfPairsExpected));
  }

  @Test
  public void testServer1_receivesJoinRequest_sendsPlayerJoined() throws IOException {
    Server server = new Server();
    InputStream networkIn =
        getNetworkIn("{\"messageType\":\"JoinGameRequest\",\"playerName\":\"" + USER1 + "\"}");
    OutputStream networkOut = getNetworkOut();
    MockSocket mockSocket = new MockSocket(networkIn, networkOut);
    MockServerSocket serverSocket = new MockServerSocket(List.of(mockSocket));

    try {
      server.start(serverSocket);
    } catch (NoSuchElementException e) {
      // expected, because number of incoming clients will be exhausted quickly.
    }

    String sent = networkOut.toString();
    String[] jsonMessages = sent.split("\n");
    for (String message : jsonMessages) {
      if (message.matches(".*\"messageType\"\\s*:\\s*\"PlayerJoinedNotification\".*")
          && message.matches(".*\"newPlayerName\"\\s*:\\s*\"" + USER1 + "\".*")) {
        assertThat(message).matches(".*\"numPlayers\"\\s*:\\s*1.*");
        assertThatContainsNKeyValuePairs(message, 3);
        return;
      }
    }
    Assertions.fail("No PlayerJoinedNotification for player " + USER1);
  }

  @Test
  public void testServer2_receivesGuessRequestH_handlesGuessAndSendsGameUpdate()
      throws IOException {
    Server server = new Server();
    String joinRequest = "{\"messageType\":\"JoinGameRequest\",\"playerName\":\"" + USER1 + "\"}";
    String guessRequest =
        "{\"messageType\":\"GuessRequest\",\"guess\":\"HIGH\",\"playerName\":\"" + USER1 + "\"}";
    InputStream networkIn = getNetworkIn(joinRequest + System.lineSeparator() + guessRequest);
    OutputStream networkOut = getNetworkOut();
    MockSocket mockSocket = new MockSocket(networkIn, networkOut);
    MockServerSocket serverSocket = new MockServerSocket(List.of(mockSocket));

    try {
      server.start(serverSocket);
    } catch (NoSuchElementException e) {
      // expected, because number of incoming clients will be exhausted quickly.
    }

    String sent = networkOut.toString();
    String[] jsonMessages = sent.split("\n");
    for (String message : jsonMessages) {
      if (message.matches(".*\"messageType\"\\s*:\\s*\"GameStateNotification\".*")
          && message.matches(".*\"playerName\"\\s*:\\s*\"" + USER1 + "\".*")
          && message.matches(".*\"numRounds\"\\s*:\\s*2.*")) {
        assertThat(message).matches(".*\"messageType\"\\s*:\\s*\"GameStateNotification\".*");
        assertThat(message).matches(".*\"playerName\"\\s*:\\s*\"" + USER1 + "\".*");
        assertThat(message).matches(".*\"currentCard\"\\s*:\\s*\\{[^}]+}.*");
        assertThat(message).matches(".*\"suit\"\\s*:\\s*\"(CLUBS|DIAMONDS|HEARTS|SPADES)\".*");
        assertThat(message).matches(".*\"value\"\\s*:\\s*([0-9]|1[0-3]).*");
        assertThat(message).matches(".*\"numRounds\"\\s*:\\s*2.*");
        assertThat(message).matches(".*\"score\"\\s*:\\s*[01].*");
        assertThatContainsNKeyValuePairs(message, 7);
        return;
      }
    }
    Assertions.fail("No GameStateNotification for player " + USER1 + " and round 2");
  }

  @Test
  public void testServer2_receivesGuessRequestL_handlesGuessAndSendsGameUpdate()
      throws IOException {
    Server server = new Server();
    String joinRequest = "{\"messageType\":\"JoinGameRequest\",\"playerName\":\"" + USER1 + "\"}";
    String guessRequest =
        "{\"messageType\":\"GuessRequest\",\"guess\":\"LOW\",\"playerName\":\"" + USER1 + "\"}";
    InputStream networkIn = getNetworkIn(joinRequest + System.lineSeparator() + guessRequest);
    OutputStream networkOut = getNetworkOut();
    MockSocket mockSocket = new MockSocket(networkIn, networkOut);
    MockServerSocket serverSocket = new MockServerSocket(List.of(mockSocket));

    try {
      server.start(serverSocket);
    } catch (NoSuchElementException e) {
      // expected, because number of incoming clients will be exhausted quickly.
    }

    String sent = networkOut.toString();
    String[] jsonMessages = sent.split("\n");
    for (String message : jsonMessages) {
      if (message.matches(".*\"messageType\"\\s*:\\s*\"GameStateNotification\".*")
          && message.matches(".*\"playerName\"\\s*:\\s*\"" + USER1 + "\".*")
          && message.matches(".*\"numRounds\"\\s*:\\s*2.*")) {
        assertThat(message).matches(".*\"messageType\"\\s*:\\s*\"GameStateNotification\".*");
        assertThat(message).matches(".*\"playerName\"\\s*:\\s*\"" + USER1 + "\".*");
        assertThat(message).matches(".*\"currentCard\"\\s*:\\s*\\{[^}]+}.*");
        assertThat(message).matches(".*\"suit\"\\s*:\\s*\"(CLUBS|DIAMONDS|HEARTS|SPADES)\".*");
        assertThat(message).matches(".*\"value\"\\s*:\\s*([0-9]|1[0-3]).*");
        assertThat(message).matches(".*\"numRounds\"\\s*:\\s*2.*");
        assertThat(message).matches(".*\"score\"\\s*:\\s*[01].*");
        assertThatContainsNKeyValuePairs(message, 7);
        return;
      }
    }
    Assertions.fail("No GameStateNotification for player " + USER1 + " and round 2");
  }

  @Test
  public void testServer2_receivesGuessRequestE_handlesGuessAndSendsGameUpdate()
      throws IOException {
    Server server = new Server();
    String joinRequest = "{\"messageType\":\"JoinGameRequest\",\"playerName\":\"" + USER1 + "\"}";
    String guessRequest =
        "{\"messageType\":\"GuessRequest\",\"guess\":\"EQUAL\",\"playerName\":\"" + USER1 + "\"}";
    InputStream networkIn = getNetworkIn(joinRequest + System.lineSeparator() + guessRequest);
    OutputStream networkOut = getNetworkOut();
    MockSocket mockSocket = new MockSocket(networkIn, networkOut);
    MockServerSocket serverSocket = new MockServerSocket(List.of(mockSocket));

    try {
      server.start(serverSocket);
    } catch (NoSuchElementException e) {
      // expected, because number of incoming clients will be exhausted quickly.
    }

    String sent = networkOut.toString();
    String[] jsonMessages = sent.split("\n");
    for (String message : jsonMessages) {
      if (message.matches(".*\"messageType\"\\s*:\\s*\"GameStateNotification\".*")
          && message.matches(".*\"playerName\"\\s*:\\s*\"" + USER1 + "\".*")
          && message.matches(".*\"numRounds\"\\s*:\\s*2.*")) {
        assertThat(message).matches(".*\"messageType\"\\s*:\\s*\"GameStateNotification\".*");
        assertThat(message).matches(".*\"playerName\"\\s*:\\s*\"" + USER1 + "\".*");
        assertThat(message).matches(".*\"currentCard\"\\s*:\\s*\\{[^}]+}.*");
        assertThat(message).matches(".*\"suit\"\\s*:\\s*\"(CLUBS|DIAMONDS|HEARTS|SPADES)\".*");
        assertThat(message).matches(".*\"value\"\\s*:\\s*([0-9]|1[0-3]).*");
        assertThat(message).matches(".*\"numRounds\"\\s*:\\s*2.*");
        assertThat(message).matches(".*\"score\"\\s*:\\s*[01].*");
        assertThatContainsNKeyValuePairs(message, 7);
        return;
      }
    }
    Assertions.fail("No GameStateNotification for player " + USER1 + " and round 2");
  }
}
