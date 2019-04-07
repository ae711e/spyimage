package keygenmy;

import ae.R;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
/*
 Контроллер формирования собственного ключа
 */

public class Controller implements Initializable {

  Model model = new Model();

  @FXML
  TextField   txt_user;
  @FXML
  ComboBox<String>  cb_users;
  @FXML
  TextArea    ta_publicKey;
  @FXML
  TextArea    ta_privateKey;
  @FXML
  Button      btn_keygen;
  @FXML
  Button      btn_deleteusr;
  @FXML
  Button      btn_close;

  /**
   * Вызывается при инициализации root объекта, будем заполнять ComboBox данными из БД
   * @param location URl
   * @param resources ресурс
   */
  @Override
  public void initialize(URL location, ResourceBundle resources)
  {
    //
    txt_user.setText(R.Email);
    prepUsersList();
  }

  /**
   * Обработка нажатия кнопки "сгенерировать ключи"
   * @param ae событие
   */
  public void onclickBtnKeygen(ActionEvent ae)
  {
    String[]  keys = model.keyGen();    // генерируем ключи
    ta_publicKey.setText(keys[0]);
    ta_privateKey.setText(keys[1]);
    String usr =  txt_user.getText();    // получим имя пользователя
    //model.rememberKeys(keys[0],keys[1]);
    boolean res;
    res = model.addUser(usr, keys[0], keys[1]);
    if(!res) {
      String errMess = "Ошибка добавления пользователя " + usr + " / " + model.getLastError();
      ta_privateKey.setText(errMess);
      ta_publicKey.setText("");
    } else {
      prepUsersList();  // переподготовить список из БД
    }
  }

  public void onclickBtnDeleteusr(ActionEvent actionEvent)
  {
    ta_publicKey.setText("");
    ta_privateKey.setText("");
    String usr =  cb_users.getValue();    // получим имя пользователя
    boolean res;
    res = model.delUser(usr);
    if(!res) {
      String errMess = "Ошибка удаления пользователя " + usr + "  " + model.getLastError();
      ta_privateKey.setText(errMess);
    } else {
      prepUsersList();  // переподготовить список из БД
      ta_privateKey.setText("Удалены ключи пользователя " + usr);
    }
  }

  /**
   * Приготовить список пользователей из БД
   */
  private void  prepUsersList()
  {
    Model model = new Model();
    Collection<String> usrs = model.getUsrsKeys();
    cb_users.getItems().removeAll(cb_users.getItems()); // очистить список комбо-бокса
    cb_users.getItems().addAll(usrs);                   // добавить список
    cb_users.getSelectionModel().select(0);       // выбрать первый элемент
  }

  public void onclick_btn_close(ActionEvent ae)
  {
    closeStage(ae);
  }

  /**
   * Закрыть сцену, а значит и диалоговое окно
   * @param ae  активное событие
   */
  private void closeStage(ActionEvent ae)
  {
    Node source = (Node) ae.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    stage.close();
  }

} // end of class

