package ecommerce.ecommerce.service;

import ecommerce.ecommerce.dto.request.CustomerRegistrationRequest;
import ecommerce.ecommerce.dto.response.ApiResponse;
import ecommerce.ecommerce.dto.response.CustomerRegistrationResponse;
import ecommerce.ecommerce.exceptions.CustomerException;

public interface CustomerService {
    CustomerRegistrationResponse register(CustomerRegistrationRequest customerRegistrationRequest) throws CustomerException;
    ApiResponse<?> verifyCustomer(String token) throws CustomerException;
}
