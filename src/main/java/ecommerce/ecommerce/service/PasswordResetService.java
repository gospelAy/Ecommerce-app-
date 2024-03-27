package ecommerce.ecommerce.service;

import ecommerce.ecommerce.dto.request.PasswordResetRequest;

public interface PasswordResetService {

    void sendOTP(String email);

    void verifyOTP(String otp);

    void changePassword(String email, PasswordResetRequest request);

}