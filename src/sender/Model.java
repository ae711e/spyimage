/*
 * Copyright (c) 2019. Eremin
 */
/*
  Модель отправителя изображения
 */
package sender;

import ae.MyCrypto;
import ae.Postman;
import ae.R;
import static ae.R.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

class Model
{

  /**
   * Отправить по адресу файл вложения
   * @param email       адрес получателя
   * @param fileAppend  файл вложения
   * @return признак успеха отправки почты
   */
  boolean  sendMailTo(String email, String fileAppend)
  {
    String subj, mess;
    String  app;
    // ключ, который записан на указанный e-mail
    String usr = R.db.s2s(email);
    String pk  = R.db.Dlookup("SELECT publickey FROM keys WHERE usr=" + usr);
    // есть публичный ключ?
    if(pk != null && pk.length() > 1) {
      // Зашифруем файл во временный каталог
      String fout = encryptFile(fileAppend, pk);
      if(fout != null) {
        // отправить зашифрованный документ
        String  fn = getFileName(fileAppend);
        subj = R.Subj_imagedoc + " " + email + " <" + fn + ">";
        mess = "Hello, dear friend! " + email + ".\r" + subj;
        mess += "\r\n Дорогой друг, отправляю тебе специальное изображение документа\r\n";
        app = fout;
      } else {
        System.err.println("?-Error-не удалось зашифровать файл: " + fileAppend);
        return false;
      }
    } else {
      // запросить публичный ключ у получателя
      System.out.println("Получатель " + email + " неизвестен, запросим у него public key");
      String ks = " to email address: [ " + R.Email + " ]";
      subj = R.Subj_askpubkey + ks;
      mess = subj + ".\r\nS uvageniem, abonent S.P.Y. Image.\r\n";
      mess += "\r\nПримечание. Для безопасной пересылки данных нужен публичный ключ.\r\n";
      app = null;
    }
    return Postman.mailSend(email, subj, mess, app);
  }

  /**
   * Взять список получателей из таблицы keys,
   * Все кроме меня.
   * @return массив строк
   */
  Collection<String> getKeysUsers()
  {
    // получим список имен из БД
    ArrayList<String[]> ard = R.db.DlookupArray("SELECT usr,mykey FROM keys WHERE (mykey) is null or mykey!=1 ORDER BY mykey,usr");
    ArrayList<String> list = new ArrayList<>();
    for(String[] r: ard) list.add(r[0]); // добавим имя в массив
    return  list;
  }

  /**
   * Зашифровать файл публичным ключом и вернуть имя зашифрованного файла
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
        // запишем зашифрованный файл во временный файл
        String outFileName = tempFileName(fileName);
        Files.write(Paths.get(outFileName), crypt);
        return outFileName;
      }
    } catch (Exception e) {
      System.out.println("?-Error-encryptFile(): " + e.getMessage());
    }
    return null;
  }

  /**
   * Получить имя временного файла (для зашифрованного файла)
   * @param finp  входное имя файла
   * @return  имя файла во временном каталоге
   */
  private String tempFileName(String finp)
  {
    String f1 = getFileName(finp);
//    String  fext = getFileExtension(finp); // расширение
//    //String f2 = f1.replace(" ","_");
//    String name;
//    try {
//      String tmpd = MimeUtility.encodeText(f1);
//      String fo2  = MimeUtility.decodeText(tmpd);
//      name = f1;
//    } catch (Exception e) {
//      // ошибка кодирования имени файла
//      System.err.println("error mime string " + e.getMessage());
//      name = "filename" + fext;
//    }
//    //String attach = MimeUtility.decodeText(bp.getFileName());  // раскодируем на всякий случай имя файла
    // в Linux разделитель необходим
    return R.TmpDir + R.sep + f1 + R.CryptoExt;
  }

} // end of class
