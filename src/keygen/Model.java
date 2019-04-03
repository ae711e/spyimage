package keygen;

import ae.*;

import java.util.ArrayList;

/*
 Модель формирования ключа
 */

public class Model {
  private String    f_dbName = "keys.db";
  private Database  f_db;    // база данных ключей
  private String lastError = "";
  Model()
  {
    Database db = new DatabaseSqlite(f_dbName);
    f_db = db;
  }

  /**
   * Взять список получателей из таблицы keys в виде массива строк
   * @return массив строк
   */
  public String[]  getUsrKeys()
  {
    // получим список имен из БД
    ArrayList<String[]> ardb = f_db.DlookupArray("SELECT usr FROM keys ORDER BY usr");
    int n = ardb.size();          // кол-во имен
    String[] ar = new String[n];  // создадим массив для значений имен
    int i = 0;
    for (String[] rst: ardb) {
      ar[i++] = rst[0]; // добавим имя в массив
    }
    return  ar;
  }

  /**
   * Генерировать пару ключей RSA
   * @return  String[2] [0]:public [1]:private ключи
   */
  public String[] keyGen()
  {
    MyCrypto mc = new MyCrypto(null,null);
    mc.generateKeys();
    String[]  keys = new String[2];
    keys[0] = mc.getPublicKey();
    keys[1] = mc.getPrivateKey();
    return keys;
  }

  public boolean  addUser(String usrName, String pubKey, String privKey)
  {
    if(usrName == null || pubKey == null || privKey == null) return false;
    if(usrName.length()<1 || pubKey.length()<1 || privKey.length()<1) return false;
    String  sql;
    String  un  = f_db.s2s(usrName);
    String  pub = f_db.s2s(pubKey);
    String  pri = f_db.s2s(privKey);
    sql = "INSERT INTO keys (usr,publickey,privatekey) VALUES(" + un + "," + pub + "," + pri +")";
    int a = f_db.ExecSql(sql);
    lastError = f_db.getLastError();
    return (a==1);
  }

  public boolean  delUser(String usrName)
  {
    if(usrName == null || usrName.length()<1) return false;
    String  sql;
    String  un  = f_db.s2s(usrName);
    sql = "DELETE FROM keys WHERE usr=" + un;
    int a = f_db.ExecSql(sql);
    lastError = f_db.getLastError();
    return (a==1);
  }

  public String getLastError() {
    return lastError;
  }

}

