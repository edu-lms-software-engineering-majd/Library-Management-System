package lms.application.email;

import java.util.Properties;
import java.util.logging.Logger;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {

    private static final Logger logger = Logger.getLogger(EmailService.class.getName());
    private static EmailService instance;
    private final String username;
    private final String appPassword;

    private EmailService() {
        Dotenv dotenv = Dotenv.load();
        this.username = dotenv.get("GMAIL_USERNAME");
        this.appPassword = dotenv.get("GMAIL_APP_PASSWORD");
    }

    public static synchronized EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }

    public void sendEmail(String to, String subject, String body) {

       
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            logger.info("Email sent successfully to " + to);

        } catch (MessagingException e) {
            logger.severe("Failed to send email to " + to + ": " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}