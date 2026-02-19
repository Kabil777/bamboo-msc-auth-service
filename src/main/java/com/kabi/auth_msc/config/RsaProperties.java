package com.kabi.auth_msc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "rsa")
public record RsaProperties(RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {}
