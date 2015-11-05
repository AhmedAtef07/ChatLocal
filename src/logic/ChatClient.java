package logic;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
  private String username;
  private PrintWriter serverOutputBuffer;
  private BufferedReader serverInputBuffer;
  private Socket serverSocket;
  private GUIController guiController;

  public ChatClient(GUIController guiController, String username, int port) throws IOException {
    this.username = username;
    this.serverSocket = new Socket("localhost", port);
    this.guiController = guiController;

    serverInputBuffer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
    serverOutputBuffer = new PrintWriter(serverSocket.getOutputStream(), true);

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
    serverOutputBuffer.println(message);
  }

  private void receivedFromServer(String message) {
    Platform.runLater(() -> {
      guiController.appendMessage(message);
    });
  }

  class IncomingMessages extends Thread {
    @Override
    public void run() {
      try {
        while(true) {
          receivedFromServer(serverInputBuffer.readLine());
        }
      } catch(Exception ex) {
      }
    }
  }
}
