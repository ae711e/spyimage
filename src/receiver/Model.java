/*
 * Copyright (c) 2019. Eremin
 */
/*
  Модель. Прием зашифрованных изображений
 */

package receiver;

import ae.MyCrypto;
import ae.Postman;
import ae.R;

import javax.mail.*;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static ae.R.extractEmail;

class Model
{
  //private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm");
  private static final SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");


  ArrayList<String[]>  readmails()
  {
    ArrayList<String[]> strRes = new ArrayList<>();
    // @see http://javatutor.net/articles/receiving-mail-with-mail-api
    // http://prostoitblog.ru/poluchenie-pochti-java-mail/
    // https://www.pvsm.ru/java/16472
    // http://toolkas.blogspot.com/2019/02/java.html
    // http://java-online.ru/javax-mail.xhtml
    // http://ryakovlev.blogspot.com/2014/11/java_17.html
    try {
      // Получить store
      Store store = Postman.openStore(); // открыть хранилище почтового сервера
      // Получить folder
      Folder folder;
      // Получить каталог
      Message[] messages;
      assert store != null;
      folder = store.getFolder("INBOX");
      folder.open(Folder.READ_WRITE); //  READ_ONLY
      messages = folder.getMessages();
      // Отобразить поля from (только первый отправитель) и subject сообщений
      for(Message m: messages) {
        String from, fuel;
        fuel = m.getFrom()[0].toString(); // первый отправитель
        from = extractEmail(fuel);  // выделим чистый e-mail
        String subj = m.getSubject();
        if(subj.contains(R.Subj_askpubkey)) {
          // письмо содержит запрос публичного ключа
          if(sendMyPubKey(from)) {
            // Если публичный ключ отправлен удаляем письмо-запрос
            System.out.println("Отправили свой публичный ключ " + from);
            m.setFlag(Flags.Flag.DELETED, true);
          }
          continue;
        }
        // Письмо с публичным ключом отправителя
        if(subj.contains(R.Subj_publickey)) {
          // Письмо содержит публичный ключ отправителя
          Object content = m.getContent();
          if(content instanceof Multipart) {
            // содержимое письма Multipart, найдем вложение с публичным ключом
            Multipart mp = (Multipart) content;
            BodyPart bp = getAttachedPart(mp); // часть сообщения c вложением
            if(bp != null) {
              // файл вложения
              String attach = MimeUtility.decodeText(bp.getFileName());  // раскодируем на всякий случай имя файла
              if(attach.contains(R.PubKeyFileName)) {
                // имя файла похоже на имя зашифрованного публичного ключа
                // прочитаем публичный ключ сразу в строку и сохраним в БД
                String txt = R.InputStream2String(bp.getInputStream());
                String sql = "INSERT INTO keys(usr,publickey) VALUES('" + from + "','"+ txt +"')";
                int a = R.db.ExecSql(sql);
                if(a == 1)
                  System.out.println("Добавили публичный ключ для " + from);
                // удалим письмо
                m.setFlag(Flags.Flag.DELETED, true);
              } else {
                System.out.println("Письмо содержит неправильный файл публичного ключа от " + from);
              }
            }
          }
        }
      }
      // Закрыть соединение
      // https://javaee.github.io/javamail/FAQ#gmaildelete
      folder.close(true);  // false
      //
      // будем искать письма с изображениями и заполнять коллекцию
      folder = store.getFolder("INBOX");
      folder.open(Folder.READ_ONLY); //  READ_ONLY
      messages = folder.getMessages();
      // Отобразить поля from (только первый отправитель) и subject сообщений
      for(Message m: messages) {
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
            BodyPart  bp = getAttachedPart(mp);
            if(bp != null) {
              // файл вложения
              String ssb    = bp.getFileName();
              String attach = MimeUtility.decodeText(ssb);  // раскодируем на всякий случай имя файла
              if(attach.contains(R.CryptoExt)) {
                // имя файла с крипто-расширением могут содержать зашифрованное изображение
                // запомним это письмо
                // @see https://javaee.github.io/javamail/docs/api/javax/mail/Message.html#getSentDate--
                Date  dt = m.getSentDate();
                if(dt == null) dt = m.getReceivedDate();  // как вариант
                if(dt == null) dt = new Date(); // ну просто сейчас :-)
                String sdt = sformat.format(dt);
                String[] r = new String[4];
                r[0] = "" + m.getMessageNumber();
                r[1] = sdt;
                r[2] = from;
                r[3] = attach;
                strRes.add(r);
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
    String subj, mess;
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
      mess = "Hello, dear friend! " + email + ".\rI'm send Your my public key. Sincerely, " + R.Email;
      return Postman.mailSend(email, subj, mess, foname);
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
   * @return имя файла соханения
   */
  String  loadMailImage(int mind)
  {
    String fdatname = null; // имя зашифрованного файла
    try {
      // Получить store
      Store store = Postman.openStore(); // открыть хранилище почтового сервера
      // Получить folder
      Folder folder;
      // Получить каталог
      Message[] messages;
      assert store != null;
      folder = store.getFolder("INBOX");
      folder.open(Folder.READ_ONLY); //  READ_ONLY
      messages = folder.getMessages();
      // Отобразить поля from (только первый отправитель) и subject сообщений
      for(Message m: messages) {
        if(m.getMessageNumber() == mind) {
          // нашли нужное сообщение
          Object content = m.getContent();
          // письмо может содержать вложения - поищем их
          if(content instanceof Multipart) {
            Multipart mp = (Multipart) content;
            for (int ip = 0, nmp = mp.getCount(); ip < nmp; ip++) {
              BodyPart bp = mp.getBodyPart(ip); // часть сообщения
              String fileAttach = bp.getFileName();
              if (fileAttach != null) {
                // файл вложения
                String attach = MimeUtility.decodeText(fileAttach);  // раскодируем на всякий случай имя файла
                if (attach.contains(R.CryptoExt))
                  fdatname = writeAttachFile(bp);
                break;
              }
            }
          } else {
            System.out.println("В письме нет вложения");
          }
          break;
        }
      }
      folder.close(false);
      store.close();
      //
      if(fdatname != null)  {
        // System.out.println("Crypto file " + fdatname);
        String  priv = R.db.Dlookup("SELECT privatekey FROM keys WHERE mykey=1");
        if(priv  != null) {
          return decryptFile(fdatname, priv);
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
        String  ext = R.CryptoExt.replace(".", "\\.")+"$"; // сделаем рег.выр для поиска расширения
        String outFileName = fileName.replaceAll(ext, "");
        Files.write(Paths.get(outFileName), decry);
        return outFileName;
      }
    } catch (Exception e) {
      System.out.println("?-Error-encryptFile(): " + e.getMessage());
    }
    return null;
  }

  /**
   * Получить часть с файлом-вложением из составного письма.
   * Если вложений несколько,то возвращается часть с вложением.
   * @param mp  тело письма
   * @return часть с файлом-вложением или null
   */
  private BodyPart  getAttachedPart(Multipart mp)
  {
    try {
      int n = mp.getCount();
      for (int i = 0; i < n; i++) {
        BodyPart bp = mp.getBodyPart(i); // часть сообщения
        String fileAttach = bp.getFileName();
        if (fileAttach != null)
          return bp;
      }
    } catch (Exception e) {
      System.err.println("?-Error-getAttachedPart(): " + e.getMessage());
    }
    return null; // нет частей с вложением
  }

} // end of class
