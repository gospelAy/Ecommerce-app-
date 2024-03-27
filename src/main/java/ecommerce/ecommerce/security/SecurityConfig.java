package ecommerce.ecommerce.security;

import ecommerce.ecommerce.security.filters.AuthenticationFilter;
import ecommerce.ecommerce.security.filters.AuthorizationFilter;
import ecommerce.ecommerce.utils.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static ecommerce.ecommerce.utils.AppUtils.*;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        UsernamePasswordAuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager, jwtUtil);
        authenticationFilter.setFilterProcessesUrl(LOGIN_ENDPOINT);
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(c->c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new AuthorizationFilter(jwtUtil), AuthenticationFilter.class)
                .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(c->c.requestMatchers(POST, CUSTOMER_API_VALUE)
                        .permitAll())
                .authorizeHttpRequests(c->c.requestMatchers(POST, LOGIN_ENDPOINT)
                        .permitAll())
                .authorizeHttpRequests(c->c.requestMatchers(POST, CUSTOMER_REGISTRATION_API_VALUE)
                        .permitAll())
                .authorizeHttpRequests(c->c.requestMatchers(POST, SEND_OTP_ENDPOINT)
                        .permitAll())
                .authorizeHttpRequests(c->c.requestMatchers(POST, VERIFY_OTP)
                        .permitAll()).
                authorizeHttpRequests(c->c.requestMatchers(PUT, CHANGE_PASSWORD)
                        .permitAll())
//                .authorizeHttpRequests(c->c.requestMatchers(PATCH, UPDATE_CUSTOMER_ENDPOINT)
//                        .hasRole(CUSTOMER.name()))
                .authorizeHttpRequests(c->c.anyRequest().authenticated())
                .build();
    }
}
