package com.kabi.auth_msc.exception;

import com.kabi.auth_msc.exception.customExceptions.CustomUserNotFoundException;
import com.kabi.auth_msc.model.ClearCookies;
import com.kabi.auth_msc.model.ErrorResponseGenerator;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

import javax.security.auth.login.CredentialExpiredException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomUserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(
            CustomUserNotFoundException customUserNotFoundException) {

        return ErrorResponseGenerator.of(
                HttpStatus.UNAUTHORIZED,
                "customUserNotFoundError",
                customUserNotFoundException.getMessage(),
                Map.of());
    }

    @ExceptionHandler(CredentialExpiredException.class)
    public ResponseEntity<?> handleExpired(CredentialExpiredException credentialExpiredException) {
        return ErrorResponseGenerator.of(
                HttpStatus.UNAUTHORIZED,
                "tokenExpiredError",
                "Token Expired",
                Map.of("action", "refresh_token"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<?> handleBadCreds(
            BadCredentialsException badCredentialsException, HttpServletResponse response) {
        ClearCookies.clearAuthCookies(response);
        return ErrorResponseGenerator.of(
                HttpStatus.UNAUTHORIZED,
                "invalidCredentialsError",
                "Invalid credentials",
                Map.of("msg", badCredentialsException.getMessage()));
    }
}
