package highlowcardgame.server;

import highlowcardgame.communication.messages.GameStateNotification;
import highlowcardgame.communication.messages.GuessRequest;
import highlowcardgame.communication.messages.HandleJson;
import highlowcardgame.communication.messages.JoinGameRequest;
import highlowcardgame.communication.messages.Message;
import highlowcardgame.communication.messages.PlayerGuessedNotification;
import highlowcardgame.communication.messages.PlayerJoinedNotification;
import highlowcardgame.communication.messages.PlayerLeftNotification;
import highlowcardgame.game.Card;
import highlowcardgame.game.Deck.NoNextCardException;
import highlowcardgame.game.GameState;
import highlowcardgame.game.HighLowCardGame;
import highlowcardgame.game.HighLowCardGame.Guess;
import highlowcardgame.game.Player;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/** A player like class representing the server-sided part to communicate with a client. */
public class PlayerConnection implements Player {

  private final Socket socket;
  private final HighLowCardGame game;
  private String playerName;

  public PlayerConnection(Socket socket, HighLowCardGame game) {
    this.socket = socket;
    this.game = game;
  }

  void reactToInput() throws IOException, NoNextCardException {

    boolean isRunning = true;

    try (BufferedReader pcInput =
        new BufferedReader(
            new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
      while (true) {
        String pcIn = pcInput.readLine();
        if (pcIn == null) {
          break;
        }

        Message decodedString = HandleJson.decode(pcIn);

        PrintWriter pcOutput =
            new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
        // joinGameRequest
        if (decodedString instanceof JoinGameRequest) {
          playerName = (((JoinGameRequest) decodedString).getPlayerName());
          System.err.println("Welcome to the Game, " + playerName + "!");
          game.addPlayer(this);

          // guessRequest
        } else if (decodedString instanceof GuessRequest) {
          String playerName = (((GuessRequest) decodedString).getPlayerName());
          Guess actualGuess = (((GuessRequest) decodedString).getGuess());
          GameState actualGuessedState = game.getState().addGuess(this, actualGuess);
          int numNotGuessedPlayers =
              actualGuessedState.getPlayers().size() - actualGuessedState.getGuesses().size();
          pcOutput.println(
              HandleJson.encode(new PlayerGuessedNotification(numNotGuessedPlayers, playerName)));
          game.guess(this, actualGuess);
        } else {
          throw new AssertionError("Unknown message type!");
        }
      }
    }
  }

  @Override
  public String getName() {
    return playerName;
  }

  @Override
  public void updateState(GameState state) {
    try {
      PrintWriter pcOutput =
          new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
      // playerName = getName();
      int currentRound = state.getRound();
      Card currentCard = state.getCurrentCard();
      int currentScore = state.getScores().get(this).get();
      pcOutput.println(
          HandleJson.encode(
              new GameStateNotification(currentCard, currentRound, playerName, currentScore)));
    } catch (IOException e) {
      throw new AssertionError("Shit happens.");
    }
  }

  @Override
  public void updateNewPlayer(String playerName, GameState state) {
    // updateState(state);
    try {
      PrintWriter pcOutput =
          new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
      pcOutput.println(
          HandleJson.encode(new PlayerJoinedNotification(playerName, state.getPlayers().size())));
    } catch (IOException e) {
      throw new AssertionError("Player left the game.");
    }
    updateState(state);
  }

  @Override
  public void updateRemovedPlayer(String playerName, GameState state) {
    updateState(state);
    try {
      PrintWriter pcOutput =
          new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
      pcOutput.println(
          HandleJson.encode(new PlayerLeftNotification(state.getPlayers().size(), playerName)));
    } catch (IOException e) {
      throw new AssertionError("Player left the game.");
    }
  }
}
