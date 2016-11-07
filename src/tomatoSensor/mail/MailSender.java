/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tomatoSensor.mail;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author 中村 勇吾
 */
public class MailSender {

    private String from, userName, password, smtp, to, msg, subject;

    private int port=-1;

    public MailSender() {
        
    }
    
    public void sendMail() throws MessagingException {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", smtp);
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.socketFactory.fallback", "false");
        prop.put("mail.smtp.port", port);
        prop.put("mail.smtp.auth", true);
        Session session = Session.getInstance(prop, new MyAuth(userName, password));
        Message mail = new MimeMessage(session);
        mail.setFrom(new InternetAddress(from));
        mail.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        mail.setSubject(subject);
        mail.setText(msg);
        mail.setSentDate(new Date());
        Transport.send(mail);
    }

    public void saveSetting() throws IOException{
        Properties settingpProperty = new Properties();
        settingpProperty.setProperty("sender", from);
        settingpProperty.setProperty("receiver", to);
        settingpProperty.setProperty("SMTPServerAdderss", smtp);
        settingpProperty.setProperty("SMTPServerPort", Integer.toString(port));
        settingpProperty.setProperty("userName", userName);
        settingpProperty.setProperty("userPassword", password);
        settingpProperty.store(new FileWriter("./settingData/mailSetting.properties"), "");
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStmp(String stmp) {
        this.smtp = stmp;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
}

class MyAuth extends Authenticator {

    private String userName, password;

    MyAuth(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password);
    }
}
