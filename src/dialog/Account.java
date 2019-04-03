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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Account {
  private String name;
  private String pass;
  private boolean change = false; // флаг "изменен" сброшен

  public Account(String name, String pass)
  {
    this.name = name;
    this.pass = pass;
  }

  /**
   * Проверка - изменились данные
   * @return изменились данные
   */
  public boolean  isChange()
  {
    return change;
  }

  @Override
  public String toString() {
    return "Account{" +
        "name=" + name +
        ", pass=" + pass +
        '}';
  }

  public String getName() {
    return name;
  }

  public void setName(String name)
  {
    if(name.compareTo(this.name) != 0) {
      this.name = name;
      change = true;  // ставим флаг "изменен"
    }
  }

  public String getPass() {
    return pass;
  }

  public void setPass(String pass)
  {
    if(pass.compareTo(this.pass) != 0) {
      this.pass = pass;
      change = true;  // ставим флаг "изменен"
    }
  }

  /**
   * Вызываем диалоговое окно для изменения учетных данных
   * @param event
   * @throws IOException
   */
  public void openDialog(ActionEvent event) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("account.fxml"));
    Parent parent = fxmlLoader.load();
    AccountController dialog = fxmlLoader.getController();
    dialog.setAccount(this);
    //
    Scene scene = new Scene(parent, 400, 250);
    Stage stage = new Stage();
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setScene(scene);
    FileChooser fileChooser = new FileChooser();
    File selectedFile = fileChooser.showOpenDialog(stage);
    stage.showAndWait();
  }

} // end of class

