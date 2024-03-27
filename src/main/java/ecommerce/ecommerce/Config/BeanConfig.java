package ecommerce.ecommerce.Config;

import ecommerce.ecommerce.dto.request.EmailNotificationRequest;
import ecommerce.ecommerce.service.MailService;
import ecommerce.ecommerce.service.MailServiceImp;
import ecommerce.ecommerce.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class BeanConfig {

    @Value("${jwt.signing.secret}")
    private String jwt_secret;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(jwt_secret);
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public MailService mailService(){
        JavaMailSender sender = new JavaMailSenderImpl();
        return new MailServiceImp(sender);

    }
}

