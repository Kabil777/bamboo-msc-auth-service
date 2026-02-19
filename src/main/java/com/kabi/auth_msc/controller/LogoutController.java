package com.kabi.auth_msc.controller;

import com.kabi.auth_msc.service.LogoutService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class LogoutController {
    private final LogoutService logoutService;

    LogoutController(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        logoutService.logout(httpServletRequest, httpServletResponse);
        return ResponseEntity.ok().build();
    }
}
