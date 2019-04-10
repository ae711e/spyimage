/*
 * Copyright (c) 2019. Eremin
 */

/*
  Выбор файла
  @see https://o7planning.org/ru/11129/javafx-filechooser-and-directorychooser-tutorial
  @see https://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm
 */

package dialog;

import ae.R;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class FileSelect
{
  private static String  initialDir = System.getProperty("user.home");

  public FileSelect() {}

  //public FileSelect(String iDir)  { initialDir = iDir; }

  /**
   * Выбор для чтения файла на диске
   * @param ae  событие кнопки (из нее получим сцену)
   * @param extensionImageFile  признак задать расширения графических файлов
   * @return  имя выбранного файла
   */
  public String openDialog(ActionEvent ae, boolean extensionImageFile)
  {
    Stage stage = R.event2stage(ae);
    FileChooser fileChooser = new FileChooser();
    // @see https://o7planning.org/ru/11129/javafx-filechooser-and-directorychooser-tutorial#a3827915
    // Set Initial Directory
    fileChooser.setInitialDirectory(new File(initialDir));
    if(extensionImageFile) {
      fileChooser.getExtensionFilters().addAll(
          new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
          new FileChooser.ExtensionFilter("All Files", "*.*")
      );
    }
    File selectedFile = fileChooser.showOpenDialog(stage);
    if(selectedFile == null)
      return null;
    String  fname = selectedFile.getPath();
    initialDir = name2dir(fname);
    return fname;
  }

  /**
   * Выбор для записи файла на диске
   * @param ae  событие кнопки (из нее получим сцену)
   * @param extensionImageFile  строка расширения файла (jpg и т.д.)
   * @return  имя выбранного файла
   */
  public String saveDialog(ActionEvent ae, String extensionImageFile)
  {
    Stage stage = R.event2stage(ae);
    FileChooser fileChooser = new FileChooser();
    // @see https://www.genuinecoder.com/save-files-javafx-filechooser/
    // Set Initial Directory
    fileChooser.setInitialDirectory(new File(initialDir));
    if(extensionImageFile != null) {
      String  n = "*." +extensionImageFile;
      fileChooser.getExtensionFilters().addAll(
          new FileChooser.ExtensionFilter("Image Files " + extensionImageFile, n),
          new FileChooser.ExtensionFilter("All Files", "*.*")
      );
    }
    File selectedFile = fileChooser.showSaveDialog(stage);
    if(selectedFile == null)
      return null;
    String  fname = selectedFile.getPath();
    initialDir = name2dir(fname);
    return fname;
  }

  /**
   * Получить из полного имени файла его директорию
   * @param fname полное имя файла
   * @return  директория файла
   */
  private String  name2dir(String fname)
  {
    File f = new File(fname);
    return f.getParent();
  }

} // end of class

