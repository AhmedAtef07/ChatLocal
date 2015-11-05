package logic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.System.out;

public class ChatClient extends JFrame implements ActionListener {
  BufferedReader br;
  JTextArea taMessages;
  JTextField tfInput;
  JButton btnSend;
  private String username;
  private PrintWriter serverOutBuffer;
  private BufferedReader serverInBuffer;
  private BufferedReader clientInBuffer;
  private Socket serverSocket;

  public ChatClient(String username, String servername, int port) throws Exception {
    this.username = username;
    this.serverSocket = new Socket(servername, port);

    serverInBuffer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
    serverOutBuffer = new PrintWriter(serverSocket.getOutputStream(), true);

    clientInBuffer = new BufferedReader(new InputStreamReader(System.in));

    serverOutBuffer.println(username);
    out.printf("Hi %s! Start talking...\n", username);

    buildInterface();

    new IncomingMessages().start();
    new OutgoingMessages().start();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Infrom the server of user disconnection.
        serverOutBuffer.println("terminateme");
      }
    });
  }

  public static void main(String[] args) {
    // String servername = args[0];
    // int port = Integer.parseInt(args[1]);
    // String name = args[2];

    String servername = "localhost";
    int port = 7080;
    String name = JOptionPane.showInputDialog(null, "Enter your name :", "Username",
            JOptionPane.PLAIN_MESSAGE);

    try {
      new ChatClient(name, servername, port);
    } catch (Exception ex) {
      out.println("Error ==> " + ex.getMessage());
    }
  }

  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == btnSend) {
      sendMessageFromGUI();
    }
  }

  private void sendMessageFromGUI() {
    serverOutBuffer.println(tfInput.getText());
    tfInput.setText("");
  }


  public void buildInterface() {
    btnSend = new JButton("Send");
    taMessages = new JTextArea();
    taMessages.setRows(10);
    taMessages.setColumns(50);
    taMessages.setEditable(false);
    tfInput = new JTextField(50);
    JScrollPane sp = new JScrollPane(taMessages, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    add(sp, "Center");
    JPanel bp = new JPanel(new FlowLayout());
    bp.add(tfInput);
    bp.add(btnSend);
    add(bp, "South");
    btnSend.addActionListener(this);
    setSize(500, 300);
    setVisible(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    KeyListener keyListener = new KeyListener() {
      public void keyPressed(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if (KeyEvent.getKeyText(keyCode).equals("Enter")) {
          sendMessageFromGUI();
        } else if (KeyEvent.getKeyText(keyCode).equals("Escape")) {
          System.exit(0);
        }
      }

      public void keyReleased(KeyEvent keyEvent) {
      }

      public void keyTyped(KeyEvent keyEvent) {
      }
    };

    tfInput.addKeyListener(keyListener);
    tfInput.requestFocusInWindow();

    pack();

//        GridPane grid = new GridPane();
//
//        grid.setAlignment(Pos.CENTER);
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new javafx.geometry.Insets(10));
//
//        javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea();
////        textArea.setDisable(true);
//        textArea.setEditable(false);
//        javafx.scene.control.TextField textField = new javafx.scene.control.TextField();
//
//        javafx.scene.control.Button sendButton = new javafx.scene.control.Button("Send");
//        sendButton.setOnAction((javafx.event.ActionEvent event) -> {
//            textArea.appendText(textField.getText() + "\n");
//            textField.setText("");
//        });
//
//        grid.add(textArea, 0, 0, 6, 3);
//        grid.add(textField, 0, 3, 4, 1);
//        grid.add(sendButton, 4, 3, 2, 1);
//
//
//
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(grid, 300, 275));
//        primaryStage.show();
//        FlatterFX.style();
  }

  class IncomingMessages extends Thread {
    public void run() {
      try {
        while (true) {
          String line = serverInBuffer.readLine();
          out.println(line);
          taMessages.append(line + "\n");
        }
      } catch (Exception ex) {
      }
    }
  }

  class OutgoingMessages extends Thread {
    public void run() {
      try {
        while (true) {
          String line = clientInBuffer.readLine();
          serverOutBuffer.println(line);
        }
      } catch (Exception ex) {
      }
    }
  }
}
