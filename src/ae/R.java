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

/**
 * Created by ae on 28.01.2017.
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

  static String SmtpServer     = _r.smtpserver;       // адрес почтового сервера
  static int    SmtpServerPort = _r.smtpserverport;   // порт почтового сервера
  static String SmtpSender     = _r.smtpsender;       // адрес отправителя почты
  static String SmtpServerUser = _r.smtpserveruser;   // имя пользователя почтового сервера
  static String SmtpServerPwd  = _r.smtpserverpwd;    // пароль пользователя почтового сервера

  /**
   * Загрузить параметры по-умолчанию из БД таблицы "_Info"
   */
  static void loadDefault(Database db)
  {
    // прочитать из БД значения часов выдержки
    R.SmtpMailCC      = R.getInfo(db, "SmtpMailCC",      R.SmtpMailCC);       // кому отсылать копии
    R.ProxyServer     = R.getInfo(db, "ProxyServer",     R.ProxyServer);      // прокси сервер
    R.ProxyPort       = R.getInfo(db, "ProxyPort",       R.ProxyPort);        // прокси порт
    R.ProxyUser       = R.getInfo(db, "ProxyUser",       R.ProxyUser);        // прокси пользователь
    R.ProxyPass       = R.getInfo(db, "ProxyPass",       R.ProxyPass);        // прокси пароль

    // System.out.println("HoursNotOnLine  = " + R.HoursNotOnLine);
    // System.out.println("HoursAfterEmail = " + R.HoursAfterEmail);
    // System.out.println("HoursExpLens    = " + R.HoursExpLens);
    // System.out.println("HoursTasksBack  = " + R.HoursTasksBack);
    // System.out.println("HoursExpTasks   = " + R.HoursExpTasks);
    // System.out.println("TaskQuestDelay  = " + R.TaskQuestDelay);
    // System.out.println("TaskFail        = " + R.TaskFail);
    // System.out.println("MetaTasks       = " + R.MetaTasks);
    // System.out.println("MetaTasksID     = " + R.MetaTasksID);
    // System.out.println("ProxyServer     = " + R.ProxyServer);
    // System.out.println("ProxyPort       = " + R.ProxyPort);
    // System.out.println("TimeOut (ms)    = " + R.TimeOut);
    //
    //LogRecordTTL
    //
  }

  /**
     * Пауза выполнения программы
     * @param time   время задержки, мсек
     */
    static void sleep(long time)
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
        String val = db.Dlookup("SELECT val FROM _Info WHERE key='" + keyName + "'");
        if(val == null) {
            return defaultValue;
        }
        return Integer.parseInt(val);
    }

    /**
     * Получить из таблицы _Info значение ключа, а если таблицы или ключа нет, то вернуть значение по-умолчанию
     * CREATE TABLE _Info(key text PRIMARY KEY, val text)
     * @param db            база данных с таблицей Info
     * @param keyName       имя ключа
     * @param defaultValue  значение по-умолчанию
     * @return значение ключа (действительное число)
     */
    private static double getInfo(Database db, String keyName, double defaultValue)
    {
        String val = db.Dlookup("SELECT val FROM _Info WHERE key='" + keyName + "'");
        if(val == null) {
            return defaultValue;
        }
        return Double.parseDouble(val);
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

} // end of class
