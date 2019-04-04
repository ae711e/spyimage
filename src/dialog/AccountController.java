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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AccountController {

  @FXML
  TextField txt_SmtpSender;       // электронный адрес почты

  @FXML
  TextField txt_SmtpServer;       // адрес почтового сервера

  @FXML
  TextField txt_SmtpServerPortSend;   // порт почтового сервера для отправки

  @FXML
  TextField txt_SmtpServerPortRecv;   // порт почтового сервера для приема

  @FXML
  TextField txt_SmtpServerUser;   // имя пользователя почтового сервера

  @FXML
  TextField txt_SmtpServerPwd;    // пароль пользователя почтового сервера

  @FXML
  Button        btn_Ok;

  @FXML
  Button        btn_Cancel;

  /**
   * Возьмем данные учетной записи
   */
  public void getAccount() {
    R.getAccount();   // заполним данные учетной записи
    txt_SmtpSender.setText(R.SmtpSender);       // электронный адрес почты
    txt_SmtpServer.setText(R.SmtpServer);       // адрес почтового сервера
    txt_SmtpServerPortSend.setText(R.SmtpServerPortSend);   // порт почтового сервера для отправки
    txt_SmtpServerPortRecv.setText(R.SmtpServerPortRecv);   // порт почтового сервера для приема
    txt_SmtpServerUser.setText(R.SmtpServerUser);   // имя пользователя почтового сервера
    txt_SmtpServerPwd.setText(R.SmtpServerPwd);    // пароль пользователя почтового сервера

    //txt_name.setText(account.getName());
    //psw_pass.setText(account.getPass());
  }

  /**
   * Запишем данные учетной записи после редактирования
   */
  void putAccount() {

    R.SmtpSender          = txt_SmtpSender.getText();       // электронный адрес почты
    R.SmtpServer          = txt_SmtpServer.getText();       // адрес почтового сервера
    R.SmtpServerPortSend  = txt_SmtpServerPortSend.getText();   // порт почтового сервера для отправки
    R.SmtpServerPortRecv  = txt_SmtpServerPortRecv.getText();   // порт почтового сервера для приема
    R.SmtpServerUser      = txt_SmtpServerUser.getText();   // имя пользователя почтового сервера
    R.SmtpServerPwd       = txt_SmtpServerPwd.getText();    // пароль пользователя почтового сервера

    //txt_name.setText(account.getName());
    //psw_pass.setText(account.getPass());
    R.putAccount();   // заполним данные учетной записи
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
  @FXML
  public void click_btn_Ok(ActionEvent ae)
  {
    //System.out.println("Click OK");
    //account.setName(txt_name.getText().trim());
    //account.setPass(psw_pass.getText().trim());
    //
    putAccount();
    closeStage(ae);
  }

} // end of class

