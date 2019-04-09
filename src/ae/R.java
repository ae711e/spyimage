/*
 * Copyright (c) 2017. Eremin
 * 16.03.2017 21:03
 * 03.04.2019
 */

package ae;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Ресурсный класс
*/
/*
Modify:

*/

public class R {
  final static String Ver = "1.0"; // номер версии

  // рабочая БД
  static final String WorkDB = "spyimage.db";   // CentOs Linux (в Windows будет D:\var\Gmir\*.db)
  static public Database  db;   // база данных проекта
  // выдать временный каталог (завершается обратным слэшем)
  public final static String TmpDir = System.getProperty("java.io.tmpdir");
  // разделитель имени каталогов
  public final static String sep = System.getProperty("file.separator");

  // тема письма с зашифрованным изображением документа
  final static public String  Subj_imagedoc  = "(spyimage)_Image_of_document_for_person";
  // тема письма с запросом о публичном ключе
  final static public String  Subj_askpubkey = "(spyimage)_Ask_give_me_Your_public_key";
  // тема письма с ответом, содержащий public key
  final static public String  Subj_publickey = "(spyimage)_Please_get_my_public_key";
  // имя файла с публичным ключом
  final static public String  PubKeyFileName = "publickey.key";
  // расширение зашифрованных файлов
  final static public String  CryptoExt = ".spydat";

  // final static String sep = System.getProperty("file.separator"); // разделитель имени каталогов
  static String ProxyServer = _r.proxyserv;  // proxy сервер
  static int    ProxyPort   = _r.proxyport;  // порт proxy-сервера
  static int    TimeOut     = 30000;         // тайм-аут мс
  static String ProxyUser   = _r.proxyuser;
  static String ProxyPass   = _r.proxypass;
  //
  // почтовые дела
  // адрес получателя почты (можно несколько с разделением по ;)
  static String SmtpMailCC     = null;              // адрес получателя копии почты

  public static String Email      = _r.email;       // адрес почты
  //
  public static String SmtpUser   = _r.smtpuser;   // имя пользователя получения от почтового сервера
  public static String SmtpPwd    = _r.smtppwd;    // пароль пользователя почтового сервера
  public static String SmtpServer = _r.smtpserver;  // адрес почтового сервера
  public static String SmtpPort   = _r.smtpport;    // (25) порт почтового сервера
  public static String SmtpSSL    = _r.smtpssl;     // протокол SSL SMTP сервера
  //
  public static String PostProtocol = _r.protocol;  // imap pop3 протокол сервера
  public static String PostUser   = _r.postuser;    // имя пользователя посылки почтового сервера
  public static String PostPwd    = _r.postpwd;     // пароль пользователя почтового сервера
  public static String PostServer = _r.postserver;  // адрес почтового сервера
  public static String PostPort   = _r.postport;    // порт
  public static String PostSSL    = _r.postssl;     // протокол SSL

  /**
   * Проверить наличие базы данных и создать нужные таблицы
   */
  static void testDb()
  {
    final String create_tables =
        "CREATE TABLE _Info(key VARCHAR(32)  PRIMARY KEY, val text);" +
        "CREATE TABLE keys (usr VARCHAR(255) PRIMARY KEY, publickey TEXT, privatekey TEXT, mykey INT unique, wdat DATETIME DEFAULT (DATETIME('now', 'localtime')));" +
            "INSERT INTO _Info(key) VALUES('Email');" +
            "INSERT INTO _Info(key) VALUES('SmtpUser');" +
            "INSERT INTO _Info(key) VALUES('SmtpPwd');" +
            "INSERT INTO _Info(key) VALUES('SmtpServer');" +
            "INSERT INTO _Info(key) VALUES('SmtpPort');" +
            "INSERT INTO _Info(key) VALUES('SmtpSSL');" +
            "INSERT INTO _Info(key) VALUES('PostProtocol');" +
            "INSERT INTO _Info(key) VALUES('PostUser');" +
            "INSERT INTO _Info(key) VALUES('PostPwd');" +
            "INSERT INTO _Info(key) VALUES('PostServer');" +
            "INSERT INTO _Info(key) VALUES('PostPort');" +
            "INSERT INTO _Info(key) VALUES('PostSSL');" +
            "";
    if(db == null) {
      db = new DatabaseSqlite(WorkDB);
      //
      String str = db.Dlookup("SELECT COUNT(*) FROM _Info;");
      if (str == null) {
        // ошибка чтения из БД - создадим таблицу
        String ssql[] = create_tables.split(";"); // разобьем на отдельные операторы
        for (String ss: ssql)
          db.ExecSql(ss);
      }
    }
  }

