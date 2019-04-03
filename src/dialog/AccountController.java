/*
 * Copyright (c) 2018. Алексей Еремин
 * 14.09.18 8:24
 */

/*
  Контролер: Диалоговое окно, которое устанавливает данные в Account (name и pass)
  @see http://riptutorial.com/javafx/example/28660/creating-custom-dialog
 */

package dialog;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AccountController {

  @FXML
  TextField     txt_name;

  @FXML
  PasswordField psw_pass;

  @FXML
  Button        btn_Ok;

  @FXML
  Button        btn_Cancel;

  private Account account;

  public Account getAccount() {
    return account;
  }

  /**
   * Запомниает аккаут, в который запишет новые данные
   * @param account аккаунт, который надо изменять
   */
  public void setAccount(Account account) {
    this.account = account;
    txt_name.setText(account.getName());
    psw_pass.setText(account.getPass());
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
    account.setName(txt_name.getText().trim());
    account.setPass(psw_pass.getText().trim());
    //
    closeStage(ae);
  }

} // end of class

