/*
 * Copyright (c) 2017. Eremin
 * 16.03.2017 21:03
 * 03.04.2019
 */

package ae;

import java.io.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Ресурсный класс
*/
/*
Modify:

*/

public class R {
  final static String Ver = "1.0"; // номер версии

  // рабочая БД
  //public static String  WorkDB = "C:/tmp/asrevizor.db";
  static String WorkDB = "spyimage.db";   // CentOs Linux (в Windows будет D:\var\Gmir\asrevizor.db)
  static public Database  db;   // база данных проекта

  // final static String sep = System.getProperty("file.separator"); // разделитель имени каталогов
  static String ProxyServer = _r.proxyserv;  // proxy сервер
  static int    ProxyPort   = _r.proxyport;  // порт proxy-сервера
  static int    TimeOut     = 30000;         // тайм-аут мс
  static String ProxyUser   = _r.proxyuser;
  static String ProxyPass   = _r.proxypass;
  //
  // почтовые дела
  // адрес получателя почты (можно несколько с разделением по ;)
  static String SmtpMailCC     = _r.smtpmailcc;          // адрес получателя копии почты

  public static String SmtpSender     = _r.smtpsender;       // адрес отправителя почты
  public static String SmtpServer     = _r.smtpserver;       // адрес почтового сервера
  public static String SmtpServerPortSend = _r.smtpserverportsend;   // порт почтового сервера
  public static String SmtpServerPortRecv = _r.smtpserverportrecv;   // порт почтового сервера приема
  public static String SmtpServerUser = _r.smtpserveruser;   // имя пользователя почтового сервера
  public static String SmtpServerPwd  = _r.smtpserverpwd;    // пароль пользователя почтового сервера

  /**
   * Проверить наличие базы данных и создать нужные таблицы
   */
  static void testDb()
  {
    final String create_tables = "CREATE TABLE _Info(key VARCHAR(32) PRIMARY KEY, val text);CREATE TABLE keys (usr VARCHAR(255) PRIMARY KEY, publickey TEXT, privatekey TEXT, flag INT DEFAULT 0, wdat DATETIME DEFAULT (DATETIME('now', 'localtime')));INSERT INTO _Info(key) VALUES('SmtpSender');INSERT INTO _Info(key) VALUES('SmtpServer');INSERT INTO _Info(key) VALUES('SmtpServerPortSend');INSERT INTO _Info(key) VALUES('SmtpServerPortRecv');INSERT INTO _Info(key) VALUES('SmtpServerUser');INSERT INTO _Info(key) VALUES('SmtpServerPwd');";
    if(db == null) {
      db = new DatabaseSqlite(WorkDB);
      //
      String str = db.Dlookup("SELECT COUNT(*) FROM _Info;");
      if (str == null) {
        // ошибка чтения из БД - создадим таблицу
        String ssql[] = create_tables.split(";"); // разобьем на отдельные операторы
        for (String ss: ssql)  db.ExecSql(ss);
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
    // прочитать из БД значения часов выдержки
    SmtpSender         = getInfo(db, "SmtpSender",     SmtpSender);        // адрес отправителя
    SmtpServer         = getInfo(db, "SmtpServer",     SmtpServer);       // адрес почтового сервера
    SmtpServerPortSend = getInfo(db, "SmtpServerPortSend", SmtpServerPortSend);      // порт сервера
    SmtpServerPortRecv = getInfo(db, "SmtpServerPortRecv", SmtpServerPortRecv);      // порт сервера
    SmtpServerUser     = getInfo(db, "SmtpServerUser", SmtpServerUser);        // имя пользователя для регистрации на сервере
    SmtpServerPwd      = getInfo(db, "SmtpServerPwd",  SmtpServerPwd);        // пароль пользователя для регистрации на сервере
  }

  static public void putAccount()
  {
    // положить в БД значения аккаунта
    putInfo(db, "SmtpSender",     SmtpSender);        // адрес отправителя
    putInfo(db, "SmtpServer",     SmtpServer);       // адрес почтового сервера
    putInfo(db, "SmtpServerPortSend", SmtpServerPortSend);      // порт сервера
    putInfo(db, "SmtpServerPortRecv", SmtpServerPortRecv);      // порт сервера
    putInfo(db, "SmtpServerUser", SmtpServerUser);        // имя пользователя для регистрации на сервере
    putInfo(db, "SmtpServerPwd",  SmtpServerPwd);        // пароль пользователя для регистрации на сервере
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
  public boolean writeStr2File(String strTxt, String fileName)
  {
      File f = new File(fileName);
      try {
          PrintWriter out = new PrintWriter(f);
          out.write(strTxt);
          out.close();
      } catch(IOException ex) {
          ex.printStackTrace();
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
      if(val == null) {
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
    if(Value == null || Value.length() < 1)
      val = "null";
    else
      val = db.s2s(Value);
    db.ExecSql("UPDATE _Info SET val=" + val + " WHERE key='" + keyName + "'");
  }

    /**
   * Копировать содержимое таблицы в другую аналогичную таблицу
   * @param db      база данных
   * @param tabSrc  исходная таблица
   * @param tabDst  таблица, куда записывают
   * @return  кол-во скопированных записей
   */
  static int copyTab2Tab(Database db, String tabSrc, String tabDst)
  {
    int a = 0;
    // получим набор полей
    try {
      Statement stm = db.getDbStatement();
      ResultSet rst = stm.executeQuery("SELECT * FROM " + tabSrc);
      ResultSetMetaData md = rst.getMetaData();
      int Narr = md.getColumnCount();
      StringBuilder nabor = new StringBuilder(256);
      for (int i = 1; i <= Narr; i++) {
        if(i > 1) nabor.append(",");
        nabor.append(md.getColumnName(i));
      }
      rst.close();
      // System.out.println(nabor);
      String sql;
      // синтаксис Sqlite!
      sql = "INSERT OR IGNORE INTO " + tabDst + "(" + nabor + ") SELECT " + nabor + " FROM " + tabSrc;
      a = db.ExecSql(sql);
    } catch (Exception e) {
      System.out.println("?-Error-don't copy table. " + e.getMessage());
    }
    return a;
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

} // end of class