  /**
   * Загрузить параметры по-умолчанию из БД таблицы "_Info"
   */
  static public void loadDefault()
  {
    testDb(); // проверить наличие БД
    // прочитать из БД значения часов выдержки
    R.SmtpMailCC      = R.getInfo(db, "SmtpMailCC",     R.SmtpMailCC);       // кому отсылать копии
    R.ProxyServer     = R.getInfo(db, "ProxyServer",    R.ProxyServer);      // прокси сервер
    R.ProxyPort       = R.getInfo(db, "ProxyPort",      R.ProxyPort);        // прокси порт
    R.ProxyUser       = R.getInfo(db, "ProxyUser",      R.ProxyUser);        // прокси пользователь
    R.ProxyPass       = R.getInfo(db, "ProxyPass",      R.ProxyPass);        // прокси пароль
    //
    getAccount();
  }

  static public void dbClose()
  {
    if(db != null) {
      db.close();
      db = null;
    }
  }

  static public void getAccount()
  {
    // прочитать из БД значения аккаунта
    Email       = getInfo(db, "Email", Email);            // адрес отправителя
    SmtpUser    = getInfo(db, "SmtpUser", SmtpUser);      // имя пользователя для передачи писем
    SmtpPwd     = getInfo(db, "SmtpPwd", SmtpPwd);        // пароль пользователя для передачи
    SmtpServer  = getInfo(db, "SmtpServer", SmtpServer);  // адрес SMTP почтового сервера
    SmtpPort    = getInfo(db, "SmtpPort",   SmtpPort);    // порт SMTP сервера
    SmtpSSL     = getInfo(db, "SmtpSSL", SmtpSSL);        // протокол SSL SMTP сервера
    PostProtocol = getInfo(db, "PostProtocol", PostProtocol); // протокол приема писем из почтового сервера
    PostUser    = getInfo(db, "PostUser", PostUser);      // имя пользователя для приема писем
    PostPwd     = getInfo(db, "PostPwd", PostPwd);        // пароль пользователя для приема
    PostServer  = getInfo(db, "PostServer", PostServer);  // адрес IMAP/POP3 почтового сервера
    PostPort    = getInfo(db, "PostPort", PostPort);      // порт IMAP/POP3 сервера
    PostSSL     = getInfo(db, "PostSSL", PostSSL);        // протокол SSL IMAP/POP3 сервера
  }

  static public void putAccount()
  {
    // положить в БД значения аккаунта
    putInfo(db, "Email", Email);            // адрес отправителя
    putInfo(db, "SmtpUser", SmtpUser);      // имя пользователя для передачи писем
    putInfo(db, "SmtpPwd", SmtpPwd);        // пароль пользователя для передачи
    putInfo(db, "SmtpServer", SmtpServer);  // адрес SMTP почтового сервера
    putInfo(db, "SmtpPort",   SmtpPort);    // порт SMTP сервера
    putInfo(db, "SmtpSSL", SmtpSSL);        // протокол SSL SMTP сервера
    putInfo(db, "PostProtocol", PostProtocol); // протокол приема писем из почтового сервера
    putInfo(db, "PostUser", PostUser);      // имя пользователя для приема писем
    putInfo(db, "PostPwd", PostPwd);        // пароль пользователя для приема
    putInfo(db, "PostServer", PostServer);  // адрес IMAP/POP3 почтового сервера
    putInfo(db, "PostPort", PostPort);      // порт IMAP/POP3 сервера
    putInfo(db, "PostSSL", PostSSL);        // протокол SSL IMAP/POP3 сервера
  }

