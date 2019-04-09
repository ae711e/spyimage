/*
 * Copyright (c) 2019. Еремин
 */

/*
  Контролер: Диалоговое окно, которое устанавливает данные учетной записи почтового сервера
  @see http://riptutorial.com/javafx/example/28660/creating-custom-dialog
 */

package dialog;

import ae.R;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountController implements Initializable {

  @FXML
  TextField txt_Email;       // электронный адрес почты
  @FXML
  TextField txt_SmtpUser;   // имя пользователя почтового сервера
  @FXML
  TextField txt_SmtpPwd;    // пароль пользователя почтового сервера

  @FXML
  TextField txt_SmtpServer;       // адрес почтового сервера
  @FXML
  TextField txt_SmtpPort;   // порт почтового сервера для отправки
  @FXML
  TextField txt_SmtpSSL;   // SSL почтового сервера

  @FXML
  TextField txt_PostUser;       // пользователь приема
  @FXML
  TextField txt_PostPwd;   // пароль приема

  @FXML
  TextField txt_PostServer;       // адрес почтового сервера для приема
  @FXML
  TextField txt_PostPort;   // порт почтового сервера
  @FXML
  TextField txt_PostSSL;   // SSL почтового сервера

  @FXML
  ComboBox<String>  cb_proto;

  @FXML
  ComboBox<String> cb_SmtpSSL;

  @FXML
  ComboBox<String> cb_PostSSL;


  @FXML
  Button        btn_Ok;

  @FXML
  Button        btn_Cancel;

  /**
   * Первоначальный запуск
   * @param location  расположение
   * @param resources ресурс
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // заполним комбо-бокс протоколами
    cb_proto.getItems().add("imap");    cb_proto.getItems().add("pop3");
    cb_SmtpSSL.getItems().add("true");  cb_SmtpSSL.getItems().add("false");
    cb_PostSSL.getItems().add("true");  cb_PostSSL.getItems().add("false");
  }

  /**
   * Возьмем данные учетной записи
   */
  public void getAccount() {
    R.getAccount();   // заполним данные учетной записи
    txt_Email.setText(R.Email);       // электронный адрес почты

    txt_SmtpUser.setText(R.SmtpUser);    // имя пользователя отправки
    txt_SmtpPwd.setText(R.SmtpPwd);       // пароль пользователя отправки
    txt_SmtpServer.setText(R.SmtpServer); // адрес почтового сервера для отправки
    txt_SmtpPort.setText(R.SmtpPort);     // порт почтового сервера для отправки
    //txt_SmtpSSL.setText(R.SmtpSSL);       // протокол SSL почтового сервера отправки
    cb_SmtpSSL.getSelectionModel().select(R.SmtpSSL); // протокол SSL почтового сервера отправки

    // комбобокс с протоколом приемного сервера
    cb_proto.getSelectionModel().select(R.PostProtocol);
    //txt_PostProtocol.setText(R.PostProtocol); // протокол приема сообщение

    txt_PostUser.setText(R.PostUser);    // пользователь
    txt_PostPwd.setText(R.PostPwd);       // пароль
    txt_PostServer.setText(R.PostServer); // адрес почтового сервера для приема
    txt_PostPort.setText(R.PostPort);     // порт почтового сервера
    //txt_PostSSL.setText(R.PostSSL);       // протокол SSL почтового сервера
    cb_PostSSL.getSelectionModel().select(R.PostSSL); // протокол SSL почтового сервера отправки
    //

  }

  /**
   * Запишем данные учетной записи после редактирования
   */
  void putAccount() {
    //
    R.Email = txt_Email.getText();       // электронный адрес почты

    R.SmtpUser = txt_SmtpUser.getText();    // имя пользователя отправки
    R.SmtpPwd = txt_SmtpPwd.getText();       // пароль пользователя отправки
    R.SmtpServer = txt_SmtpServer.getText(); // адрес почтового сервера для отправки
    R.SmtpPort = txt_SmtpPort.getText();     // порт почтового сервера для отправки
    //R.SmtpSSL = txt_SmtpSSL.getText();       // протокол SSL почтового сервера отправки
    R.SmtpSSL = cb_SmtpSSL.getValue();       // протокол SSL почтового сервера отправки

    R.PostProtocol = cb_proto.getValue();  // txt_PostProtocol.getText();       // протокол приема сообщение

    R.PostUser = txt_PostUser.getText();    // пользователь
    R.PostPwd = txt_PostPwd.getText();       // пароль
    R.PostServer = txt_PostServer.getText(); // адрес почтового сервера для приема
    R.PostPort = txt_PostPort.getText();     // порт почтового сервера
    //R.PostSSL = txt_PostSSL.getText();       // протокол SSL почтового сервера
    R.PostSSL = cb_PostSSL.getValue();       // протокол SSL почтового
    //
    R.putAccount();   // заполним данные учетной записи в БД
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

  /**
   * Закрыть окно без добавления аккаунта
   * @param ae  активное событие
   */
  @FXML
  public void click_Btn_Cancel(ActionEvent ae)
  {
    //System.out.println("Click Cancel");
    //
    closeStage(ae);
  }

  /**
   * По нажатию добавим в список значений новый аккаунт
   * @param ae  активное событие
   */
  public void click_btn_Ok(ActionEvent ae)
  {
    System.out.println("Данные аккаунта сохранены");
    //account.setName(txt_name.getText().trim());
    //account.setPass(psw_pass.getText().trim());
    //
    putAccount();
    closeStage(ae);
  }

} // end of class

