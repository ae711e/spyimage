/*
 * Copyright (c) 2019. Eremin
 */
/*
  Модель отправителя изображения
 */
package sender;

import ae.MailSend;
import ae.R;

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
    MailSend msg = new MailSend();
    //
    String otv = msg.mailSend(email, "Picture for You", "Hello, World", fileAppend);
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


} // end of class