  /**
   * Пауза выполнения программы
   * @param time   время задержки, мсек
   */
  static public void sleep(long time)
  {
      try {
          Thread.sleep(time);
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
  }

  /**
   * прочитать ресурсный файл
   * by novel  http://skipy-ru.livejournal.com/5343.html
   * https://docs.oracle.com/javase/tutorial/deployment/webstart/retrievingResources.html
   * @param nameRes - имя ресурсного файла
   * @return -содержимое ресурсного файла
   */
  public String readRes(String nameRes)
  {
      String str = null;
      ByteArrayOutputStream buf = readResB(nameRes);
      if(buf != null) {
          str = buf.toString();
      }
      return str;
  }

  /**
   * Поместить ресурс в байтовый массив
   * @param nameRes - название ресурса (относительно каталога пакета)
   * @return - байтовый массив
   */
  private ByteArrayOutputStream readResB(String nameRes)
  {
      try {
          // Get current classloader
          InputStream is = getClass().getResourceAsStream(nameRes);
          if(is == null) {
              System.out.println("Not found resource: " + nameRes);
              return null;
          }
          // https://habrahabr.ru/company/luxoft/blog/278233/ п.8
          BufferedInputStream bin = new BufferedInputStream(is);
          ByteArrayOutputStream bout = new ByteArrayOutputStream();
          int len;
          byte[] buf = new byte[512];
          while((len=bin.read(buf)) != -1) {
              bout.write(buf,0,len);
          }
          return bout;
      } catch (IOException ex) {
          ex.printStackTrace();
      }
      return null;
  }

  /**
   * Записать в файл текст из строки
   * @param strTxt - строка текста
   * @param fileName - имя файла
   * @return      true - записано, false - ошибка
   */
  public static boolean  writeStr2File(String strTxt, String fileName)
  {
      File f = new File(fileName);
      try {
          PrintWriter out = new PrintWriter(f);
          out.write(strTxt);
          out.close();
      } catch(IOException ex) {
        System.err.println("?Error-writeStr2File() " + ex.getMessage());
        return false;
      }
      return true;
  }

  /**
   *  Записать в файл ресурсный файл
   * @param nameRes   имя ресурса (от корня src)
   * @param fileName  имя файла, куда записывается ресурс
   * @return  true - запись выполнена, false - ошибка
   */
  public boolean writeRes2File(String nameRes, String fileName)
  {
      boolean b = false;
      ByteArrayOutputStream buf = readResB(nameRes);
      if(buf != null) {
          try {
              FileOutputStream fout = new FileOutputStream(fileName);
              buf.writeTo(fout);
              fout.close();
              b = true;
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
      return b;
  }

  /**
   * Загружает текстовый ресурс в заданной кодировке
   * @param name      имя ресурса
   * @param code_page кодировка, например "Cp1251"
   * @return          строка ресурса
   */
  public String getText(String name, String code_page)
  {
      StringBuilder sb = new StringBuilder();
      try {
          InputStream is = this.getClass().getResourceAsStream(name);  // Имя ресурса
          BufferedReader br = new BufferedReader(new InputStreamReader(is, code_page));
          String line;
          while ((line = br.readLine()) !=null) {
              sb.append(line);  sb.append("\n");
          }
      } catch (IOException ex) {
          ex.printStackTrace();
      }
      return sb.toString();
  }

  /**
   * Получить из таблицы _Info значение ключа, а если таблицы или ключа нет, то вернуть значение по-умолчанию
   * CREATE TABLE _Info(key text PRIMARY KEY, val text)
   * @param db            база данных с таблицей Info
   * @param keyName       имя ключа
   * @param defaultValue  значение по-умолчанию
   * @return значение ключа
   */
  private static int getInfo(Database db, String keyName, int defaultValue)
  {
      String val = getInfo(db, keyName, Integer.toString(defaultValue));
      return Integer.parseInt(val);
  }

  /**
   * Получить из таблицы _Info значение ключа, а если таблицы или ключа нет, то вернуть значение по-умолчанию
   * CREATE TABLE _Info(key text PRIMARY KEY, val text)
   * @param db            база данных с таблицей Info
   * @param keyName       имя ключа
   * @param defaultValue  значение по-умолчанию
   * @return значение ключа (строка)
   */
  private static String getInfo(Database db, String keyName, String defaultValue)
  {
      String val = db.Dlookup("SELECT val FROM _Info WHERE key='" + keyName + "'");
      if(val == null /*|| val.length() < 1*/) {
          return defaultValue;
      }
      return val;
  }

  /**
   * Записать в таблицу параметров числовое значение
   * CREATE TABLE _Info(key text PRIMARY KEY, val text)
   * @param db        база данных с таблицей Info
   * @param keyName   имя ключа
   * @param Value     значение
   */
  private static void putInfo(Database db, String keyName, int Value)
  {
    putInfo(db, keyName, Integer.toString(Value));
  }

  /**
   * Записать в таблицу параметров строковое значение
   * CREATE TABLE _Info(key text PRIMARY KEY, val text)
   * @param db        база данных с таблицей Info
   * @param keyName   имя ключа
   * @param Value     значение
   */
  private static void putInfo(Database db, String keyName, String Value)
  {
    String val;
    if(Value == null /*|| Value.length() < 1*/)
      val = "null";
    else
      val = db.s2s(Value);
    db.ExecSql("UPDATE _Info SET val=" + val + " WHERE key='" + keyName + "'");
  }

  /**
   * преобразовать секунды UNIX эпохи в строку даты
   * @param unix  секунды эпохи UNIX
   * @return дата и время в формате SQL (ГГГГ-ММ-ДД ЧЧ:ММ:СС)
   */
  public static String unix2datetimestr(int unix)
  {
    Date date = new Date(unix*1000L);
    // format of the date
    SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //jdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
    return jdf.format(date);
  }

  /*
   * Преобразование строки времени вида ЧЧ:ММ:СС в кол-во секунд
   * @param str   входная строка времени (0:0:2)
   * @return  кол-во секунд

  public static int hms2sec(String str)
  {
    String[] sar;
    int result = 0;
    try {
      sar = str.split(":", 3);
      int ih = Integer.parseInt(sar[0]);
      int im = Integer.parseInt(sar[1]);
      int is = Integer.parseInt(sar[2]);
      result = ih * 3600 + im * 60 + is;
    } catch (Exception e) {
      //e.printStackTrace();
      result = -1;
    }
    return result;
  }
*/

  /////////////////////////////////////////////////////////////////////////////////

  /**
   * Скопировать входной поток в строку
   * @param inputStream входной поток
   * @return выходная строка
   */
  public static String InputStream2String(InputStream inputStream)
  {
    // http://qaru.site/questions/226/readconvert-an-inputstream-to-a-string
    Scanner s = new Scanner(inputStream).useDelimiter("\\A");
    String txt = s.hasNext() ? s.next() : "";
    return txt;
  }

  /**
   * Выделить e-mail из входной строки
   * @param inputStr входная строка
   * @return строка e-mail или null если ажреса нет
   */
  public static String  extractEmail(String inputStr)
  {
    Matcher mat = email_pattern.matcher(inputStr);
    if(mat.find()) {
      String m = mat.group();
      return m;
    }
    return null;
  }
  // регулярное выражение для выделения эл. адреса
  private static Pattern email_pattern = Pattern.compile("[a-z0-9_.\\-]+@[a-z0-9.\\-]+\\.[a-z]{2,4}",Pattern.CASE_INSENSITIVE);


} // end of class
