package highlowcardgame.communication.messages;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;
import java.io.IOException;

/** A utility class offering methods to encode to / decode from JSON using the Moshi Libraries. */
public class HandleJson {
  static Moshi moshi =
      new Moshi.Builder()
          .add(
              PolymorphicJsonAdapterFactory.of(Message.class, "messageType")
                  .withSubtype(GameStateNotification.class, "GameStateNotification")
                  .withSubtype(GuessRequest.class, "GuessRequest")
                  .withSubtype(JoinGameRequest.class, "JoinGameRequest")
                  .withSubtype(PlayerGuessedNotification.class, "PlayerGuessedNotification")
                  .withSubtype(PlayerJoinedNotification.class, "PlayerJoinedNotification")
                  .withSubtype(PlayerLeftNotification.class, "PlayerLeftNotification"))
          .build();

  static JsonAdapter<Message> jsonAdapter = moshi.adapter(Message.class);

  public static String encode(Message message) {
    return (jsonAdapter.toJson(message));
  }

  public static Message decode(String inputMessage) throws IOException {
    Message decodedMessage = jsonAdapter.fromJson(inputMessage);
    return decodedMessage;
  }
}
