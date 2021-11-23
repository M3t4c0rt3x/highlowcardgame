package highlowcardgame.game.observable;

import highlowcardgame.game.GameState;

/** Interface representing an observable. */
public interface Observable {

  void subscribe(Observer obsv);

  void unsubscribe(Observer obsv);

  void notifyAboutState(GameState newState);

  void notifyAboutNewPlayer(String playerName, GameState newState);

  void notifyAboutRemovedPlayer(String playerName, GameState newState);
}
