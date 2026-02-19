package com.kabi.auth_msc.handlers;

import com.kabi.auth_msc.entity.CustomUserDetails;
import com.kabi.auth_msc.service.jwt.JwtService;
import com.kabi.auth_msc.service.jwt.RefreshTokenService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public Oauth2SuccessHandler(
            @Qualifier("JwtServiceImplementation") JwtService jwtService,
            @Qualifier("defaultRefreshTokenService") RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();

        claims.put("id", userDetails.getId());
        claims.put("email", userDetails.getEmail());
        claims.put("name", userDetails.getName());
        claims.put("profile_url", userDetails.getPictureUrl());

        String accessToken = jwtService.generateToken(userDetails.getName(), claims);
        String refreshToken = refreshTokenService.generateRefreshToken(userDetails.getId());

        ResponseCookie accessCookie =
                ResponseCookie.from("ac_token", accessToken)
                        .httpOnly(true)
                        .maxAge(Duration.ofMinutes(10))
                        .secure(false)
                        .domain("localhost")
                        .sameSite("Lax")
                        .path("/")
                        .build();
        ResponseCookie responseCookie =
                ResponseCookie.from("rf_token", refreshToken)
                        .httpOnly(true)
                        .maxAge(Duration.ofDays(10))
                        .domain("localhost")
                        .secure(false)
                        .sameSite("Lax")
                        .path("/")
                        .build();

        response.addHeader("Set-Cookie", responseCookie.toString());
        response.addHeader("Set-Cookie", accessCookie.toString());

        String mode = (String) request.getSession().getAttribute("OAUTH_MODE");
        boolean isNewUser = userDetails.isNewUser();
        if ("signup".equals(mode) && isNewUser) {
            response.sendRedirect("http://localhost:3000/setprofile");
        } else {
            response.sendRedirect("http://localhost:3000/");
        }
    }
}
