package hu.okrim.droneprojectmanager.security.manager;

import hu.okrim.droneprojectmanager.model.User;
import hu.okrim.droneprojectmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    private UserService userServiceImpl;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // Attempt to retrieve user from database
        User user = userServiceImpl.findByAccountNumber(Long.valueOf(authentication.getName()))
                .orElseThrow(() -> new BadCredentialsException("Invalid account number"));

        // Validate password
        if (!bCryptPasswordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
            throw new BadCredentialsException("You provided an incorrect password.");
        }

        // If valid, return authentication token
        return new UsernamePasswordAuthenticationToken(authentication.getName(), user.getPassword());
    }
}
