package com.kabi.auth_msc.service.jwt;

import com.kabi.auth_msc.exception.customExceptions.CustomUserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.BadCredentialsException;

import java.util.UUID;

import javax.security.auth.login.CredentialExpiredException;

public interface RefreshTokenService {
    public String generateRefreshToken(UUID id) throws CustomUserNotFoundException;

    public String generateAccessToken(HttpServletRequest httpServletRequest)
            throws CredentialExpiredException, BadCredentialsException, CustomUserNotFoundException;
}
