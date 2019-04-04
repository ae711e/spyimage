/*
 * Copyright (c) 2018. Алексей Еремин
 * 14.09.18 8:20
 */

/*
  Учетные данные, которые вызывают для своего изменения диалоговое окно
  @see http://riptutorial.com/javafx/example/28660/creating-custom-dialog
 */

package dialog;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Account {
  /**
   * Вызываем диалоговое окно для изменения учетных данных.
   * Данные в R поменяет контролер.
   * @param event
   */
  public void openDialog(ActionEvent event) /* throws IOException */ {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("account.fxml"));
      Parent parent = fxmlLoader.load();
      //
      AccountController dialog = fxmlLoader.getController();
      dialog.getAccount();  // загрузить данные в поля
      //
      Scene scene = new Scene(parent, 500, 300);
      Stage stage = new Stage();
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setScene(scene);
      stage.showAndWait();
    } catch (IOException e) {
      System.err.println("Ошибка открытия диалогового окна: " + e.getMessage());
    }
  }

} // end of class

