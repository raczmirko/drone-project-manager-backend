package hu.okrim.droneprojectmanager.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.okrim.droneprojectmanager.dto.UserRequestDto;
import hu.okrim.droneprojectmanager.model.User;
import hu.okrim.droneprojectmanager.security.SecurityConstants;
import hu.okrim.droneprojectmanager.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService userService;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService) {
        super.setAuthenticationManager(authenticationManager);
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            // Parse the request body
            UserRequestDto userRequestDto = new ObjectMapper().readValue(request.getInputStream(), UserRequestDto.class);

            // Create an Authentication object
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userRequestDto.accountNumber(), // Principal (accountNumber)
                    userRequestDto.password());    // Credentials (password)

            // Delegate authentication to the AuthenticationManager (CustomAuthenticationManager)
            return this.getAuthenticationManager().authenticate(authentication);
        } catch (IOException e) {
            throw new RuntimeException("Invalid request payload", e);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(failed.getMessage());
        response.getWriter().flush();
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException
    {
        // Retrieve the authenticated user from the Authentication object
        User user = (User) authResult.getPrincipal();

        // Update last login timestamp
        userService.updateLastLogin(user);

        // Generate JWT Token
        String token = JWT.create()
                .withSubject(user.getAccountNumber().toString()) // Use accountNumber as the subject
                .withClaim("schema", user.getSchema().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION)) // Expiration time
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET_KEY)); // Use secret key for signing

        // Add token to response
        response.addHeader(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);
        response.addHeader("Access-Control-Expose-Headers", "Authorization"); // Allow frontend to access the Authorization header
    }


}
