package com.kabi.auth_msc.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class JwkController {
    private final RSAKey rsaKey;

    JwkController(RSAKey rsaKey) {
        this.rsaKey = rsaKey;
    }

    @GetMapping("/oauth2/jwks")
    public Map<String, Object> keys() {
        return new JWKSet(rsaKey.toPublicJWK()).toJSONObject();
    }
}
