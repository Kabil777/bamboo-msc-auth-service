package com.kabi.auth_msc.filters;

import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultResolver;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository repo) {
        this.defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(repo, "/api/v1/auth/login");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
        return customize(req, request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(
            HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request, clientRegistrationId);
        return customize(req, request);
    }

    private OAuth2AuthorizationRequest customize(
            OAuth2AuthorizationRequest req, HttpServletRequest request) {

        if (req == null) return null;

        String mode = request.getParameter("mode");
        if (mode == null) return req;
        request.getSession().setAttribute("OAUTH_MODE", mode);

        request.getSession().setAttribute("OAUTH_MODE", mode);
        log.info("Saved OAUTH_MODE={} sessionId={}", mode, request.getSession().getId());
        return OAuth2AuthorizationRequest.from(req)
                .state(req.getState()) // keep default state
                .build();
    }
}
