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
    String str = readmails();
    System.out.println(str);
  }

  public class EmailAuthenticator extends Authenticator {
    public PasswordAuthentication getPasswordAuthentication() {
      String username ="", password="";
      //username = R.EmailUser; password = R.EmailPwd;
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
    Authenticator auth = new EmailAuthenticator();
    // Свойства установки
    Properties props = System.getProperties();
    //props.put("mail.pop3.host", R.SmtpServer);
    //
    props.put("mail.debug"          , "false"  );
    props.put("mail.store.protocol" , "imaps"  );
    props.put("mail.imap.host",       R.ImapServer);
    props.put("mail.imap.ssl.enable", R.ImapSSL );
    props.put("mail.imap.port"      , R.ImapPort);
    //
    Session session = Session.getDefaultInstance(props, auth);
    try {
      // Получить store
      Store store = session.getStore(); // "pop3"

      // Подключение к почтовому серверу
      store.connect(R.ImapServer, R.EmailUser, R.EmailPwd);
      //store.connect();
      // Получить folder
      Folder folder = store.getFolder("INBOX");
      folder.open(Folder.READ_ONLY);
      // Получить каталог
      Message[] message = folder.getMessages();
      Message m;
      // Отобразить поля from (только первый отправитель) и subject сообщений
      int nm = message.length;
      for (j=0; j<nm; j++) {
        m = message[j];
        String from = m.getFrom()[0].toString(); // первый отправитель
        String subj = m.getSubject();
        System.out.println(j + ": " + from + " " + subj);
        // Вывод содержимого в консоль
        Object content;
        String contype;
        Multipart mp;
        contype = m.getContentType();
        content = m.getContent();
        if(contype.contains("multipart")) {
          try {
            mp = (Multipart) content;
            // Вывод содержимого в консоль
            n = mp.getCount();
            for (i = 0; i < n; i++) {
              BodyPart bp = mp.getBodyPart(i);
              if (bp.getFileName() == null)
                System.out.println("    " + i + ". текст ");
              else
                System.out.println("    " + i + ". файл : '" + bp.getFileName() + "'");
            }
          } catch (Exception e) {
            System.err.printf(e.getMessage());

          }
        } else {
          // простая строка
          String scont;
          scont = (String) content;
          System.out.println("content: length " + scont.length());
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
