/*
 * Copyright (c) 2019. Eremin
 */
/*
  Модель. Прием зашифрованных изображений
 */

package receiver;

import ae.MailSend;
import ae.R;

import javax.mail.*;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static ae.R.extractEmail;

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
    String strRes = "";
    // @see http://javatutor.net/articles/receiving-mail-with-mail-api
    // http://prostoitblog.ru/poluchenie-pochti-java-mail/
    // https://www.pvsm.ru/java/16472
    // http://toolkas.blogspot.com/2019/02/java.html
    // http://java-online.ru/javax-mail.xhtml
    // http://ryakovlev.blogspot.com/2014/11/java_17.html
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
      Folder folder;
      // Получить каталог
      Message[] message;
      Message m;
      folder = store.getFolder("INBOX");
      folder.open(Folder.READ_WRITE); //  READ_ONLY
      message = folder.getMessages();
      // Отобразить поля from (только первый отправитель) и subject сообщений
      int nm = message.length;
      for (j = 0; j < nm; j++) {
        m = message[j];
        String from, fuel;
        fuel = m.getFrom()[0].toString(); // первый отправитель
        from = extractEmail(fuel);  // выделим чистый e-mail
        String subj = m.getSubject();
        System.out.println(j + ": " + from + " " + from + " " + subj);
        //
        if(subj.contains(R.Subj_askpubkey)) {
          // письмо содержит запрос публичного ключа
          if(sendMyPubKey(from)) {
            // Если публичный ключ отправлен удаляем письмо-запрос
            m.setFlag(Flags.Flag.DELETED, true);
          }
          continue;
        }
        //
        if(subj.contains(R.Subj_publickey)) {
          // Письмо содержит публичный ключ отправителя
          Object content = m.getContent();
          if(content instanceof Multipart) {
            // содержимое письма Multipart, найдем вложение с публичным ключом
            Multipart mp = (Multipart) content;
            for(int ip = 0, nmp = mp.getCount(); ip < nmp; ip++) {
              BodyPart bp = mp.getBodyPart(ip); // часть сообщения
              String fileAttach = bp.getFileName();
              if (fileAttach != null) {
                // файл вложения
                String attach = MimeUtility.decodeText(fileAttach);  // раскодируем на всякий случай имя файла
                if(attach.contains(R.PubKeyFileName)) {
                  // имя файла похоже на имя публичного ключа
                  // прочитаем публичный ключ и сохраним в БД
                  InputStream inps = bp.getInputStream();
                  int lfpk = inps.available(); // размер данных
                  byte[] attachment = new byte[lfpk];
                  inps.read(attachment);
                  String sql = "INSERT INTO keys(usr,publickey) VALUES('"
                      + from + "','"+ attach +"')";
                  R.db.ExecSql(sql);
                }
              }
            }
          }
          // удалим письмо
          m.setFlag(Flags.Flag.DELETED, true);
          continue;
        }
      }
      // Закрыть соединение
      // https://javaee.github.io/javamail/FAQ#gmaildelete
      folder.close(true);  // false

      folder = store.getFolder("INBOX");
      folder.open(Folder.READ_ONLY); //  READ_ONLY
      message = folder.getMessages();
      // Отобразить поля from (только первый отправитель) и subject сообщений
      nm = message.length;
      for (j = 0; j < nm; j++) {
        // Вывод содержимого в консоль
        m = message[j];
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
                String fn = bp.getFileName();
                // http://www.cyberforum.ru/java-j2se/thread1763814.html
                String fname = MimeUtility.decodeText(fn);  // декодируем имя почтового файла вложения
                System.out.println("    " + i + ". файл : '" + fname + "'");
                // запишем во временный каталог
                File  fout = new File(R.TmpDir, fname);
                InputStream inps = bp.getInputStream();
                FileOutputStream outs = new FileOutputStream(fout);
                byte[] buf = new byte[4096];
                int bytesRead;
                while((bytesRead = inps.read(buf)) != -1) { outs.write(buf, 0, bytesRead); }
                outs.close();
                inps.close();
                // m.setFlag(Flags.Flag.DELETED, true);
              }
            }
          } catch (Exception e) {
            System.err.println(e.getMessage());
          }
        } else {
          // простая строка
          String scont;
          scont = (String) content;
          System.out.println("content: length " + scont.length());
        }
      }
      folder.close(false);  // false
      store.close();
    } catch (Exception e) {
      System.err.println("Ошибка чтения почты: " + e.getMessage());
      return "error";
    }

    return "-\n";
  }

  /**
   * Отправить собственный публичный ключ по адресу
   * @param email адрес
   * @return результат отправки
   */
  private boolean sendMyPubKey(String email)
  {
    String otv, subj, mess;
    MailSend msg = new MailSend();
    String  app;
    //
    String usr = R.db.s2s(email);
    String pk  = R.db.Dlookup("SELECT publickey FROM keys WHERE usr=" + usr);
    // есть публичный ключ?
    if(pk != null && pk.length() > 1) {
      // запишем публичный ключ в файл
      File  fout = new File(R.TmpDir, R.PubKeyFileName);
      String  foname = fout.getPath();
      if(!R.writeStr2File(pk, foname))
        return false; // ошибка записи
      // отправить публичный ключ
      subj = R.Subj_publickey + " " + R.Email;
      mess = "Hello, dear friend! " + email + ".\rSend Your my public key. Sincerely, " + R.Email;
      otv = msg.mailSend(email, subj, mess, foname);
      return (otv != null);
    }
    return false;
  }

} // end of class
