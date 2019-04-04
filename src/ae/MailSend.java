/*
 * Copyright (c) 2017. Aleksey Eremin
 * 08.02.17 17:05
 */

package ae;

import org.apache.commons.mail.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Created by ae on 08.02.2017.
 * Отправка почты с помощью класса Apache Commons Email 1.4
 * (http://www.sources.ru/java/java_send_mail.shtml - первобытная отправка)
 * http://commons.apache.org/proper/commons-email/userguide.html
 * + возникла ошибка http://stackoverflow.com/questions/1630002/java-lang-noclassdeffounderror-javax-mail-authenticator-whats-wrong
 */
public class MailSend {
    private final String mail_from = R.SmtpSender;
    private final String smtp_server_adr = R.SmtpServer;
    private final int smtp_server_port = Integer.parseInt(R.SmtpServerPortSend);
    private final String smtp_server_user = R.SmtpServerUser; // имя пользовтаеля для регистрации на почтовом сервере
    private final String smtp_server_pwd = R.SmtpServerPwd;   // пароль для почтового сервера
    private final String addr_cc = R.SmtpMailCC;  // адрес копии

    public MailSend()
    {
        Locale.setDefault(new Locale("ru", "RU"));
    }

    /**
     * Отправка почтового сообщения на адрес SmtpMailTo и если указан SmtpMailСС
     * @param adrEmail              адрес получателя
     * @param subject               тема сообщения
     * @param message               сообщение
     * @param fileNameAttachment    имя файла вложения (может быть null)
     */
    public String mailSend(String adrEmail, String subject, String message, String fileNameAttachment)
    {
        LocalDateTime dt = LocalDateTime.now();
        String sDat = dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        String otv = null;
        // TODO убрать когда закончится отладка
        /// adrEmail = "par1812@ya.ru";
        //
        try {
            message = message + "\r\n" + sDat+ "\r\n"; // + sms (подпись для SMS-сервиса)
            //
            MultiPartEmail email = new MultiPartEmail();
            //  http://stackoverflow.com/questions/5779238/how-change-charset-in-apache-commons-email
            // установить кодировку письма UTF-8 чтобы были русские буквы из командной строки
            email.setCharset(org.apache.commons.mail.EmailConstants.UTF_8);
            //
            email.setHostName(smtp_server_adr);
            email.setSmtpPort(smtp_server_port);
            // если указано имя пользователя, значит защищенное соединение
            if(smtp_server_user != null) {
                email.setAuthenticator(new DefaultAuthenticator(smtp_server_user, smtp_server_pwd));
                email.setSSLOnConnect(true);
            }
            email.setFrom(mail_from);
            email.setSubject(subject);
            //email.setContent();
            email.setMsg(message);
            //
            // разбор строки адреса получателя на несколько адресов, разделенных ;
            String[] aato = adrEmail.replace(',',';').split(";");
            for(String s: aato) {
                email.addTo(s);
            }
            if(addr_cc != null) {
                String[] aacc = addr_cc.replace(',',';').split(";");
                for(String s: aacc) {
                    email.addBcc(s); // скрытая копия addCc(s);
                }
            }
            //
            // File fileAtt;
            // если указано имя файла, вложим его в письмо
            if(fileNameAttachment != null) {
                File fileAtt = new File(fileNameAttachment);
                if(fileAtt.exists()) {
                    email.attach(fileAtt);
                }
            }
            otv = email.send(); // ответ почтового сервера
            // System.out.println("Mail send: " + otv);
        } catch (EmailException e) {
            // e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
        //
        return otv;
    }
    
}
