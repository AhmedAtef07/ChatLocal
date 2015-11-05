package gui;

import com.guigarage.flatterfx.FlatterFX;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    GridPane grid = new GridPane();

    grid.setAlignment(Pos.CENTER);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(10));

    Label portLabel = new Label("Port #");
    TextField portField = new TextField();

    Button createButton = new Button("Create");
    Button joinButton = new Button("Join");

    grid.add(portLabel, 0, 0, 4, 1);
    grid.add(portField, 0, 1, 4, 1);
    grid.add(createButton, 0, 2, 2, 1);
    grid.add(joinButton, 2, 2, 2, 1);

    createButton.setOnAction((ActionEvent event) -> {
    });

    primaryStage.setTitle("Hello World");
    primaryStage.setScene(new Scene(grid, 300, 275));
    primaryStage.show();
    FlatterFX.style();
  }
}
