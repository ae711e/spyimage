/*
 * Copyright (c) 2019. Eremin
 */

/*
  Защищенная передача графических документов
 */

package sender;

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
    Parent root = FXMLLoader.load(getClass().getResource("sender.fxml"));
    primaryStage.getIcons().add(new Image("res/app.png"));
    primaryStage.setTitle("Отправка изображения");
    primaryStage.setScene(new Scene(root, 600, 400));
    primaryStage.getScene().getStylesheets().add("css/JMetroLightTheme.css"); //подключим стили
    primaryStage.show();
  }

  public static void main(String[] args) {
    R.loadDefault();
    R.getAccount();
    launch(args);
  }

  /**
   * Закрытие приложения
   * @throws Exception
   */
  @Override
  public void stop() throws Exception {
    if(R.db != null)
      R.db.close();
    super.stop();
  }
}

