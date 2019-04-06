/*
 * Copyright (c) 2019. Eremin
 */
/*
  Модель. Прием зашифрованных изображений
 */

package receiver;

import ae.R;

import javax.mail.*;
import java.util.Properties;

class Model
{
//  private class EmailAuthenticator extends Authenticator {
//    public PasswordAuthentication getPasswordAuthentication() {
//      String username, password;
//      username = R.EmailUser; password = R.EmailPwd;
//      return new PasswordAuthentication(username, password);
//    }
//  }

  String  readmails()
  {
    int i, j, n;
    // @see http://javatutor.net/articles/receiving-mail-with-mail-api
    // http://prostoitblog.ru/poluchenie-pochti-java-mail/
    // https://www.pvsm.ru/java/16472
    // http://toolkas.blogspot.com/2019/02/java.html
    // http://java-online.ru/javax-mail.xhtml
    //
    // Настроить аутентификацию, получить session
    //Authenticator auth = new EmailAuthenticator();
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
    Session session = Session.getDefaultInstance(props, null);
    try {
      // Получить store
      Store store = session.getStore(); // "pop3"

      // Подключение к почтовому серверу
      store.connect(R.ImapServer, R.EmailUser, R.EmailPwd);
      //store.connect();
      // Получить folder
      Folder folder = store.getFolder("INBOX");
      folder.open(Folder.READ_WRITE); //  READ_ONLY
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
              else {
                System.out.println("    " + i + ". файл : '" + bp.getFileName() + "'");
                m.setFlag(Flags.Flag.DELETED, true);
              }
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
      // https://javaee.github.io/javamail/FAQ#gmaildelete
      folder.close(true);  // false
      store.close();
    } catch (Exception e) {
      System.err.println("Ошибка чтения почты: " + e.getMessage());
      return "error";
    }

    return "-\n";
  }


}
