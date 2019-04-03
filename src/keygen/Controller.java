package keygen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;
/*
 Контроллер формирования ключа получателя
 */

public class Controller implements Initializable {
  @FXML
  ComboBox<String> cb_reciever;
  @FXML
  TextField   txt_usr;
  @FXML
  TextArea    ta_publicKey;
  @FXML
  TextArea    ta_privateKey;
  @FXML
  Button      btn_keygen;
  @FXML
  Button      btn_deleteusr;

  /**
   * Вызывается при инициализации root объекта, будем заполнять ComboBox данными из БД
   * @param location
   * @param resources
   */
  @Override
  public void initialize(URL location, ResourceBundle resources)
  {
    prepUsrList();  // приготовить список пользователей из БД
  }

  /**
   * Обработка нажатия кнопки "сгенерировать ключи"
   * @param actionEvent
   */
  public void onclickBtnKeygen(ActionEvent actionEvent)
  {
    Model model = new Model();
    String[]  keys = model.keyGen();    // генерируем ключи
    ta_publicKey.setText(keys[0]);
    ta_privateKey.setText(keys[1]);
    String usr =  txt_usr.getText();    // получим имя нового пользователя
    boolean res;
    res = model.addUser(usr, keys[0], keys[1]);
    if(!res) {
      String errMess = "Ошибка добавления пользователя " + usr + " / " + model.getLastError();
      ta_privateKey.setText(errMess);
    } else {
      prepUsrList();  // переподготовить список из БД
    }

  }


  public void onclickBtnDeleteusr(ActionEvent actionEvent)
  {
    ta_publicKey.setText("");
    ta_privateKey.setText("");
    Model model = new Model();
    String usr = cb_reciever.getValue();
    boolean res;
    res = model.delUser(usr);
    if(!res) {
      String errMess = "Ошибка удаления пользователя " + usr + "  " + model.getLastError();
      ta_privateKey.setText(errMess);
    } else {
      // prepUsrList();  // переподготовить список из БД
      ta_privateKey.setText("Удален получатель " + usr);
    }

  }

  /**
   * Приготовить список пользователей из БД
   */
  private void  prepUsrList()
  {
    Model model = new Model();
    String[] usrs = model.getUsrKeys();
    cb_reciever.getItems().removeAll(cb_reciever.getItems()); // очистить список комбо-бокса
    for(int i=0; i < usrs.length; i++) {
      cb_reciever.getItems().add(usrs[i]);
    }
    cb_reciever.getSelectionModel().select(0);  // выбрать первый элемент

  }

} // end of class

