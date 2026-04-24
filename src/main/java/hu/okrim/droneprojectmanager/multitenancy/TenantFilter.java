package hu.okrim.droneprojectmanager.multitenancy;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import hu.okrim.droneprojectmanager.security.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TenantFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String schema = extractSchemaFromRequest(httpRequest);

        try {
            TenantContext.setTenant(schema);
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
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
