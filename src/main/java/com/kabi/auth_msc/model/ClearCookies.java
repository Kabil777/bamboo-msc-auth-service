package com.kabi.auth_msc.model;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public class ClearCookies {

    private ClearCookies() {}

    public static void clearAuthCookies(HttpServletResponse response) {

        ResponseCookie clearAccess =
                ResponseCookie.from("ac_token", "")
                        .path("/")
                        .httpOnly(true)
                        .maxAge(0)
                        .sameSite("Lax")
                        .build();

        ResponseCookie clearRefresh =
                ResponseCookie.from("rf_token", "")
                        .path("/")
                        .httpOnly(true)
                        .maxAge(0)
                        .sameSite("Lax")
                        .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefresh.toString());
    }
}
