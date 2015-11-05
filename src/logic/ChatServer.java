package logic;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import static java.lang.System.out;

public class ChatServer {
  Vector<String> usernames = new Vector<String>();
  Vector<ConnectedClient> clients = new Vector<ConnectedClient>();
  PrintWriter logger;

  public ChatServer(int port) throws Exception {
    ServerSocket server = new ServerSocket(port, 10);
    logger = initLogger();
    out.println("Server Started...");
    while (true) {
      Socket clientSocket = server.accept();
      ConnectedClient c = new ConnectedClient(clientSocket);
      clients.add(c);
    }
  }

  private PrintWriter initLogger() throws FileNotFoundException {
    File logFile = new File("server_log_" + System.currentTimeMillis());
    return new PrintWriter(logFile);
  }

  public void broadcast(String user, String message) {
    for (ConnectedClient c : clients) {
      if (c.getUsername().equals(user)) {
        // c.sendMessage("Server", "[" + message + "] was sent.");
        c.sendMessage(user, message);
      } else {
        c.sendMessage(user, message);
      }
    }
  }

  class ConnectedClient extends Thread {
    private String username;
    private BufferedReader input;
    private PrintWriter output;
    private Socket clientSocket;

    public ConnectedClient(Socket clientSocket) throws Exception {
      this.clientSocket = clientSocket;
      this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      this.output = new PrintWriter(clientSocket.getOutputStream(), true);

      this.username = input.readLine();
      log("---User Connected---");

      usernames.add(this.username);
      start();
    }

    public void sendMessage(String username, String msg) {
      // output.println("\u001B[32m" + username + ": " + msg + "\u001B[0m");
      output.println(username + ": " + msg);
      output.flush();
    }

    public String getUsername() {
      return username;
    }

    private void log(String line) {
      String raw = String.format("%d:%s:%s:%s", System.currentTimeMillis(),
              clientSocket.getRemoteSocketAddress(), username, line);

      out.println(raw);
      out.flush();

      logger.println(raw);
      logger.flush();
    }

    @Override
    public void run() {
      try {
        while (true) {
          String line = input.readLine();
          log(line);

          if (line.equals("terminateme")) {
            clients.remove(this);
            usernames.remove(username);
            break;
          }

          broadcast(username, line);
        }
      } catch (Exception ex) {
        System.out.println(ex.getMessage());
      }
    }
  }

}