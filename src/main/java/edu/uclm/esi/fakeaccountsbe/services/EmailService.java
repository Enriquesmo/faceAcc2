package edu.uclm.esi.fakeaccountsbe.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void enviarEmail(String destinatario, String asunto, String cuerpo) throws MessagingException {
        try {
            MimeMessage mensaje = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, "UTF-8");
            helper.setFrom("miriamltn12@gmail.com");  
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpo, true);
            javaMailSender.send(mensaje);
            System.out.println("Correo enviado correctamente.");
        } catch (MailException e) {
            System.out.println("Error durante el envío del correo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);          
        message.setSubject(subject); 
        message.setText(body);       
        try {
            javaMailSender.send(message);
            System.out.println("Correo enviado a: " + to);
        } catch (Exception e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}