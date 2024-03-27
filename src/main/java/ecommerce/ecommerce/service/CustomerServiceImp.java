package ecommerce.ecommerce.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import ecommerce.ecommerce.dto.request.CustomerRegistrationRequest;
import ecommerce.ecommerce.dto.request.EmailNotificationRequest;
import ecommerce.ecommerce.dto.request.Recipient;
import ecommerce.ecommerce.dto.request.Sender;
import ecommerce.ecommerce.dto.response.ApiResponse;
import ecommerce.ecommerce.dto.response.CustomerRegistrationResponse;
import ecommerce.ecommerce.exceptions.CustomerException;
import ecommerce.ecommerce.exceptions.UserNotFoundException;
import ecommerce.ecommerce.models.Customer;
import ecommerce.ecommerce.repositories.CustomerRepository;
import ecommerce.ecommerce.utils.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static ecommerce.ecommerce.models.Role.CUSTOMER;
import static ecommerce.ecommerce.utils.AppUtils.*;
import static ecommerce.ecommerce.utils.ExceptionUtil.*;
import static ecommerce.ecommerce.utils.ResponseUtils.ACCOUNT_VERIFIED_SUCCESSFULLY;
import static ecommerce.ecommerce.utils.ResponseUtils.USER_REGISTRATION_SUCCESSFUL;

@Service
@AllArgsConstructor
@Slf4j
public class CustomerServiceImp implements CustomerService{

    private final CustomerRepository customerRepository;

    private final MailService mailService;

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CustomerRegistrationResponse register(CustomerRegistrationRequest customerRegistrationRequest) throws CustomerException {
        Customer customer = new Customer();
        customer.setEmail(customerRegistrationRequest.getEmail());
        String hashedPassword = passwordEncoder.encode(customerRegistrationRequest.getPassword());
        customer.setPassword(hashedPassword);
        String hashedConfirmPassword = passwordEncoder.encode(customerRegistrationRequest.getConfirmPassword());
        customer.setConfirmPassword(hashedConfirmPassword);
        if (!passwordEncoder.matches(customerRegistrationRequest.getPassword(), customer.getConfirmPassword())) {
            throw new CustomerException("Confirm password does not match with the password. Please check and try again.");
        }
        customer.setRoles(new HashSet<>());
        customer.getRoles().add(CUSTOMER);
        Customer savedCustomer = customerRepository.save(customer);
        EmailNotificationRequest emailNotificationRequest = buildEmailRequest(savedCustomer);
        mailService.sendMail(emailNotificationRequest);
        return buildRegisterCustomerResponse(savedCustomer.getId());
    }

    @Override
    public ApiResponse<?> verifyCustomer(String token) throws CustomerException {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(jwtUtil.getSecret().getBytes()))
                .build().verify(token);
        if (decodedJWT==null) throw new CustomerException(ACCOUNT_VERIFICATION_FAILED);
        Claim claim = decodedJWT.getClaim(ID);
        Long id = claim.asLong();
        Customer foundCustomer = customerRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException(
                        String.format(USER_WITH_ID_NOT_FOUND, id)));
        foundCustomer.setIsEnabled(true);
        customerRepository.save(foundCustomer);
        return ApiResponse.builder()
                .message(ACCOUNT_VERIFIED_SUCCESSFULLY)
                .build();
    }

    private EmailNotificationRequest buildEmailRequest(Customer customer) throws CustomerException {
        String token = generateToken(customer, jwtUtil.getSecret());
        EmailNotificationRequest request = new EmailNotificationRequest();
        Sender sender = new Sender(APP_NAME, APP_EMAIL);
        Recipient recipient = new Recipient(customer.getEmail());
        request.setEmailSender(sender);
        request.setRecipients(Set.of(recipient));
        request.setSubject(ACTIVATION_LINK_VALUE);
        String template = getEmailTemplate();
        request.setContent(String.format(template, FRONTEND_BASE_URL+"/customer/verify?token="+token));
        return request;
    }

    private String getEmailTemplate() throws CustomerException {
        try(BufferedReader reader =
                    new BufferedReader(new FileReader(TEMPLATE_LOCATION))){
            return  reader.lines().collect(Collectors.joining());
        }catch (IOException exception){
            throw new CustomerException(FAILED_TO_GET_ACTIVATION_LINK);
        }
    }

    private static CustomerRegistrationResponse buildRegisterCustomerResponse(Long customerId) {
        CustomerRegistrationResponse customerRegistrationResponse = new CustomerRegistrationResponse();
        customerRegistrationResponse.setMessage(USER_REGISTRATION_SUCCESSFUL);
        customerRegistrationResponse.setId(customerId);
        return customerRegistrationResponse;
    }
}
