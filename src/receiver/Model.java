/*
 * Copyright (c) 2019. Eremin
 */
/*
  Модель. Прием зашифрованных изображений
 */

package receiver;

import ae.MailSend;
import ae.MyCrypto;
import ae.R;

import javax.mail.*;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.nio.file.*;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static ae.R.extractEmail;

class Model
{
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm");
  private static final SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");


  ArrayList<String[]>  readmails()
  {
    int i, j, n;
    ArrayList<String[]> strRes = new ArrayList<>();
    // @see http://javatutor.net/articles/receiving-mail-with-mail-api
    // http://prostoitblog.ru/poluchenie-pochti-java-mail/
    // https://www.pvsm.ru/java/16472
    // http://toolkas.blogspot.com/2019/02/java.html
    // http://java-online.ru/javax-mail.xhtml
    // http://ryakovlev.blogspot.com/2014/11/java_17.html
    try {
      // Получить store
      Store store = openStore(); // открыть хранилище почтового сервера
      // Получить folder
      Folder folder;
      // Получить каталог
      Message[] messages;
      folder = store.getFolder("INBOX");
      folder.open(Folder.READ_WRITE); //  READ_ONLY
      messages = folder.getMessages();
      // Отобразить поля from (только первый отправитель) и subject сообщений
      int nm = messages.length;
      for (j = 0; j < nm; j++) {
        Message m = messages[j];
        String from, fuel;
        fuel = m.getFrom()[0].toString(); // первый отправитель
        from = extractEmail(fuel);  // выделим чистый e-mail
        String subj = m.getSubject();
        //System.out.println(j + ": " + from + " " + from + " " + subj);
        //
        if(subj.contains(R.Subj_askpubkey)) {
          // письмо содержит запрос публичного ключа
          if(sendMyPubKey(from)) {
            // Если публичный ключ отправлен удаляем письмо-запрос
            System.out.println("Отправили свой публичный ключ " + from);
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
                  // прочитаем публичный ключ сразу в строку и сохраним в БД
                  // http://qaru.site/questions/226/readconvert-an-inputstream-to-a-string
                  InputStream inputStream = bp.getInputStream();
                  Scanner s = new Scanner(inputStream).useDelimiter("\\A");
                  String result = s.hasNext() ? s.next() : "";
                  String sql =
                    "INSERT INTO keys(usr,publickey) VALUES('" + from + "','"+ result +"')";
                  int a = R.db.ExecSql(sql);
                  if(a == 1)
                    System.out.println("Добавили публичный ключ для " + from);
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
      //
      // будем искать письма с изображениями
      folder = store.getFolder("INBOX");
      folder.open(Folder.READ_ONLY); //  READ_ONLY
      messages = folder.getMessages();
      // Отобразить поля from (только первый отправитель) и subject сообщений
      nm = messages.length;
      for (j = 0; j < nm; j++) {
        // Вывод содержимого в консоль
        Message m = messages[j];
        String fuel = m.getFrom()[0].toString(); // первый отправитель
        String from = extractEmail(fuel);  // выделим чистый e-mail
        String subj = m.getSubject();
        if(subj.contains(R.Subj_imagedoc)) {
          // Письмо с изображением
          Object content = m.getContent();
          if(content instanceof Multipart) {
            // письмо может содержать вложения
            // поищем их
            Multipart mp = (Multipart) content;
            for(int ip = 0, nmp = mp.getCount(); ip < nmp; ip++) {
              BodyPart bp = mp.getBodyPart(ip); // часть сообщения
              String fileAttach = bp.getFileName();
              if (fileAttach != null) {
                // файл вложения
                String attach = MimeUtility.decodeText(fileAttach);  // раскодируем на всякий случай имя файла
                if(attach.contains(".dat")) {
                  // имя файла с расширением dat могут содержать зашифрованное изображение
                  // отметим это письмо
                  Date  dt = m.getReceivedDate();
                  if(dt == null) dt = new Date();
                  String sdt = sformat.format(dt);
                  String[] r = new String[4];
                  r[0] = "" + m.getMessageNumber();
                  r[1] = sdt;
                  r[2] = from;
                  r[3] = attach;
                  strRes.add(r);
                  break;
                }
              }
            }
          }
        }
      }
      folder.close(false);  // false
      store.close();
    } catch (Exception e) {
      System.err.println("Ошибка чтения почты: " + e.getMessage());
      String[] r = new String[4];
      r[1] = "error";
      strRes.add(r);
    }
    if(strRes.size() <1) {
      String[] r = new String[4];
      r[1] = "писем нет";
      strRes.add(r);
    }
    return strRes;
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
    //
    //String usr = R.db.s2s(R.Email);
    String pk  = R.db.Dlookup("SELECT publickey FROM keys WHERE mykey=1");
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

  /**
   * Записать файл вложением из части сообщения во временный каталог
   * @param bp  часть тела сообщения
   */
  private String writeAttachFile(BodyPart bp)
  {
    try {
      if (bp.getFileName() != null) {
        String fn = bp.getFileName();
        // http://www.cyberforum.ru/java-j2se/thread1763814.html
        String fname = MimeUtility.decodeText(fn);  // декодируем имя почтового файла вложения
        // System.out.println("файл вложения: '" + fname + "'");
        // запишем во временный каталог
        File fout = new File(R.TmpDir, fname);
        InputStream inps = bp.getInputStream();
        FileOutputStream outs = new FileOutputStream(fout);
        byte[] buf = new byte[8192];
        int bytesRead;
        while ((bytesRead = inps.read(buf)) != -1) {
          outs.write(buf, 0, bytesRead);
        }
        outs.close();
        inps.close();
        // m.setFlag(Flags.Flag.DELETED, true);
        String foutnam;
        foutnam = fout.getPath();
        return foutnam;
      }
    } catch (Exception e) {
      System.out.println("?-Error-writeAttachFile() " + e.getMessage());
    }
    return null;
  }

  /**
   * Загрузить из почты файл, расшифровать его, сохранить на диск и вернуть имя файла
   * @param mind  индекс почты
   * @return
   */
  String  loadMailImage(int mind)
  {
    String fdatname = null; // имя зашифрованного файла
    try {
      // Получить store
      Store store = openStore(); // // открыть хранилище почтового сервера
      // Получить folder
      Folder folder;
      // Получить каталог
      Message[] messages;
      folder = store.getFolder("INBOX");
      folder.open(Folder.READ_ONLY); //  READ_ONLY
      messages = folder.getMessages();
      // Отобразить поля from (только первый отправитель) и subject сообщений
      int nm = messages.length;
      for(int j = 0; j < nm; j++) {
        Message m = messages[j];
        if(m.getMessageNumber() == mind) {
          nm = 0;
          // нашли нужное сообщение
          Object content = m.getContent();
          // письмо может содержать вложения
          // поищем их
          Multipart mp = (Multipart) content;
          for(int ip = 0, nmp = mp.getCount(); ip < nmp; ip++) {
            BodyPart bp = mp.getBodyPart(ip); // часть сообщения
            String fileAttach = bp.getFileName();
            if (fileAttach != null) {
              // файл вложения
              String attach = MimeUtility.decodeText(fileAttach);  // раскодируем на всякий случай имя файла
              if(attach.contains(".dat")) {
                fdatname = writeAttachFile(bp);
                break;
              }
            }
          }
        }
      }
      folder.close(false);
      store.close();
      //
      if(fdatname != null)  {
        // System.out.println("Crypto file " + fdatname);
        String  priv = R.db.Dlookup("SELECT privatekey FROM keys WHERE mykey=1");
        if(priv  != null) {
          String foutname = decryptFile(fdatname, priv);
          return foutname;
        } else {
          System.out.println("Не заданы собственные ключи");
        }
      }
    } catch (Exception e) {
      System.out.println("?-Error-loadMailImage() " + e.getMessage());
    }

    return null;
  }

  /**
   * Расшифровать файл приватным ключом и вернуть имя расшифрованного файла (без .dat)
   * @param fileName  имя файла
   * @param privateKey публичный ключ
   * @return имя выходного файла
   */
  private String  decryptFile(String fileName, String privateKey)
  {
    MyCrypto crypto = new MyCrypto(null, privateKey);
    try {
      // @see https://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#readAllBytes%28java.nio.file.Path%29
      // @see https://ru.stackoverflow.com/questions/448553/%D0%9A%D0%B0%D0%BA-%D0%BF%D0%B5%D1%80%D0%B5%D0%B2%D0%B5%D1%81%D1%82%D0%B8-%D1%84%D0%B0%D0%B9%D0%BB-%D0%B2-%D0%BC%D0%B0%D1%81%D1%81%D0%B8%D0%B2-%D0%B1%D0%B0%D0%B9%D1%82%D0%BE%D0%B2
      byte[] array = Files.readAllBytes(Paths.get(fileName));
      byte[] decry = crypto.decryptBigData(array);
      if(decry != null) {
        // запишем расшифрованный файл
        String outFileName = fileName.replaceAll("\\.dat$", "");
        Files.write(Paths.get(outFileName), decry);
        return outFileName;
      }
    } catch (Exception e) {
      System.out.println("?-Error-encryptFile(): " + e.getMessage());
    }
    return null;
  }

//  private class EmailAuthenticator extends Authenticator {
//    public PasswordAuthentication getPasswordAuthentication() {
//      String username, password;
//      username = R.RecvEmailUser; password = R.RecvEmailPwd;
//      return new PasswordAuthentication(username, password);
//    }
//  }
  /**
   * Получить хранилище почтового сервера и подключиться к нему
   * @return
   */
  private Store openStore()
  {
    Store store;
    //Authenticator auth = null;
    // Свойства установки
    Properties props = System.getProperties();
    //
    props.put("mail.debug", "false"  );
    String  host;
    if(R.ServerProtocol.contains("imap"))
      host = R.ImapServer;
    else
      host = R.Pop3Server;
    //
    props.put("mail.store.protocol" , R.ServerProtocol);

    props.put("mail.imap.host",       R.ImapServer);
    props.put("mail.imap.ssl.enable", R.ImapSSL );
    props.put("mail.imap.port"      , R.ImapPort);
    //
    props.put("mail.pop3.host",       R.Pop3Server);
    props.put("mail.pop3.ssl.enable", R.Pop3SSL );
    props.put("mail.pop3.port"      , R.Pop3Port);
    // Настроить аутентификацию, получить session
    //auth = new EmailAuthenticator();
    //
    Session session = Session.getDefaultInstance(props, null);
    try {
      // Получить store
      store = session.getStore(); // "pop3"
      // Подключение к почтовому серверу
      store.connect(host, R.RecvEmailUser, R.RecvEmailPwd);
      //store.connect();  
    } catch(Exception e){
      System.out.println("?Error-нет подключения к почтовому серверу " + e.getMessage());
      return null;
    }
    return store;
  }
  
} // end of class
