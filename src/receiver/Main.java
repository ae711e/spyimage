/*
 * Copyright (c) 2019. Eremin
 */
/*
  Прием защищенных графических документов
 */

package receiver;

import ae.R;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception{
    Parent root = FXMLLoader.load(getClass().getResource("receiver.fxml"));
    primaryStage.getIcons().add(new Image("res/app.png"));
    primaryStage.setTitle("Прием изображения");
    primaryStage.setScene(new Scene(root, 780, 650));
    primaryStage.getScene().getStylesheets().add("css/JMetroLightTheme.css"); //подключим стили
    primaryStage.show();
  }

  public static void main(String[] args) {
    R.loadDefault();
    launch(args);
  }

  /**
   * Закрытие приложения
   * @throws Exception
   */
  @Override
  public void stop() throws Exception {
    R.dbClose();
    super.stop();
  }

} // end of class

