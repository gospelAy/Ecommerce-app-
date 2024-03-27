package ecommerce.ecommerce.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import ecommerce.ecommerce.models.Customer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static java.time.Instant.now;

public class AppUtils {
    public static final String ID = "id";
    public static final String SENDER = "sender";
    public static final String APP_NAME="Ecommerce app";
    public static final String EMPTY_SPACE_VALUE=" ";
    public static final String APP_EMAIL="gosple531@gmail.com";
    public static final String ACTIVATION_LINK_VALUE="Activation Link";
    public static final String FRONTEND_BASE_URL = "https://www.Ecommerce.com";
    public static final String TEMPLATE_LOCATION = "C:\\Users\\ADMIN\\Downloads\\ecommerce\\src\\main\\resources\\static\\activation.txt";
    public static final String TO = "to";
    public static final String SUBJECT="subject";
    public static final String CUSTOMER_API_VALUE = "/api/Ecommerce/customer";
    public static final String SEND_OTP_ENDPOINT = "/api/password-reset/send-otp/**";
    public static final String VERIFY_OTP = "/api/password-reset/verify-otp/**";
    public static final String CHANGE_PASSWORD = "/api/password-reset/change-password/**";
    public static final String CUSTOMER_REGISTRATION_API_VALUE = "/api/Ecommerce/customer/registration";
    public static final String LOGIN_ENDPOINT = "/api/v1/login";
    public static final String TOKEN_PREFIX="Bearer ";
    public static  final String HTML_CONTENT_VALUE = "htmlContent";
    public static final String CLAIMS_VALUE = "ROLES";
    public static final String CLAIM_VALUE = "claim";
    public static final String ACCESS_TOKEN_VALUE = "access_token";

    public static final String UPDATE_CUSTOMER_ENDPOINT= CUSTOMER_API_VALUE+"?*+";

    public static String generateToken(Customer customer, String secret){
        return JWT.create()
                .withIssuedAt(Date.from(now()))
                .withExpiresAt(Date.from(now().plusSeconds(200L)))
                .withClaim(ID, customer.getId())
                .sign(Algorithm.HMAC512(secret.
                        getBytes()));
    }

    public static List<String> getAuthWhiteList(){
        return List.of(
                CUSTOMER_API_VALUE, LOGIN_ENDPOINT, CUSTOMER_REGISTRATION_API_VALUE,
                SEND_OTP_ENDPOINT, VERIFY_OTP, CHANGE_PASSWORD
        );
    }
}
