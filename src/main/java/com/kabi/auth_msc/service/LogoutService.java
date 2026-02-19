package com.kabi.auth_msc.service;

import com.kabi.auth_msc.model.ClearCookies;
import com.kabi.auth_msc.repository.RefreshTokenRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class LogoutService {
    public final RefreshTokenRepository refreshTokenRepository;

    LogoutService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void logout(
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Optional<String> refreshToken =
                Optional.ofNullable(httpServletRequest.getCookies()).stream()
                        .flatMap(Arrays::stream)
                        .filter(cookie -> "rf_token".equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst();
        refreshToken.ifPresent(refreshTokenRepository::deleteByRefreshToken);

        ClearCookies.clearAuthCookies(httpServletResponse);

        return;
    }
}
