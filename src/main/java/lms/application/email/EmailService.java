package lms.application.email;

import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Singleton service for sending email notifications via Gmail SMTP.
 *
 * <p>
 * This service provides email functionality for the Library Management System,
 * primarily used to send notifications about overdue items, due date reminders,
 * and other library-related communications to users.
 * </p>
 *
 * <p>
 * The service uses Gmail's SMTP server with TLS encryption for secure email delivery.
 * Credentials are loaded from environment variables via a .env file.
 * </p>
 *
 * <p>
 * Required environment variables:
 * </p>
 * <ul>
 * <li>{@code GMAIL_USERNAME} - The Gmail account email address</li>
 * <li>{@code GMAIL_APP_PASSWORD} - The Gmail app-specific password</li>
 * </ul>
 *
 * @author Ahmad Salameh
 * @version 1.0
 */
public class EmailService {

    private static final Logger logger = Logger.getLogger(EmailService.class.getName());
    private static EmailService instance;
    private final String username;
    private final String appPassword;

    /**
     * Private constructor that loads email credentials from environment variables.
     */
    private EmailService() {
        Dotenv dotenv = Dotenv.load();
        this.username = dotenv.get("GMAIL_USERNAME");
        this.appPassword = dotenv.get("GMAIL_APP_PASSWORD");
    }

    /**
     * Returns the singleton instance of the email service.
     *
     * @return the singleton EmailService instance
     */
    public static synchronized EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }

    /**
     * Sends an email to the specified recipient.
     *
     * <p>
     * This method configures an SMTP session with Gmail and sends a plain text email
     * message. If sending fails, an exception is logged and a RuntimeException is thrown.
     * </p>
     *
     * @param to the recipient's email address
     * @param subject the email subject line
     * @param body the email message body (plain text)
     * @throws RuntimeException if email sending fails
     */
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