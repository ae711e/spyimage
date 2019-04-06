/*
 * Copyright (c) 2019. Eremin
 */
/*
  Контроллен. Прием зашифрованных изображений
 */

package receiver;

import ae.R;
import dialog.FileSelect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javax.mail.*;
import java.util.Properties;

public class Controller
{
  Model model = new Model();

  @FXML
  Button btn_save;

  @FXML
  Button  btn_readlist;

  public void onclick_btn_save(ActionEvent ae)
  {
    FileSelect fs = new dialog.FileSelect();
    String fname = fs.saveDialog(ae);
    System.out.println("Сохранить файл <" + fname + ">");


  }

  /**
   * Открыть настройку акаунта почты
   * @param ae событие
   */
  public void onclick_btn_account(ActionEvent ae)
  {
    dialog.Account  acc = new dialog.Account();
    acc.openDialog(ae);
  }

  /**
   * Прочитать список писем
   * @param ae  событие
   */
  public void onclick_btn_readlist(ActionEvent ae)
  {
    Button  btn = (Button) ae.getSource();
    String  txt = btn.getText();
    System.out.println("Нажали кнопку <" + txt + ">");
    String str = model.readmails();
    System.out.println(str);
  }



} // end of class
