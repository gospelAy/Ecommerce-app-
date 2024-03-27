package ecommerce.ecommerce.service;

import ecommerce.ecommerce.dto.request.EmailNotificationRequest;
import ecommerce.ecommerce.dto.request.PasswordResetRequest;
import ecommerce.ecommerce.dto.request.Recipient;
import ecommerce.ecommerce.dto.request.Sender;
import ecommerce.ecommerce.exceptions.CustomerException;
import ecommerce.ecommerce.exceptions.InvalidOTPException;
import ecommerce.ecommerce.exceptions.UserNotFoundException;
import ecommerce.ecommerce.models.Customer;
import ecommerce.ecommerce.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

import static ecommerce.ecommerce.utils.AppUtils.APP_EMAIL;
import static ecommerce.ecommerce.utils.AppUtils.APP_NAME;

@Service
@Slf4j
@AllArgsConstructor
public class PasswordResetServiceImp implements PasswordResetService{

    private final CustomerRepository userRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void sendOTP(String email) {
        Optional<Customer> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found");
        }
        Customer foundCustomer = user.get();
        String otp = generateRandomOTP();
        foundCustomer.setOtp(otp);
        System.out.println(otp);
        userRepository.save(foundCustomer);
        String emailBody = "Dear " + foundCustomer.getEmail() + ",\n\n" +
                "Your OTP for password reset is: " + otp + "\n\n" +
                "Please use this OTP to verify your identity.";
        EmailNotificationRequest emailNotificationRequest = new EmailNotificationRequest();
        Sender sender = new Sender(APP_NAME, APP_EMAIL);
        emailNotificationRequest.setEmailSender(sender);
        Recipient recipient = new Recipient(email);
        emailNotificationRequest.setRecipients(Set.of(recipient));
        emailNotificationRequest.setSubject("password Reset OTP");
        emailNotificationRequest.setContent(emailBody);
        mailService.sendMail(emailNotificationRequest);
    }

    @Override
    public void verifyOTP(String otp) {
        Optional<Customer> user = userRepository.findByOtp(otp);
        if (user.isEmpty()) {
            throw new InvalidOTPException("Invalid OTP");
        }
    }

    @Override
    public void changePassword(String email, PasswordResetRequest passwordResetRequest) {
        Optional<Customer> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            Customer existingUser = user.get();
            String hashedPassword = passwordEncoder.encode(passwordResetRequest.getNewPassword());
            existingUser.setPassword(hashedPassword);
            String hashedConfirmPassword = passwordEncoder.encode(passwordResetRequest.getConfirmPassword());
            existingUser.setConfirmPassword(hashedConfirmPassword);
            if (!passwordResetRequest.getNewPassword().equals(passwordResetRequest.getConfirmPassword())) {
                throw new CustomerException("Confirm password does not match with the password. Please check and try again.");
            }
            userRepository.save(existingUser);
            existingUser.setOtp(null);
        } else {
            throw new UserNotFoundException("User not found");
        }
    }


    private String generateRandomOTP() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            otp.append(Math.round(Math.random() * 9));
        }
        return otp.toString();
    }
}