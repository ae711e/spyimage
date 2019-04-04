/*
 * Copyright (c) 2019. Eremin
 */
/*
  Контролер отправителя изображения
 */

package sender;

import ae.R;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class Controller implements Initializable
{

  Model model = new Model();

  private String  fileImage;
  @FXML
  ImageView f_image;
  @FXML
  Button    btn_load;
  @FXML
  Button    btn_send;
  @FXML
  Button    btn_selfile;
  @FXML
  TextField txt_reciever; // адрес приемника картинки
  @FXML
  ComboBox<String>  cmb_users;  // список пользователей


  /**
   * Метод вызывается при инициализации контролера
   * @param location
   * @param resources
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    File file = new File("src/res/app.png");
    String uri = file.toURI().toString();
    f_image.setImage(new Image(uri));
    //
    // заполним список пользователей про которых у нас есть ключи
    Collection<String> users = model.getUsers();
    cmb_users.getItems().removeAll(cmb_users.getItems()); // очистить список комбо-бокса
    cmb_users.getItems().addAll(users);
    cmb_users.getSelectionModel().select(0);  // выбрать первый элемент
    //
    onaction_cmb_users(null); // заполним поле адресата

  }

  public void onclick_btn_load(ActionEvent ae)
  {
    String str;
    str = btn_load.getText();
    System.out.println("Нажали кнопку \"" + str + "\" / " + ae.getEventType().getName());
    //
    dialog.FileSelect  fs = new dialog.FileSelect();
    String fname = fs.openDialog(ae);
    System.out.println("Выбрали файл: <" + fname + ">");
    //
    if(fname != null) {
      fileImage = fname;
      // @see https://stackoverflow.com/questions/22710053/how-can-i-show-an-image-using-the-imageview-component-in-javafx-and-fxml
      File file = new File(fname);
      String uri = file.toURI().toString();
      f_image.setImage(new Image(uri));
    }
  }

  /**
   * Отправить картинку адресату по почте
   * @param ae
   */
  public void onclick_btn_send(ActionEvent ae)
  {
    String str;
    str = btn_send.getText();
    System.out.println("Нажали кнопку <" + str + "> / " + ae.getEventType().getName());
    str = txt_reciever.getText();   // адрес получателя
    if(model.sendMailTo(str, fileImage)) {
      System.out.println("Отправили почту");
    } else {
      System.err.println("Почта не отправилась");
    }
  }

  /**
   * Открыть настройку акаунта почты
   * @param ae
   */
  public void onclick_btn_account(ActionEvent ae)
  {
    dialog.Account  acc = new dialog.Account();
    acc.openDialog(ae);
  }

  /**
   * При изменении списка получателей заполнить поле "получатель"
   * @param ae
   */
  public void onaction_cmb_users(ActionEvent ae)
  {
    String str;
    str = cmb_users.getValue(); // значение выбранноего элемента
    System.out.println("Акция " + str);
    txt_reciever.setText(str);
  }



} // end of class

