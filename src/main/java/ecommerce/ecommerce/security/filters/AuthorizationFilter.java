package ecommerce.ecommerce.security.filters;

import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.ecommerce.exceptions.CustomerException;
import ecommerce.ecommerce.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ecommerce.ecommerce.utils.AppUtils.*;
import static ecommerce.ecommerce.utils.ExceptionUtil.ERROR_VALUE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        System.out.println("URL:>> " + request.getServletPath());
        boolean isPathInAuthWhitelist = getAuthWhiteList().contains(request.getServletPath()) &&
                request.getMethod().equals(HttpMethod.POST.name());
        System.out.println(request.getMethod());
        System.out.println(isPathInAuthWhitelist);
        if (isPathInAuthWhitelist)
            filterChain.doFilter(request,response);
        else authorizeRequest(request, response, filterChain);
    }

    private void authorizeRequest(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws IOException, ServletException {
        authorize(request, response);
        filterChain.doFilter(request, response);
    }


    private void authorize(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        System.out.println("AUTHORIZATION::>> "+ authorizationHeader);
        boolean isValidAuthorizationHeader = authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX);
        if(isValidAuthorizationHeader) {
            try {
                String token=parseTokenFrom(authorizationHeader);
                authorize(token);
            }catch (Exception exception){
                Map<String, String> errors = new HashMap<>();
                errors.put(ERROR_VALUE, exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                mapper.writeValue(response.getOutputStream(), errors);

            }
        }
    }

    private String parseTokenFrom(String authorizationHeader) {
        return authorizationHeader.substring(TOKEN_PREFIX.length());
    }

    private void authorize(String token){
        try {
            Map<String, Claim> map = jwtUtil.extractClaimsFrom(token);
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            Claim claim = map.get(CLAIMS_VALUE);
            addClaimToUserAuthorities(authorities, claim);
            Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (CustomerException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addClaimToUserAuthorities(List<SimpleGrantedAuthority> authorities, Claim claim) {
        String role = claim.asMap().get(CLAIM_VALUE).toString();
        authorities.add(new SimpleGrantedAuthority(role));
    }
}