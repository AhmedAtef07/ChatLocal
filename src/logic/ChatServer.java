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

  private void broadcast(String user, String textMessage) throws IOException {
    for(ConnectedClient connectedClient : clients) {
      if(connectedClient.username.equals(user)) {
        connectedClient.sendMessage(makeMessage(user, textMessage));
      } else {
        connectedClient.sendMessage(makeMessage(user, textMessage));
      }
    }
  }

  private String makeMessage(String username, String textMessage) {
    return String.format("%s: %s", username, textMessage);
  }

  private class ConnectedClient extends Thread {
    private String username;
    private Socket clientSocket;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public ConnectedClient(Socket clientSocket) throws IOException {
      this.clientSocket = clientSocket;
      this.dataInputStream = new DataInputStream(clientSocket.getInputStream());
      this.dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

      // This is the first thing agreed with the client to send.
      NetworkMessage usernameMessage = new NetworkMessage(dataInputStream);
      this.username = usernameMessage.getContent().toString();

      // Listen to any incoming messages on an external
      start();
    }

    private void sendMessage(String message) throws IOException {
      NetworkMessage textMessage = new NetworkMessage(MessageType.TEXT, message);
      textMessage.send(dataOutputStream);
    }

    private void messageReceived(NetworkMessage newMessage) throws IOException {
      if(newMessage.getType().equals(MessageType.SIGNAL)) {
        if(((Signal) newMessage.getContent()).equals(Signal.USER_DISCONNECTED)) {
          clients.remove(this);
          return;
        }
      }
      broadcast(username, newMessage.getContent().toString());
    }

    @Override
    public void run() {
      try {
        while(true) {
          messageReceived(new NetworkMessage(dataInputStream));
        }
      } catch(Exception ex) {
      }
    }
  }

}