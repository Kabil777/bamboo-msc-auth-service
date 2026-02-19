package com.kabi.auth_msc.controller;

import com.kabi.auth_msc.service.jwt.RefreshTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class RefreshTokenController {
    private final RefreshTokenService refreshTokenService;

    RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        String accessToken = refreshTokenService.generateAccessToken(request);
        ResponseCookie accessCookie =
                ResponseCookie.from("ac_token", accessToken)
                        .httpOnly(true)
                        .secure(false)
                        .sameSite("Lax")
                        .domain("localhost")
                        .path("/")
                        .maxAge(Duration.ofMinutes(15))
                        .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

        return ResponseEntity.noContent().build();
    }
}
