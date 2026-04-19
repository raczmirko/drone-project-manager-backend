package hu.okrim.droneprojectmanager.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import hu.okrim.droneprojectmanager.security.SecurityConstants;
import hu.okrim.droneprojectmanager.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final UserService userService;

    /**
     * Extracts the user from the request header.
     */
    public String extractUserFromToken(HttpServletRequest request) {
        String header = request.getHeader(SecurityConstants.AUTHORIZATION);
        String returnUser = null;
        if (header != null && header.startsWith(SecurityConstants.BEARER)) {
            String token = header.replace(SecurityConstants.BEARER, "");
            returnUser = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET_KEY))
                    .build()
                    .verify(token)
                    .getSubject();
        }
        return returnUser;
    }
}
