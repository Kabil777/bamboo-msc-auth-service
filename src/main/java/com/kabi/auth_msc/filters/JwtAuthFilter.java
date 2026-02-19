package com.kabi.auth_msc.filters;

import com.kabi.auth_msc.entity.User;
import com.kabi.auth_msc.model.ErrorResponseGenerator;
import com.kabi.auth_msc.model.JwtTokenHelper;
import com.kabi.auth_msc.service.jwt.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(@Qualifier("JwtServiceImplementation") JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.startsWith("/api/v1/auth/")
                || path.startsWith("/oauth2/")
                || path.startsWith("/login/oauth2/")
                || path.startsWith("/.well-known/")
                || path.startsWith("/actuator/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String jwt = JwtTokenHelper.getJwt(header);
        try {

            Map<String, Object> claims = jwtService.extractClaims(jwt);

            if (jwtService.isTokenExpired((Instant) claims.get("exp"))) {
                ErrorResponseGenerator.writeError(
                        response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "accessTokenExpired",
                        "Access Token expired",
                        Map.of(
                                "reason", "JWT expiration",
                                "action", "refresh_token"));
                return;
            }

            User user = jwtService.isValidUser((String) claims.get("email"));

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            user, null, List.of(new SimpleGrantedAuthority(user.getRole())));

            usernamePasswordAuthenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext()
                    .setAuthentication(usernamePasswordAuthenticationToken);

            // response.setHeader("X-Auth-User", user.getName());
            // response.setHeader("X-Auth-Email", user.getEmail());
            // response.setHeader("X-Auth-Role", user.getRole());

            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException ex) {
            ErrorResponseGenerator.writeError(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "invalidAccessToken",
                    "Invalid Access Token",
                    Map.of("exception", ex.getClass().getSimpleName()));
        }
    }
}
