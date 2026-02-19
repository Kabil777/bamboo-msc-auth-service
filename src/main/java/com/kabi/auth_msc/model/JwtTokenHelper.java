package com.kabi.auth_msc.model;

public class JwtTokenHelper {
    private static final String TOKEN_PREFIX = "Bearer ";

    public static Boolean isVaildJwtToken(String token) {
        if (token.startsWith(TOKEN_PREFIX)) {
            return true;
        }
        return false;
    }

    public static String getJwt(String header) {
        return header.replace(TOKEN_PREFIX, "");
    }
}
