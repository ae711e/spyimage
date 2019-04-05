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
import javafx.scene.Node;
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
    String str = readmails();
    System.out.println(str);
  }

  public class PopupAuthenticator extends Authenticator {

    public PasswordAuthentication getPasswordAuthentication() {
      String username, password;

      username = R.SmtpServerUser;
      password = R.SmtpServerPwd;

      return new PasswordAuthentication(username, password);
    }
  }

  private String  readmails()
  {
    int i, j, n;
    // @see http://javatutor.net/articles/receiving-mail-with-mail-api
    // http://prostoitblog.ru/poluchenie-pochti-java-mail/
    // https://www.pvsm.ru/java/16472
    // http://toolkas.blogspot.com/2019/02/java.html
    // http://java-online.ru/javax-mail.xhtml
    //
    // Настроить аутентификацию, получить session
    Authenticator auth = new PopupAuthenticator();
    // Свойства установки
    Properties props = System.getProperties();
    props.put("mail.pop3.host", R.SmtpServer);
    Session session = Session.getDefaultInstance(props, auth);
    try {
      // Получить store
      Store store = session.getStore("pop3");
      store.connect();
      // Получить folder
      Folder folder = store.getFolder("INBOX");
      folder.open(Folder.READ_ONLY);
      // Получить каталог
      Message[] message = folder.getMessages();
      Message m;
      // Отобразить поля from (только первый отправитель) и subject сообщений
      for (j=0, n=message.length; j<n; j++) {
        m = message[j];
        Multipart mp = (Multipart) m.getContent();

        String fn = m.getFileName();
        System.out.println(j + ": "
            + m.getFrom()[0]
            + " " + m.getSubject()
            + " " + fn);
        // Вывод содержимого в консоль
        for (i = 0; i < mp.getCount(); i++){
          BodyPart  bp = mp.getBodyPart(i);
          if (bp.getFileName() == null)
            System.out.println("    " + i + ". сообщение : '" +
                bp.getContent() + "'");
          else
            System.out.println("    " + i + ". файл : '" +
                bp.getFileName() + "'");
        }
      }

      // Закрыть соединение
      folder.close(false);
      store.close();
    } catch (Exception e) {
      System.err.println("Ошибка чтения почты: " + e.getMessage());
      return "error";
    }


    return "-\n";
  }



} // end of class
