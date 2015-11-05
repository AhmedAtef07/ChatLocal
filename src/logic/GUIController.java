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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUIController {

  private Stage stage;
  private ChatServer server;
  private ChatClient client;

  private int width = 900;
  private int height = 600;

  private TextArea chatArea;

  public GUIController(Stage primaryStage) {
    startGUI(primaryStage);
  }

  private void startGUI(Stage primaryStage) {
    stage = primaryStage;
    GridPane startGrid = getStartScreen();
    Scene scene = new Scene(startGrid, width, height);
    primaryStage.setScene(scene);

    //    primaryStage.setMaximized(true);
    stage.setTitle("Chat Room");
//    stage.setMaxHeight(height);
//    stage.setMaxWidth(width);
    stage.setWidth(width);
    stage.setHeight(height);
    stage.centerOnScreen();
    stage.show();

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
//    GridPane.setHalignment(portLabel, HPos.CENTER);
//    GridPane.setValignment(portLabel, VPos.CENTER);
//    portLabel.setAlignment(Pos.CENTER);
    TextField portField = new TextField("7090");
    GridPane.setHalignment(portField, HPos.CENTER);
    GridPane.setValignment(portField, VPos.CENTER);
    portField.setAlignment(Pos.CENTER);
    VBox portBox = new VBox(5, portLabel, portField);

    Label ipAddressLabel = new Label("IP Address");
//    GridPane.setHalignment(ipAddressLabel, HPos.CENTER);
//    GridPane.setValignment(ipAddressLabel, VPos.CENTER);
//    ipAddressLabel.setAlignment(Pos.CENTER);
    TextField ipAddressField = new TextField("192.168.1.");
//    GridPane.setHalignment(ipAddressField, HPos.CENTER);
//    GridPane.setValignment(ipAddressField, VPos.CENTER);
//    ipAddressField.setAlignment(Pos.CENTER);
    VBox ipAddresstBox = new VBox(5, ipAddressLabel, ipAddressField);


    Label usernameLabel = new Label("User Name");
//    GridPane.setHalignment(usernameLabel, HPos.CENTER);
//    GridPane.setValignment(usernameLabel, VPos.CENTER);
//    usernameLabel.setAlignment(Pos.CENTER);
    TextField usernameField = new TextField("Atef & Emad");
//    GridPane.setHalignment(usernameField, HPos.CENTER);
//    GridPane.setValignment(usernameField, VPos.CENTER);
//    usernameField.setAlignment(Pos.CENTER);
    VBox usernameBox = new VBox(5, usernameLabel, usernameField);

    Button createButton = new Button("Create");
    Button joinButton = new Button("Join");

    grid.add(portBox, 0, 0, 4, 2);
    grid.add(ipAddresstBox, 0, 2, 4, 2);
    grid.add(usernameBox, 0, 4, 4, 2);
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
      } catch(Exception ex) {
        ex.printStackTrace();
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
      } catch(Exception ex) {
        ex.printStackTrace();
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
      try {
        client.sendToServer(messageField.getText());
      } catch(Exception ex) {
        ex.printStackTrace();
      }
      messageField.setText("");
    });

    ListView<String> list = new ListView<>();

    ObservableList<String> items = FXCollections.observableArrayList(
            "User 1", "User 2", "User 3", "User 4");
    list.setItems(items);

    grid.add(list, 6, 0, 2, 5);
    grid.add(chatArea, 0, 0, 6, 4);
    grid.add(messageField, 0, 4, 4, 1);
    grid.add(sendButton, 4, 4, 2, 1);

    return grid;
  }

  private void goToChat() {
    GridPane chatGrid = getChatScreen();
    Scene scene = new Scene(chatGrid, width, height);
    stage.setScene(scene);
//    stage.setMaxHeight(height);
//    stage.setMaxWidth(width);
    stage.setWidth(width);
    stage.setHeight(height);
    stage.centerOnScreen();
    chatGrid.requestFocus();
  }

  public void appendMessage(String message) {
    chatArea.appendText(message + "\n");
  }

}
