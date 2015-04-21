package hu.uniobuda.nik.parentalcontrol;


import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
    final String emailPort = "587";// gmail's smtp port
    final String smtpAuth = "true";
    final String starttls = "true";
    final String emailHost = "smtp.gmail.com";

    String fromEmail;
    String fromPassword;
    String toEmail;
    String emailSubject;
    String emailBody;

    Properties emailProperties;
    Session mailSession;
    MimeMessage emailMessage;

    public EmailSender(String fromEmail, String fromPassword, String toEmail,
                       String emailSubject, String emailBody) {
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        this.toEmail = toEmail;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", smtpAuth);
        emailProperties.put("mail.smtp.starttls.enable", starttls);
        Log.i("GMail", "Mail server properties set.");
    }

    public MimeMessage createEmailMessage() throws AddressException,
            MessagingException, UnsupportedEncodingException {

        mailSession = Session.getDefaultInstance(emailProperties, null);
        emailMessage = new MimeMessage(mailSession);

        emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail));
        emailMessage.addRecipient(Message.RecipientType.TO,new InternetAddress(toEmail));
        emailMessage.setSubject(emailSubject);
        emailMessage.setContent(emailBody, "text/html");// for a html email
        // emailMessage.setText(emailBody);// for a text email
        Log.i("GMail", "Email Message created.");
        return emailMessage;
    }

    public void sendEmail()  {
        new Thread() {
            @Override
            public void run() {
                mailSession = Session.getDefaultInstance(emailProperties, null);
                emailMessage = new MimeMessage(mailSession);

                try {
                    emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail));
                    emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
                    emailMessage.setSubject(emailSubject);
                    emailMessage.setContent(emailBody, "text/html");
                    Transport transport = mailSession.getTransport("smtp");
                    transport.connect(emailHost, fromEmail, fromPassword);
                    transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
                    transport.close();
                } catch (MessagingException e) {

                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.i("GMail", "Email sent successfully.");
            }
        }.start();
    }

}
