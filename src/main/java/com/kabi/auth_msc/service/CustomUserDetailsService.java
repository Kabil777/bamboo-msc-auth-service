package com.kabi.auth_msc.service;

import com.kabi.auth_msc.entity.CustomUserDetails;
import com.kabi.auth_msc.entity.User;
import com.kabi.auth_msc.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@Slf4j
public class CustomUserDetailsService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final UserRepository userRepository;

    CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest oidcUserRequest) throws OAuth2AuthenticationException {
        OidcUserService delegate = new OidcUserService();
        OidcUser oidcUser = delegate.loadUser(oidcUserRequest);

        Map<String, Object> attributes = oidcUser.getAttributes();
        log.info("OIDC attributes: {}", attributes);
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");
        String providerId = attributes.get("sub").toString();
        String registrationId = oidcUserRequest.getClientRegistration().getRegistrationId();
        log.info("OAuth2 attributes: {}", attributes);

        User user = userRepository.findByEmail(email).orElseGet(User::new);

        boolean isNewUser = (user.getId() == null);

        user.setEmail(email);
        user.setName(name);
        user.setProvider(registrationId);
        user.setProvider_id(providerId);
        user.setPicture_url(picture);
        user.setRole("ROLE_USER");
        user.setCreated_at(Instant.now());
        user.setLastLogin(Instant.now());

        if (isNewUser) {
            user.setCreated_at(Instant.now());
        }
        userRepository.save(user);

        return new CustomUserDetails(user, oidcUser, isNewUser);
    }
}
