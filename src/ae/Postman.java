/*
 * Copyright (c) 2019. Еремин
 *
 */

/*
  Обработка почты
 */

package ae;

import javax.mail.*;
import javax.mail.internet.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

public class Postman
{

  public static boolean mailSend(String emailTo, String subject, String txtmsg, String fileAttachment)
  {
    final String username = R.SmtpUser;
    final String password = R.SmtpPwd;
    Locale.setDefault(new Locale("ru", "RU"));
    //
    Properties prop = System.getProperties();
    prop.put("mail.smtp.host", R.SmtpServer);
    prop.put("mail.smtp.port", R.SmtpPort);
    Authenticator authenticator = null;
    if(username != null && username.length()>0) {
      // наличие пароля предполагает SSL протокол отправки smtp
      prop.put("mail.smtp.auth", "true");
      prop.put("mail.smtp.socketFactory.port", R.SmtpPort);
      prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
      authenticator = new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(username, password);
        }
      };
    }
    // делаем сессию для передачи сообщения
    Session session = Session.getInstance(prop, authenticator);
    try {
      //
      MimeMessage msg = new MimeMessage(session);
//      msg.addHeader("Content-type", "text/plain; charset=\"UTF-8\"");
//      msg.addHeader("format", "flowed");
//      msg.addHeader("Content-Transfer-Encoding", "8bit");
      msg.setFrom(new InternetAddress(R.Email));
      InternetAddress[] address = {new InternetAddress(emailTo)};
      msg.setRecipients(Message.RecipientType.TO, address);
      msg.setSubject(subject, StandardCharsets.UTF_8.name());
      //msg.setText(txtmsg);
      msg.setSentDate(new Date());  // дата отправки
      // @see https://www.journaldev.com/2532/javamail-example-send-mail-in-java-smtp#javamail-example-8211-send-mail-in-java-with-attachment
      // Create a multipart message, возможно будет вложение
      Multipart multipart = new MimeMultipart();
      // Create the message body part
      // создадим 1 часть с текстом
      BodyPart messageBodyPart = new MimeBodyPart();
      // Fill the message
      messageBodyPart.setText(txtmsg);
      // Set text message part, добавим часть в составное тело сообщения
      multipart.addBodyPart(messageBodyPart);
      // если задано имя файла вложения, то добавим еще одну часть к письму
      if(fileAttachment != null) {
        // Second part is attachment
        MimeBodyPart fileBodyPart = new MimeBodyPart();
        // этого достаточно (по примеру javamail-samples\sendfile.java)
        fileBodyPart.attachFile(fileAttachment);
        // избыточный код из примера
        // @see https://www.journaldev.com/2532/javamail-example-send-mail-in-java-smtp#javamail-example-8211-send-mail-in-java-with-attachment
        //        DataSource source = new FileDataSource(fileAttachment);
        //        fileBodyPart.setDataHandler(new DataHandler(source));
        //        File f = new File(fileAttachment);
        //        fileBodyPart.setFileName(f.getName()); // только имя файла без пути
        multipart.addBodyPart(fileBodyPart);
      }
      // Send the complete message parts
      msg.setContent(multipart);
      // отправка сообщения
      Transport.send(msg);
    } catch (Exception e) {
      //e.printStackTrace();
      System.err.println("?-Error-mailSend(): " + e.getMessage());
      return false;
    }
    return true;
  }

  /**
   * Получить хранилище почтового сервера и подключиться к нему
   * @return почтовое хранилище
   */
  public static Store openStore()
  {
    final String username = R.PostUser;
    final String password = R.PostPwd;
    Store store;
    //Authenticator auth = null;
    // Свойства установки
    Properties props = System.getProperties();
    //
    props.put("mail.debug", "false");
    String  host  = R.PostServer;
    String  proto = R.PostProtocol;
    // если протокол не то, не се, то сделаем imap
    if(proto.compareTo("imap")!=0 && proto.compareTo("pop3")!=0) {
      R.PostProtocol = proto = "imap";
    }
    props.put("mail.store.protocol" , proto);
    props.put("mail." + proto + ".host",       R.PostServer);
    props.put("mail." + proto + ".port"      , R.PostPort);
    props.put("mail." + proto + ".ssl.enable", R.PostSSL);
    // https://www.mkyong.com/java/javamail-api-sending-email-via-gmail-smtp-example/
    props.put("mail." + proto + ".auth", "true"); // !!!
    // Настроить аутентификацию, получить session
    Authenticator auth = new Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    };
    //
    Session session = Session.getDefaultInstance(props, auth);
    try {
      // Получить store
      store = session.getStore(proto);
      // Подключение к почтовому серверу
      store.connect(host, username, password);
      //store.connect();
    } catch(Exception e){
      System.err.println("?Error-нет подключения к почтовому серверу " + e.getMessage());
      return null;
    }
    return store;
  }

} // end of class
