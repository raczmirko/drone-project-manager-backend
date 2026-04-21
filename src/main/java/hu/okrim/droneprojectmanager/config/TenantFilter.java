package hu.okrim.droneprojectmanager.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import hu.okrim.droneprojectmanager.security.SecurityConstants;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TenantFilter implements Filter {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Extract schema from JWT or request header
        String schema = extractSchemaFromRequest(httpRequest);

        if (schema != null) {
            // Dynamically set the search_path for this session
            jdbcTemplate.execute(String.format("SET search_path TO \"%s\"", schema));
        }

        // Continue the filter chain
        chain.doFilter(httpRequest, httpResponse);

        // Optionally reset the schema after the request
        jdbcTemplate.execute("SET search_path TO public");
    }

    private String extractSchemaFromRequest(HttpServletRequest request) {
        // Example: Extract schema from Authorization Bearer token (JWT)
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith(SecurityConstants.BEARER)) {
            String jwt = token.replace(SecurityConstants.BEARER, "").trim();
            return JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET_KEY))
                    .build()
                    .verify(jwt)
                    .getClaim("schema")
                    .asString();
        }
        return null;
    }
}
