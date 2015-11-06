package logic;

import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class ChatClient {

  protected byte[] sendData = new byte[1024];
  protected byte[] receiveData = new byte[1024];
  private DataInputStream inputStream;
  private DataOutputStream outputStream;
  private String username;
  private Socket serverSocket;
  private GUIController guiController;

  public ChatClient(String serverIP, GUIController guiController, String username, int port)
          throws Exception {
    this.username = username;
    this.serverSocket = new Socket(serverIP, port);
    this.guiController = guiController;

    inputStream = new DataInputStream(serverSocket.getInputStream());
    outputStream = new DataOutputStream(serverSocket.getOutputStream());

    sendToServer(username);

    new IncomingMessages().start();
    String ipAddress = "This IP: " + getCurrentEnvironmentNetworkIp();
    byte[] data = DataConversion.bytesFromString(ipAddress);
    receivedFromServer(data);

    // Inform the server of user disconnection.
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          sendToServer("terminateme");
        } catch(Exception ex) {
          ex.printStackTrace();
        }
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

  public void sendToServer(String message) throws Exception {
    sendData = DataConversion.bytesFromString(message);
    StreamsTraffic.writeMessage(outputStream, sendData, sendData.length);
  }

  private void receivedFromServer(byte[] message) {
    Platform.runLater(() -> {
      String data = DataConversion.bytesToString(message);
      guiController.appendMessage(data);
    });
  }

  class IncomingMessages extends Thread {
    @Override
    public void run() {
      try {
        while(true) {
          receiveData = StreamsTraffic.readMessage(inputStream);
          receivedFromServer(receiveData);
        }
      } catch(Exception ex) {
      }
    }
  }

}
