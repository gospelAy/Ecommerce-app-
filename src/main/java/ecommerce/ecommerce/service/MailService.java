package ecommerce.ecommerce.service;

import ecommerce.ecommerce.dto.request.EmailNotificationRequest;

public interface MailService {
    void sendMail(EmailNotificationRequest emailNotificationRequest);
}
