package highlowcardgame.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

public class MockServerSocket extends ServerSocket {

  private Iterator<MockSocket> sockets;

  public MockServerSocket(List<MockSocket> socketsToAccept) throws IOException {
    sockets = socketsToAccept.iterator();
  }

  @Override
  public Socket accept() {
    if (sockets.hasNext()) {
      return sockets.next();
    }
    // give other logic enough time to do whatever they have to do
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return sockets.next();
  }

  @Override
  public void close() {}
}
