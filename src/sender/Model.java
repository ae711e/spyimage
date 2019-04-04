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

public class Model
{

  /**
   * Отправить по адресу файл вложения
   * @param email       адрес получателя
   * @param fileAppend  файл вложения
   * @return
   */
  public boolean  sendMailTo(String email, String fileAppend)
  {
    MailSend msg = new MailSend();
    //
    String otv = msg.mailSend(email, "Picture for You", "Hello, World", fileAppend);
    return (otv != null);
  }

  /**
   * Взять список получателей из таблицы keys в виде массива строк
   * @return массив строк
   */
  Collection<String>  getUsers()
  {
    // получим список имен из БД
    ArrayList<String[]> ard = R.db.DlookupArray("SELECT usr FROM keys ORDER BY usr");
    ArrayList<String> list = new ArrayList<>();
    for (String[] r: ard) list.add(r[0]); // добавим имя в массив
    return  list;
  }

} // end of class
