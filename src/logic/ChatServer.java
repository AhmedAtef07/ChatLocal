package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

  private void broadcast(String user, byte[] message) {
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
    private BufferedReader inputBuffer;
    private PrintWriter outputBuffer;
    private Socket clientSocket;

    public ConnectedClient(Socket clientSocket) throws IOException {
      this.clientSocket = clientSocket;
      this.inputBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      this.outputBuffer = new PrintWriter(clientSocket.getOutputStream(), true);

      // This is the first thing agreed with the client to send.
      this.username = inputBuffer.readLine();

      // Listen to any incoming messages on an external
      start();
    }

    private void sendMessage(String message) {
      outputBuffer.println(message);
      outputBuffer.flush();
    }

    private void messageReceived(byte[] message) {
      String data = DataConversion.bytesToString(message);
      if (data.equals("terminateme")) {
        clients.remove(this);
        return;
      }
      broadcast(username, message);
    }

    @Override
    public void run() {
      try {
        while(true) {
          String data = inputBuffer.readLine();
          byte[] message = DataConversion.bytesFromString(data);
          messageReceived(message);
        }
      } catch(Exception ex) {
      }
    }
  }

}