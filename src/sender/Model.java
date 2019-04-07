/*
 * Copyright (c) 2019. Eremin
 */
/*
  Модель отправителя изображения
 */
package sender;

import ae.MailSend;
import ae.MyCrypto;
import ae.R;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

class Model
{

  /**
   * Отправить по адресу файл вложения
   * @param email       адрес получателя
   * @param fileAppend  файл вложения
   * @return
   */
  boolean  sendMailTo(String email, String fileAppend)
  {
    String otv, subj, mess;
    MailSend msg = new MailSend();
    String  app;
    //
    String usr = R.db.s2s(email);
    String pk  = R.db.Dlookup("SELECT publickey FROM keys WHERE usr=" + usr);
    // есть публичный ключ?
    if(pk != null && pk.length() > 1) {
      String fout = encryptFile(fileAppend, pk);
      if(fout != null) {
        // отправить зашифрованный документ
        subj = R.Subj_imagedoc + " " + email;
        mess = "Hello, dear friend! " + email + ".\r" + subj;
        app = fout;
      } else {
        System.err.println("?-Error-не удалось зашифровать файл: " + fileAppend);
        return false;
      }
    } else {
      // запросить публичный ключ у получателя
      System.out.println("Получатель " + email + " неизвестен, запросить у него public key");
      String ks = " to email address: [ " + R.Email + " ]";
      subj = R.Subj_askpubkey + ks;
      mess = subj + ".\r\n" + ks + " .\r\n";
      app = null;
    }
    otv = msg.mailSend(email, subj, mess, app);
    return (otv != null);
  }

  /**
   * Взять список получателей из таблицы keys
   * @return массив строк
   */
  Collection<String> getKeysUsers()
  {
    // получим список имен из БД
    ArrayList<String[]> ard = R.db.DlookupArray("SELECT usr FROM keys ORDER BY usr");
    ArrayList<String> list = new ArrayList<>();
    for(String[] r: ard) list.add(r[0]); // добавим имя в массив
    return  list;
  }

  /**
   * Зашифровать файл публичным ключом и вернуть имя звшифрованного файла
   * @param fileName  имя файла
   * @param publicKey публичный ключ
   * @return имя выходного файла
   */
  private String  encryptFile(String fileName, String publicKey)
  {
    MyCrypto  crypto = new MyCrypto(publicKey, null);
    try {
      // @see https://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#readAllBytes%28java.nio.file.Path%29
      // @see https://ru.stackoverflow.com/questions/448553/%D0%9A%D0%B0%D0%BA-%D0%BF%D0%B5%D1%80%D0%B5%D0%B2%D0%B5%D1%81%D1%82%D0%B8-%D1%84%D0%B0%D0%B9%D0%BB-%D0%B2-%D0%BC%D0%B0%D1%81%D1%81%D0%B8%D0%B2-%D0%B1%D0%B0%D0%B9%D1%82%D0%BE%D0%B2
      byte[] array = Files.readAllBytes(Paths.get(fileName));
      byte[] crypt = crypto.encryptBigData(array);
      if(crypt != null) {
        // запишем зашифрованный файл
        String outFileName = fileName + ".dat";
        Files.write(Paths.get(outFileName), crypt);
        return outFileName;
      }
    } catch (Exception e) {
      System.out.println("?-Error-encryptFile(): " + e.getMessage());
    }
    return null;
  }

} // end of class
