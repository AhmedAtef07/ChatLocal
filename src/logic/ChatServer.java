package logic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer {
  private Vector<ConnectedClient> clients;

  public ChatServer(int port) throws Exception {
    clients = new Vector<>();
    ServerSocket server = new ServerSocket(port);
    while(true) {
      Socket clientSocket = server.accept();
      ConnectedClient c = new ConnectedClient(clientSocket);
      clients.add(c);
    }
  }

  private void broadcast(String user, String message) {
    for(ConnectedClient connectedClient : clients) {
      if(connectedClient.username.equals(user)) {
        connectedClient.sendMessage(makeMessage(user, message));
      } else {
        connectedClient.sendMessage(makeMessage(user, message));
      }
    }
  }

  private String makeMessage(String username, String message) {
    return String.format("%s: %s", username, message);
  }

  private class ConnectedClient extends Thread {
    private String username;
    private BufferedReader inputBuffer;
    private PrintWriter outputBuffer;
    private Socket clientSocket;

    public ConnectedClient(Socket clientSocket) throws Exception {
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

    private void messageReceived(String message) {
      if(message.equals("terminateme")) {
        clients.remove(this);
        return;
      }
      broadcast(username, message);
    }

    @Override
    public void run() {
      try {
        while(true) {
          messageReceived(inputBuffer.readLine());
        }
      } catch(Exception ex) {
      }
    }
  }
}