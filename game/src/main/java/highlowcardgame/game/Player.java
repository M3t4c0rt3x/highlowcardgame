package highlowcardgame.game;

import highlowcardgame.game.observable.Observer;

/** Interface representing a player. */
public interface Player extends Observer {
  String getName();
}
