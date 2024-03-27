package ecommerce.ecommerce.controllers;

import ecommerce.ecommerce.dto.request.CustomerRegistrationRequest;
import ecommerce.ecommerce.dto.request.LoginRequest;
import ecommerce.ecommerce.dto.response.ApiResponse;
import ecommerce.ecommerce.dto.response.CustomerRegistrationResponse;
import ecommerce.ecommerce.exceptions.CustomerException;
import ecommerce.ecommerce.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/Ecommerce/customer")
@AllArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/registration")
    public ResponseEntity<CustomerRegistrationResponse> register(@RequestBody CustomerRegistrationRequest customerRegistrationRequest) {
        try {
            System.out.println("I DEY HERE");
            var response = customerService.register(customerRegistrationRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (CustomerException exception) {
            var response = new CustomerRegistrationResponse();
            response.setMessage(exception.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCustomer(@RequestHeader(value = "token") String token) {
        try {
            var response = customerService.verifyCustomer(token);
            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            ApiResponse<?> response = ApiResponse.builder()
                    .message(exception.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                        loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}