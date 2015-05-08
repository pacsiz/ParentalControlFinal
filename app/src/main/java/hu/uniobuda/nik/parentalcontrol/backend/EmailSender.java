package hu.uniobuda.nik.parentalcontrol.backend;


import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
    final String port = "587";// gmail's smtp port
    final String authRequired = "true";
    final String starttls = "true";
    final String host = "smtp.gmail.com";

    String fromAddress;
    String fromPassword;
    String toAddress;
    String subject;
    String text;

    Properties emailProperties;
    Session mailSession;
    MimeMessage emailMessage;

    public EmailSender(String fromAddress, String fromPassword, String toAddress,
                       String subject, String text) {
        this.fromAddress = fromAddress;
        this.fromPassword = fromPassword;
        this.toAddress = toAddress;
        this.subject = subject;
        this.text = text;

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", port);
        emailProperties.put("mail.smtp.auth", authRequired);
        emailProperties.put("mail.smtp.starttls.enable", starttls);
    }

    public void sendEmail() {
        new Thread() {
            @Override
            public void run() {
                mailSession = Session.getDefaultInstance(emailProperties, null);
                emailMessage = new MimeMessage(mailSession);

                try {
                    emailMessage.setFrom(new InternetAddress(fromAddress, fromAddress));
                    emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
                    emailMessage.setSubject(subject);
                    emailMessage.setContent(text, "text/html");
                    Transport transport = mailSession.getTransport("smtp");
                    transport.connect(host, fromAddress, fromPassword);
                    transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
                    transport.close();
                } catch (MessagingException | UnsupportedEncodingException e) {

                    e.printStackTrace();
                }
            }
        }.start();
    }

}
