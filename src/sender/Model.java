/*
 * Copyright (c) 2019. Eremin
 */
/*
  Модель отправителя изображения
 */
package sender;

import ae.MailSend;

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

}
