package com.example.donationsjava.controllers;

import com.example.donationsjava.EmailBuilderDto;
import com.example.donationsjava.EmailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

@RestController
public class EmailController {
    @PostMapping("/sendemail")
    public ResponseEntity<EmailResponse> sendEmail(@RequestBody EmailBuilderDto dto) throws AddressException, MessagingException, IOException{
        EmailResponse er = new EmailResponse();
        boolean success = sendmail(dto);
        System.out.println(success);
        er.setStatus(success);
        if(success) {
            er.setMessage("Email sent successfully");
            return ResponseEntity.ok(er);
        }
        er.setMessage("This request cannot be completed at this time");
        return ResponseEntity.ok(er);
    }

    private boolean sendmail(EmailBuilderDto dto) throws AddressException, MessagingException, IOException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("testemail@gmail.com", "password");
            }
        });
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("testemail@gmail.com", false));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(dto.getEmail()));
            msg.setSubject("Complete Donation");
            msg.setContent("<p>Hello "+dto.getName()+",</p>\n" +
                    "        <br/>\n" +
                    "        <p>Thanks for choosing to donate!</p>\n" +
                    "        <br/>\n" +
                    "        <p>To complete your donation, please click the paystack url below:</p>\n" +
                    "        <br/>\n" +
                    "        <p>www.paystack.com</p>\n" +
                    "        <br/>\n" +
                    "        <br/>\n" +
                    "        <p>Best Regards,</p>\n" +
                    "        <p>Donations team</p>", "text/html");
            msg.setSentDate(new Date());

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent("Complete Donation", "text/html");
            Transport.send(msg);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
}
