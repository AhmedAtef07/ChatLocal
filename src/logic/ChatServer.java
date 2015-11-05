package logic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer extends Thread {

  private Vector<ConnectedClient> clients;
  private ServerSocket server;

  public ChatServer(int port) throws IOException {
    clients = new Vector<>();
    server = new ServerSocket(port);

    // Start waiting for clients to connect on detached thread;
    start();
  }

  @Override
  public void run() {
    try {
      while(true) {
        Socket clientSocket = server.accept();
        ConnectedClient c = new ConnectedClient(clientSocket);
        clients.add(c);
      }
    } catch(Exception ex) {
    }
  }

  private void broadcast(String user, byte[] message) throws IOException {
    for(ConnectedClient connectedClient : clients) {
      if(connectedClient.username.equals(user)) {
        connectedClient.sendMessage(makeMessage(user, message));
      } else {
        connectedClient.sendMessage(makeMessage(user, message));
      }
    }
  }

  private String makeMessage(String username, byte[] message) {
    String data = DataConversion.bytesToString(message);
    return String.format("%s: %s", username, data);
  }

  private class ConnectedClient extends Thread {
    private String username;
    private Socket clientSocket;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private byte[] sendData = new byte[1024];
    private byte[] receiveData = new byte[1024];

    public ConnectedClient(Socket clientSocket) throws IOException {
      this.clientSocket = clientSocket;
      this.inputStream = new DataInputStream(clientSocket.getInputStream());
      this.outputStream = new DataOutputStream(clientSocket.getOutputStream());

      // This is the first thing agreed with the client to send.
      receiveData = StreamsTraffic.readMessage(inputStream);
      this.username = DataConversion.bytesToString(receiveData);

      // Listen to any incoming messages on an external
      start();
    }

    private void sendMessage(String message) throws IOException {
      sendData = DataConversion.bytesFromString(message);
      StreamsTraffic.writeMessage(outputStream, sendData, sendData.length);
    }

    private void messageReceived(byte[] message) throws IOException {
      String data = DataConversion.bytesToString(message);
      if(data.equals("terminateme")) {
        clients.remove(this);
        return;
      }
      broadcast(username, message);
    }

    @Override
    public void run() {
      try {
        while(true) {
          receiveData = StreamsTraffic.readMessage(inputStream);
          messageReceived(receiveData);
        }
      } catch(Exception ex) {
      }
    }
  }

}