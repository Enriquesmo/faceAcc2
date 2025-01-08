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
            // Crear el mensaje
            MimeMessage mensaje = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, "UTF-8");

            // Configuración del mensaje
            helper.setFrom("miriamltn12@gmail.com");  
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpo, true);  // El segundo parámetro indica si el cuerpo es HTML

            // Enviar el correo
            javaMailSender.send(mensaje);
            System.out.println("Correo enviado correctamente.");
        } catch (MailException e) {
            System.out.println("Error durante el envío del correo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void sendEmail(String to, String subject, String body) {
        // Crear un mensaje de correo simple
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);           // Destinatario
        message.setSubject(subject); // Asunto
        message.setText(body);       // Cuerpo

        // Enviar el correo
        try {
            javaMailSender.send(message);
            System.out.println("Correo enviado a: " + to);
        } catch (Exception e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}