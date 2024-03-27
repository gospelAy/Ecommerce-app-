package ecommerce.ecommerce.dto.request;

import lombok.Data;

@Data
public class CustomerRegistrationRequest {
    private Long id;
    private String email;
    private String password;
    private String confirmPassword;
}
