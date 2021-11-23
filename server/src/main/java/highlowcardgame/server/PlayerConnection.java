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
public class PlayerConnection implements Player, Runnable {

  private final Socket socket;
  private final HighLowCardGame game;
  private String playerName;

  public PlayerConnection(Socket socket, HighLowCardGame game) {
    this.socket = socket;
    this.game = game;
  }

  /**
   * Main method of the thread, handling incoming and outgoing "server" messages to its
   * corresponding client.
   */
  public void run() {
    PrintWriter pcOutput = null;
    BufferedReader pcInput = null;
    try {
      pcInput =
          new BufferedReader(
              new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

      while (true) {
        String pcIn = pcInput.readLine();
        if (pcIn == null) {
          break;
        }

        Message decodedString = HandleJson.decode(pcIn);

        pcOutput = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
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
    } catch (IOException | NoNextCardException e) {
      //e.printStackTrace();
      game.removePlayer(this);
    } finally {
      try {
        if (pcOutput != null) {
          pcOutput.close();
          game.removePlayer(this);
        }
        if (pcInput != null) {
          pcInput.close();
          socket.close();
          game.removePlayer(this);
        }
      } catch (IOException e) {
        e.printStackTrace();
        game.removePlayer(this);
      } finally {
        game.removePlayer(this);
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
