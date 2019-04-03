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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
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

  public void onclick_btn_send(ActionEvent ae)
  {
    String str;
    str = btn_send.getText();
    System.out.println("Нажали кнопку \"" + str + "\" / " + ae.getEventType().getName());
    if(model.sendMailTo("ae999@mail.ru", fileImage)) {
      System.out.println("Отправили почту");
    } else {
      System.err.println("Почта не отправилась");
    }
  }

  public void onclick_btn_selfile(ActionEvent ae)
  {
    String str;
    str = btn_selfile.getText();
    System.out.println("Нажали кнопку \"" + str + "\" / " + ae.getEventType().getName());
    //
    dialog.FileSelect  fs = new dialog.FileSelect("c:/tmp");
    String fname = fs.openDialog(ae);
    System.out.println("Выбрали файл: <" + fname + ">");
  }


} // end of class

