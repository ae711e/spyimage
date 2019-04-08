package keygenmy;

import ae.Database;
import ae.MyCrypto;
import ae.R;

import java.util.ArrayList;
import java.util.Collection;

/*
 Модель формирования собственного ключа
 */

public class Model {

  private Database  db;
  private String    public_key, private_key;  // ключи
  private String    lastError;

  Model()
  {
    db = R.db;
  }

  /**
   * Запомнить ключи шифрования
   * @param pubKey
   * @param privKey
   */
  void rememberKeys(String pubKey, String privKey)
  {
    public_key = pubKey;
    private_key = privKey;
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

  /**
   * Добавить ключи собственного пользователя в таблицу
   * @param usrName имя собственного пользователя
   * @param pubKey  публичный ключ
   * @param privKey приватный ключ
   * @return
   */
  boolean  addUser(String usrName, String pubKey, String privKey)
  {
    lastError = "Неправильные аргументы";
    if(usrName == null || pubKey == null || privKey == null) return false;
    if(usrName.length()<1 || pubKey.length()<1 || privKey.length()<1) return false;
    String  str = db.Dlookup("SELECT count(*) FROM keys WHERE mykey=1");
    if(Integer.parseInt(str) != 0) {
      lastError = "Собственный ключ уже есть";
      return false;
    }
    String  sql;
    String  un  = db.s2s(usrName);
    String  pub = db.s2s(pubKey);
    String  pri = db.s2s(privKey);
    // flag = 1 - собственный пользователь
    sql = "INSERT INTO keys (usr,publickey,privatekey,mykey) VALUES(" + un + "," + pub + "," + pri +",1)";
    int a = db.ExecSql(sql);
    lastError = db.getLastError();
    return (a==1);
  }

  boolean  delUser(String usrName)
  {
    if(usrName == null || usrName.length()<1) return false;
    String  sql;
    String  un  = db.s2s(usrName);
    sql = "DELETE FROM keys WHERE usr=" + un;
    int a = db.ExecSql(sql);
    lastError = db.getLastError();
    return (a==1);
  }

  public String getLastError() {
    return lastError;
  }

  /**
   * Взять список получателей из таблицы keys в виде коллекции
   * @return массив строк
   */
  Collection<String>  getUsrsKeys()
  {
    // получим список имен из БД
    ArrayList<String[]> ardb = db.DlookupArray("SELECT usr,mykey FROM keys WHERE (mykey) is null or mykey!=1 ORDER BY mykey,usr");
    ArrayList<String> collect = new ArrayList<>();
    for (String[] rst: ardb) {
      collect.add(rst[0]); // добавим имя в массив
    }
    return  collect;
  }

}

