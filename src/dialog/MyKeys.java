/*
 * Copyright (c) 2019. Eremin
 *
 */

/*
    Диалоговое окно управления моими ключами
 */

package dialog;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MyKeys
{

  public void openDialog(ActionEvent ae)
  {
    try {
      Parent parent = FXMLLoader.load(getClass().getResource("../keygenmy/keygenmy.fxml"));
      //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../keygenmy/keygenmy.fxml"));
      //Parent parent = fxmlLoader.load();
      Scene scene = new Scene(parent, 700, 480);
      Stage stage = new Stage();
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setScene(scene);
      stage.showAndWait();
    } catch (Exception e) {
      System.err.println("Ошибка открытия диалогового окна: " + e.getMessage());
    }
  }

}
