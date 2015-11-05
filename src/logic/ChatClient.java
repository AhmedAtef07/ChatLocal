package logic;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class ChatClient {
  private String username;
  private PrintWriter serverOutputBuffer;
  private BufferedReader serverInputBuffer;
  private Socket serverSocket;
  private GUIController guiController;

  public ChatClient(String serverIP, GUIController guiController, String username, int port) throws IOException {
    this.username = username;
    this.serverSocket = new Socket(serverIP, port);
    this.guiController = guiController;

    serverInputBuffer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
    serverOutputBuffer = new PrintWriter(serverSocket.getOutputStream(), true);

    sendToServer(username);

    new IncomingMessages().start();
    receivedFromServer("This IP: " + getCurrentEnvironmentNetworkIp());

    // Inform the server of user disconnection.
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        sendToServer("terminateme");
      }
    });
  }

  private String getCurrentEnvironmentNetworkIp() {
    String currentHostIpAddress = null;
    Enumeration<NetworkInterface> netInterfaces = null;
    try {
      netInterfaces = NetworkInterface.getNetworkInterfaces();
      while(netInterfaces.hasMoreElements()) {
        NetworkInterface ni = netInterfaces.nextElement();
        Enumeration<InetAddress> address = ni.getInetAddresses();
        while(address.hasMoreElements()) {
          InetAddress addr = address.nextElement();
          if(!addr.isLoopbackAddress()
                  && addr.isSiteLocalAddress()
                  && !addr.getHostAddress().contains(":")) {
            currentHostIpAddress = addr.getHostAddress();
          }
        }
      }
      if(currentHostIpAddress == null) {
        currentHostIpAddress = "127.0.0.1";
      }

    } catch(SocketException e) {
      currentHostIpAddress = "127.0.0.1";
    }
    return currentHostIpAddress;
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
