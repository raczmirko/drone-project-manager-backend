package hu.okrim.droneprojectmanager.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.okrim.droneprojectmanager.dto.UserDto;
import hu.okrim.droneprojectmanager.security.SecurityConstants;
import hu.okrim.droneprojectmanager.security.manager.CustomAuthenticationManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

@AllArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private CustomAuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            // Parse the request body
            UserDto userDto = new ObjectMapper().readValue(request.getInputStream(), UserDto.class);

            // Create a UsernamePasswordAuthenticationToken for authentication
            return new UsernamePasswordAuthenticationToken(
                    userDto.getAccountNumber(), // Pass accountNumber as principal
                    userDto.getPassword());     // Pass password as credentials
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
        // Generate JWT Token
        String token = JWT.create()
                .withSubject(authResult.getPrincipal().toString()) // Use accountNumber as the subject
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION)) // Expiration time
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET_KEY)); // Use secret key for signing

        // Add token to response
        response.addHeader(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);
        response.addHeader("Access-Control-Expose-Headers", "Authorization"); // Allow frontend to access the Authorization header
    }


}
