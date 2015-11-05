package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
  private String username;
  private PrintWriter serverOutBuffer;
  private BufferedReader serverInBuffer;
  private BufferedReader clientInBuffer;
  private Socket serverSocket;

  public ChatClient(String username, int port) throws IOException {
    this.username = username;
    this.serverSocket = new Socket("localhost", port);

    serverInBuffer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
    serverOutBuffer = new PrintWriter(serverSocket.getOutputStream(), true);

    sendToServer(username);

    new IncomingMessages().start();

    // Inform the server of user disconnection.
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        sendToServer("terminateme");
      }
    });
  }

  public void sendToServer(String message) {
    serverOutBuffer.println(message);
  }

  private void receivedFromServer(String message) {
    //TODO: Send data to GUIHandler when it is implemented;
  }

  class IncomingMessages extends Thread {
    @Override
    public void run() {
      try {
        while (true) {
          receivedFromServer(serverInBuffer.readLine());
        }
      } catch (Exception ex) {
      }
    }
  }
}
