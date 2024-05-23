package org.smart.mvc.smartcontactmanager.service;


import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import java.util.Properties;

//import java.net.PasswordAuthentication;

@Service
public class EmailService {

    public boolean sendEmail(String subject, String message, String to) {

        boolean f = false;

        String from = "learningjava916@gmail.com";

        //variable for gmail
        String host = "smtp.gmail.com";

        //get the system properties
        Properties properties = System.getProperties();
        System.out.println("Properties : " + properties);

        //setting important information to properties object
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Use TLS

        //to get the session object
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("learningjava916@gmail.com", "");
            }
        });

        session.setDebug(true);

        //compose the message{text,multiMedia}
        MimeMessage m = new MimeMessage(session);

        try {
            //from email
            m.setFrom(from);

            //adding recipient to message
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            //adding subject to message
            m.setSubject(subject);

            //adding text to message
            m.setText(message);

            //send message using transport class
            Transport.send(m);

            System.out.println("Sent success..");

            f = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }
}