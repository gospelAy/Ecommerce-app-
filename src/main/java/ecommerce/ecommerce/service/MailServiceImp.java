package ecommerce.ecommerce.service;

import ecommerce.ecommerce.dto.request.EmailNotificationRequest;
import ecommerce.ecommerce.dto.request.Recipient;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Set;

public class MailServiceImp implements MailService{

    private final JavaMailSender mailSender;

    @Autowired
    public MailServiceImp(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendMail(EmailNotificationRequest emailNotificationRequest) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(emailNotificationRequest.getEmailSender().getEmail());
            helper.setTo(getRecipientEmails(emailNotificationRequest.getRecipients()));
            helper.setSubject(emailNotificationRequest.getSubject());
            helper.setText(emailNotificationRequest.getContent(), true);
            mailSender.send(mimeMessage);
        } catch (MailException | MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }
    private String[] getRecipientEmails(Set<Recipient> recipients) {
        return recipients.stream()
                .map(Recipient::getEmail)
                .toArray(String[]::new);
    }
}
