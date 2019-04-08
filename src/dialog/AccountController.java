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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AccountController {

  @FXML
  TextField txt_Email;       // электронный адрес почты
  @FXML
  TextField txt_EmailUser;   // имя пользователя почтового сервера
  @FXML
  TextField txt_EmailPwd;    // пароль пользователя почтового сервера

  @FXML
  TextField txt_SmtpServer;       // адрес почтового сервера
  @FXML
  TextField txt_SmtpPort;   // порт почтового сервера для отправки

  @FXML
  TextField txt_ImapServer;       // адрес почтового сервера для приема
  @FXML
  TextField txt_ImapPort;   // порт почтового сервера
  @FXML
  TextField txt_ImapSSL;   // протокол почтового сервера

  @FXML
  TextField txt_Pop3Server;       // адрес почтового сервера для приема
  @FXML
  TextField txt_Pop3Port;   // порт почтового сервера
  @FXML
  TextField txt_Pop3SSL;   // протокол почтового сервера


  @FXML
  Button        btn_Ok;

  @FXML
  Button        btn_Cancel;

  /**
   * Возьмем данные учетной записи
   */
  public void getAccount() {
    R.getAccount();   // заполним данные учетной записи
    txt_Email.setText(R.Email);       // электронный адрес почты
    txt_EmailUser.setText(R.RecvEmailUser);   // имя пользователя почтового сервера
    txt_EmailPwd.setText(R.RecvEmailPwd);    // пароль пользователя почтового сервера

    txt_SmtpServer.setText(R.SmtpServer);       // адрес почтового сервера
    txt_SmtpPort.setText(R.SmtpPort);   // порт почтового сервера для отправки

    txt_ImapServer.setText(R.ImapServer); // адрес почтового сервера для приема
    txt_ImapPort.setText(R.ImapPort);     // порт почтового сервера
    txt_ImapSSL.setText(R.ImapSSL);       // протокол SSL почтового сервера

    txt_Pop3Server.setText(R.Pop3Server); // адрес почтового сервера для приема
    txt_Pop3Port.setText(R.Pop3Port);     // порт почтового сервера
    txt_Pop3SSL.setText(R.Pop3SSL);       // протокол SSL почтового сервера

    //txt_name.setText(account.getName());
    //psw_pass.setText(account.getPass());
  }

  /**
   * Запишем данные учетной записи после редактирования
   */
  void putAccount() {
    R.Email = txt_Email.getText();            // электронный адрес почты
    R.RecvEmailUser = txt_EmailUser.getText();  // имя пользователя почтового сервера
    R.RecvEmailPwd = txt_EmailPwd.getText();   // пароль пользователя почтового сервера
    R.SmtpServer  = txt_SmtpServer.getText(); // адрес почтового сервера
    R.SmtpPort    = txt_SmtpPort.getText();   // порт почтового сервера для отправки
    R.ImapServer  = txt_ImapServer.getText(); // адрес почтового сервера для приема
    R.ImapPort    = txt_ImapPort.getText();   // порт почтового сервера
    R.ImapSSL     = txt_ImapSSL.getText();    // протокол SSL почтового сервера
    R.Pop3Server  = txt_Pop3Server.getText(); // адрес почтового сервера для приема
    R.Pop3Port    = txt_Pop3Port.getText();   // порт почтового сервера
    R.Pop3SSL     = txt_Pop3SSL.getText();    // протокол SSL почтового сервера
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

