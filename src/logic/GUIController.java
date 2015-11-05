package logic;

import com.guigarage.flatterfx.FlatterFX;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIController {
  private Stage pr;
  private ChatServer server;
  private ChatClient client;

  private TextArea chatArea;

  public GUIController(Stage primaryStage) {
    startGUI(primaryStage);
  }

  private void startGUI(Stage primaryStage) {
    pr = primaryStage;
    GridPane startGrid = getStartScreen();
    Scene scene = new Scene(startGrid, 800, 500);
    primaryStage.setScene(scene);

    //    primaryStage.setMaximized(true);
    primaryStage.centerOnScreen();
    primaryStage.setTitle("Chat Room");
    primaryStage.show();
    FlatterFX.style();
    startGrid.requestFocus();
  }

  private GridPane getStartScreen() {
    GridPane grid = new GridPane();

    grid.setAlignment(Pos.CENTER);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(10));

    Label portLabel = new Label("Port #");
    GridPane.setHalignment(portLabel, HPos.CENTER);
    GridPane.setValignment(portLabel, VPos.CENTER);
    portLabel.setAlignment(Pos.CENTER);
    TextField portField = new TextField("7090");

    Label ipAddressLabel = new Label("IP Address");
    GridPane.setHalignment(ipAddressLabel, HPos.CENTER);
    GridPane.setValignment(ipAddressLabel, VPos.CENTER);
    ipAddressLabel.setAlignment(Pos.CENTER);
    TextField ipAddressField = new TextField("192.168.1.");


    Label usernameLabel = new Label("User Name");
    GridPane.setHalignment(usernameLabel, HPos.CENTER);
    GridPane.setValignment(usernameLabel, VPos.CENTER);
    usernameLabel.setAlignment(Pos.CENTER);
    TextField usernameField = new TextField("Atef & Emad");

    Button createButton = new Button("Create");
    Button joinButton = new Button("Join");

    grid.add(portLabel, 0, 0, 4, 1);
    grid.add(portField, 0, 1, 4, 1);
    grid.add(ipAddressLabel, 0, 2, 4, 1);
    grid.add(ipAddressField, 0, 3, 4, 1);
    grid.add(usernameLabel, 0, 4, 4, 1);
    grid.add(usernameField, 0, 5, 4, 1);
    grid.add(createButton, 0, 6, 2, 1);
    grid.add(joinButton, 2, 6, 2, 1);

    createButton.setOnAction((ActionEvent event) -> {
      // Create Server
      try {
        int port = Integer.parseInt(portField.getText());
        String username = usernameField.getText();
        server = new ChatServer(port);
        client = new ChatClient("localhost", this, username, port);
        goToChat();
      } catch(IOException e) {
        e.printStackTrace();
      }
    });

    joinButton.setOnAction((ActionEvent event) -> {
      // Create Client
      try {
        int port = Integer.parseInt(portField.getText());
        String username = usernameField.getText();
        String serverIP = ipAddressField.getText();
        client = new ChatClient(serverIP, this, username, port);
        goToChat();
      } catch(IOException e) {
        e.printStackTrace();
      }
      goToChat();
    });

    return grid;
  }

  private GridPane getChatScreen() {
    GridPane grid = new GridPane();

    grid.setAlignment(Pos.CENTER);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(10));

    chatArea = new TextArea();
    chatArea.setEditable(false);

    TextField messageField = new TextField();
    Button sendButton = new Button("Send");

    sendButton.setOnAction((ActionEvent event) -> {
      client.sendToServer(messageField.getText());
      messageField.setText("");
    });

    ListView<String> list = new ListView<>();

    ObservableList<String> items = FXCollections.observableArrayList(
            "User 1", "User 2", "User 3", "User 4");
    list.setItems(items);

    grid.add(list, 6, 0, 2, 4);
    grid.add(chatArea, 0, 0, 6, 4);
    grid.add(messageField, 0, 4, 4, 1);
    grid.add(sendButton, 4, 4, 2, 1);

    return grid;
  }

  private void goToChat() {
    GridPane chatGrid = getChatScreen();
    Scene scene = new Scene(chatGrid);
    pr.setScene(scene);
    chatGrid.requestFocus();
  }

  public void appendMessage(String message) {
    chatArea.appendText(message + "\n");
  }
}
