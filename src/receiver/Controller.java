/*
 * Copyright (c) 2019. Eremin
 */
/*
  Контроллен. Прием зашифрованных изображений
 */

package receiver;

import dialog.FileSelect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class Controller
{
  Model model = new Model();

  @FXML
  Button btn_save;


  public void onclick_btn_save(ActionEvent ae)
  {
    FileSelect fs = new dialog.FileSelect();
    String fname = fs.saveDialog(ae);
    System.out.println("Сохранить файл <" + fname + ">");


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

} // end of class
