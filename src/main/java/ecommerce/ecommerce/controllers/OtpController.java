package ecommerce.ecommerce.controllers;

import ecommerce.ecommerce.dto.request.PasswordResetRequest;
import ecommerce.ecommerce.exceptions.InvalidOTPException;
import ecommerce.ecommerce.exceptions.UserNotFoundException;
import ecommerce.ecommerce.service.PasswordResetService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@AllArgsConstructor
@RequestMapping("/api/password-reset")
public class OtpController {
    private final PasswordResetService passwordResetService;

    @PostMapping("/send-otp/{email}")
    public ResponseEntity<Void> sendOTP(@PathVariable String email) throws UserNotFoundException {
        passwordResetService.sendOTP(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-otp/{otp}")
    public ResponseEntity<Void> verifyOTP(@PathVariable String otp) throws InvalidOTPException {
        passwordResetService.verifyOTP(otp);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestParam String email, @RequestBody PasswordResetRequest request) {
        try {
            passwordResetService.changePassword(email, request);
            return ResponseEntity.ok("Password changed successfully");
        } catch (InvalidOTPException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
