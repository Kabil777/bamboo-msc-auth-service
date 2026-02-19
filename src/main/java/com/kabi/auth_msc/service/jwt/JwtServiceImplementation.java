package com.kabi.auth_msc.service.jwt;

import com.kabi.auth_msc.entity.User;
import com.kabi.auth_msc.exception.customExceptions.CustomUserNotFoundException;
import com.kabi.auth_msc.repository.UserRepository;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service("JwtServiceImplementation")
public class JwtServiceImplementation implements JwtService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;

    public JwtServiceImplementation(
            JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, UserRepository userRepository) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.userRepository = userRepository;
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        Instant instant = Instant.now();
        JwtClaimsSet jwtClaims =
                JwtClaimsSet.builder()
                        .issuer("admin@bammooCorp")
                        .issuedAt(instant)
                        .expiresAt(instant.plus(10, ChronoUnit.MINUTES))
                        .subject(subject)
                        .claims(claim -> claim.putAll(claims))
                        .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaims)).getTokenValue();
    }

    @Override
    public Boolean isTokenValid(Map<String, Object> claims) {
        return !isTokenExpired((Instant) claims.get("exp"));
    }

    @Override
    public Boolean isTokenExpired(Instant expTime) {
        return expTime.isBefore(Instant.now());
    }

    @Override
    public Map<String, Object> extractClaims(String token) {
        try {
            Jwt claims = jwtDecoder.decode(token);
            return claims.getClaims();
        } catch (JwtException jwtException) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
    }

    @Override
    public User isValidUser(String email) {
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new CustomUserNotFoundException("User not found"));
        return user;
    }
}
