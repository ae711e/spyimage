/*
 * Copyright (c) 2019. Eremin
 */
/*
  Контроллен. Прием зашифрованных изображений
 */

package receiver;

import ae.R;
import dialog.FileSelect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.ArrayList;


public class Controller
{
  Model model = new Model();

  @FXML
  TableView<Stroka> tbl_table;
  @FXML
  private TableColumn<Stroka, String> col_mind;
  @FXML
  private TableColumn<Stroka, String> col_datt;
  @FXML
  private TableColumn<Stroka, String> col_from;
  @FXML
  private TableColumn<Stroka, String> col_fnam;

  @FXML
  ImageView f_image;

  @FXML
  Button btn_save;

  @FXML
  Button  btn_readlist;

  private ObservableList<Stroka> usersData = FXCollections.observableArrayList();

  public void onclick_btn_save(ActionEvent ae)
  {
    FileSelect fs = new dialog.FileSelect();
    String fname = fs.saveDialog(ae);
    System.out.println("Сохранить файл <" + fname + ">");


  }


  /**
   * Загрузить изображение из почты
   * @param ae событие
   */
  public void onclick_btn_loadimg(ActionEvent ae)
  {
    int i;
    TableView.TableViewSelectionModel<Stroka> selectionModel = tbl_table.getSelectionModel();
    Stroka stro = selectionModel.getSelectedItem();
    if(stro != null) {
      int mind = Integer.parseInt(stro.getMind());
      System.out.println("test " + mind);
      String fimg = model.loadMailImage(mind);
      if(fimg != null) {
        // загрузить изображение в контрол
        File file = new File(fimg);
        String uri = file.toURI().toString();
        f_image.setImage(new Image(uri));
      }
    }

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
   * Открыть настройку ключей
   * @param ae событие
   */
  public void onclick_btn_mykeys(ActionEvent ae)
  {
    keygenmy.Dialog  dlg = new keygenmy.Dialog();
    dlg.open(ae);
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
    ArrayList<String[]> arr = model.readmails();
    loadData(arr);
  }

  /**
   * Инициализировать таблицу данными
   * @param mailList  массив данных
   */
  private void  loadData(ArrayList<String[]> mailList)
  {
    usersData.clear();  // очистить таблицу от данных
    for(String[] r: mailList) {
      usersData.add(new Stroka(r[0],r[1],r[2],r[3]));
    }
    //
    col_mind.setCellValueFactory(cellData -> cellData.getValue().mindProperty());
    col_datt.setCellValueFactory(cellData -> cellData.getValue().dattProperty());
    col_from.setCellValueFactory(cellData -> cellData.getValue().fromProperty());
    col_fnam.setCellValueFactory(cellData -> cellData.getValue().fnamProperty());
    // заполняем таблицу данными
    tbl_table.setItems(usersData);


  }


} // end of class
