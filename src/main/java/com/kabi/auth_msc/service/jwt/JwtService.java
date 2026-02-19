package com.kabi.auth_msc.service.jwt;

import com.kabi.auth_msc.entity.User;

import java.time.Instant;
import java.util.Map;

public interface JwtService {
    public String generateToken(String subject, Map<String, Object> claims);

    public Boolean isTokenValid(Map<String, Object> token);

    public Boolean isTokenExpired(Instant expTime);

    public Map<String, Object> extractClaims(String token);

    public User isValidUser(String email);
}
