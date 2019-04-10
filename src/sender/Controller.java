/*
 * Copyright (c) 2019. Eremin
 */
/*
  Контролер отправителя изображения
 */

package sender;

import ae.R;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class Controller extends OutputStream implements Initializable
{

  Model model = new Model();

  private String fileNameImage;
  @FXML
  ImageView f_image;
  @FXML
  Button    btn_load;
  @FXML
  Button    btn_send;
  @FXML
  TextField txt_receiver; // адрес приемника картинки
  @FXML
  ComboBox<String>  cmb_users;  // список пользователей

  @FXML
  Label lbl_email;

  @FXML
  TextArea  txt_output;

  ///////////////////////////////////////////////////////////////////
  // Перенаправление стандартного вывода в TextArea
  // class ... extends OutputStream implements Initializable {
  // стандартный вывод System.output направил в поле txt_out
  // https://code-examples.net/ru/q/19a134d

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    OutputStream out = new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        appendText(String.valueOf((char) b));
      }
      @Override
      public void write(byte[] b, int off, int len) throws IOException {
        appendText(new String(b, off, len));
      }

      @Override
      public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
      }
    };
    System.setOut(new PrintStream(out, true));
    //
    initialRun();
  }

  @Override
  public void write(int b) throws IOException {
    Platform.runLater(() -> txt_output.appendText(""+b));
  }

  public void appendText(String str) {
    Platform.runLater(() -> txt_output.appendText(str));
  }

  /**
   * Метод вызывается при инициализации контролера
   */

  private void initialRun()
  {
    lbl_email.setText(R.Email);
    //
    //File file = new File("src/res/app.png");
    //String uri = file.toURI().toString();
    //InputStream input2 = getClass().getResourceAsStream("src/res/app1.png");
    Image image2 = new Image("res/appgray.png");
    f_image.setImage(image2);
    //
    // заполним список пользователей про которых у нас есть ключи
    loadUsers();
    cmb_users.getSelectionModel().select(0);
    //
    onaction_cmb_users(null); // заполним поле адресата
  }

  /**
   * заполнить список пользователей в комбо-боксе
   */
  private void  loadUsers()
  {
    String  str = cmb_users.getValue();
    // заполним список пользователей про которых у нас есть ключи
    Collection<String> users = model.getKeysUsers();
    cmb_users.getItems().removeAll(cmb_users.getItems()); // очистить список комбо-бокса
    cmb_users.getItems().addAll(users);
    if(str != null && str.length() > 1)
      cmb_users.getSelectionModel().select(str);  // выбрать ранее выбранный
  }

  /**
   * Загрузка изображения
   * @param ae  событие
   */
  public void onclick_btn_load(ActionEvent ae)
  {
    String str;
    str = btn_load.getText();
    // System.out.println("Нажали кнопку \"" + str + "\" / " + ae.getEventType().getName());
    //
    dialog.FileSelect  fs = new dialog.FileSelect();
    String fname = fs.openDialog(ae, false);
    // System.out.println("Выбрали файл: <" + fname + ">");
    //
    if(fname != null) {
      fileNameImage = fname;
      // @see https://stackoverflow.com/questions/22710053/how-can-i-show-an-image-using-the-imageview-component-in-javafx-and-fxml
      File file = new File(fname);
      String uri = file.toURI().toString();
      f_image.setImage(new Image(uri));
    }
  }

  /**
   * Отправить картинку адресату по почте
   * @param ae  событие
   */
  public void onclick_btn_send(ActionEvent ae)
  {
    String str;
    str = btn_send.getText();
    // System.out.println("Нажали кнопку <" + str + "> / " + ae.getEventType().getName());
    str = txt_receiver.getText();   // адрес получателя
    if(model.sendMailTo(str, fileNameImage)) {
      System.out.println("Отправили почту " + str);
    } else {
      System.out.println("Ошибка отправки почты");
    }
  }

  /**
   * Открыть настройку акаунта почты
   * @param ae  событие
   */
  public void onclick_btn_account(ActionEvent ae)
  {
    dialog.Account  acc = new dialog.Account();
    acc.openDialog(ae);
  }

  /**
   * Открыть настройку собственных ключей шифрования
   * @param ae  событие
   */
  public void onclick_btn_mykeys(ActionEvent ae)
  {
    keygenmy.Dialog  dw = new keygenmy.Dialog();
    dw.open(ae);
    // заполним список пользователей про которых у нас есть ключи
    loadUsers();
  }

  /**
   * При изменении списка получателей заполнить поле "получатель"
   * @param ae  событие
   */
  public void onaction_cmb_users(ActionEvent ae)
  {
    String str;
    str = cmb_users.getValue(); // значение выбранноего элемента
    // System.out.println("Акция " + str);
    txt_receiver.setText(str);
  }

  /**
   * Вызовем на сцену "прием изображений"
    * @param ae событие
   */
  public void onclick_btn_app(ActionEvent ae)
  {
    Stage stage = R.event2stage(ae);  // сцена
    //
    receiver.Main m = new receiver.Main();
    try {
      m.start(stage);
    } catch (Exception e) {
      System.err.println("Ошибка запуска Передачи");
    }
  }

} // end of class

