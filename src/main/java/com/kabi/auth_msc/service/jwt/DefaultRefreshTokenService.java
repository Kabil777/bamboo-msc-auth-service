package com.kabi.auth_msc.service.jwt;

import com.kabi.auth_msc.entity.RefreshToken;
import com.kabi.auth_msc.entity.User;
import com.kabi.auth_msc.exception.customExceptions.CustomUserNotFoundException;
import com.kabi.auth_msc.repository.RefreshTokenRepository;
import com.kabi.auth_msc.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.security.auth.login.CredentialExpiredException;

@Service
@Slf4j
public class DefaultRefreshTokenService implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Autowired
    DefaultRefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public String generateRefreshToken(UUID id) throws CustomUserNotFoundException {
        String token = UUID.randomUUID().toString() + UUID.randomUUID();
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(() -> new CustomUserNotFoundException("Invalid credentials"));

        RefreshToken tokenEntity =
                refreshTokenRepository.findByUser(user).orElse(new RefreshToken());

        tokenEntity.setUser(user);
        tokenEntity.setRefreshToken(token);
        tokenEntity.setExpiry(Instant.now().plus(10, ChronoUnit.DAYS));

        refreshTokenRepository.save(tokenEntity);

        return tokenEntity.getRefreshToken();
    }

    private Optional<String> extractRefreshToken(HttpServletRequest request) {

        if (request.getCookies() == null) return Optional.empty();

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "rf_token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    public String generateAccessToken(HttpServletRequest httpServletRequest)
            throws BadCredentialsException,
                    CredentialExpiredException,
                    CustomUserNotFoundException {
        String refreshToken =
                extractRefreshToken(httpServletRequest)
                        .orElseThrow(() -> new BadCredentialsException("RefreshToken is missing"));

        RefreshToken token =
                refreshTokenRepository
                        .findByRefreshToken(refreshToken)
                        .orElseThrow(() -> new BadCredentialsException("RefreshToken is missing"));
        if (token.getExpiry().isBefore(Instant.now())) {
            throw new CredentialExpiredException("RefreshToken is Expired");
        }
        User user =
                userRepository
                        .findById(token.getUser().getId())
                        .orElseThrow(() -> new CustomUserNotFoundException("Invalid credentials"));

        Map<String, Object> claims =
                Map.of(
                        "id",
                        user.getId(),
                        "email",
                        user.getEmail(),
                        "name",
                        user.getName(),
                        "profile_url",
                        user.getPicture_url());

        return jwtService.generateToken(user.getName(), claims);
    }
}
