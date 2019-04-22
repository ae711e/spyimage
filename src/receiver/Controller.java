/*
 * Copyright (c) 2019. Eremin
 */
/*
  Контроллен. Прием зашифрованных изображений
 */

package receiver;

import ae.R;
import dialog.FileSelect;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ae.R.getFileExtension;


public class Controller extends OutputStream implements Initializable
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
  private String  fileNameImage;  // имя файла с изображением

  @FXML
  Button    btn_save;

  @FXML
  Button    btn_readlist;

  @FXML
  Label     lbl_email;

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
  public void write(int b) {
    Platform.runLater(() -> txt_output.appendText(""+b));
  }

  public void appendText(String str) {
    Platform.runLater(() -> txt_output.appendText(str));
  }

  private void  initialRun()
  {
    lbl_email.setText(R.Email);
  }

  private ObservableList<Stroka> usersData = FXCollections.observableArrayList();

  public void onclick_btn_save(ActionEvent ae)
  {
    if(fileNameImage != null) {
      // если файл с изображением есть, то выберем куда копировать
      FileSelect fs = new dialog.FileSelect();
      // расширение (без точки)
      String  ext = getFileExtension(fileNameImage).replace(".","");
      String fname = fs.saveDialog(ae, ext);
      //System.out.println("Сохранить изображение в файл <" + fname + ">");
      try {
        // @see http://programador.ru/java-copy-file/
        File src = new File(fileNameImage); // исходный файл
        File dst = new File(fname);         // файл назначения
        Files.copy(src.toPath(), dst.toPath());
        System.out.println("Записан файл <" + fname + ">");
      } catch (IOException e) {
        System.out.println("Ошибка записи выходного файла: " + fname);
      }
    }
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
      // System.out.println("test " + mind);
      String fimg = model.loadMailImage(mind);
      if(fimg != null) {
        this.fileNameImage = fimg;
        // загрузить изображение в контрол
        File file = new File(fimg);
        String uri = file.toURI().toString();
        f_image.setImage(new Image(uri));
      } else {
        System.out.println("Ошибка расшифровки входного файла");
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
  public void onclick_btn_keys(ActionEvent ae)
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
    // System.out.println("Нажали кнопку <" + txt + ">");
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

  /**
   * Вызовен на сцену "отправка изображений"
   * @param ae событие
   */
  public void onclick_btn_app(ActionEvent ae)
  {
    Stage stage = R.event2stage(ae);  // сцена
    //
    sender.Main m = new sender.Main();
    try {
      m.start(stage);
    } catch (Exception e) {
      System.err.println("Ошибка запуска Приема");
    }
  }

} // end of class
