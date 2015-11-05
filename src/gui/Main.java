package gui;

import javafx.application.Application;
import javafx.stage.Stage;
import logic.GUIController;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }


  @Override
  public void start(Stage primaryStage) throws Exception {
    GUIController guiController = new GUIController(primaryStage);
  }
}
