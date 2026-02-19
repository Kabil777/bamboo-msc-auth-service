package com.kabi.auth_msc.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CustomUserDetails implements OidcUser {
    private final User user;
    private final OAuth2User oAuth2User;
    private final boolean newUser;

    public CustomUserDetails(User user, OAuth2User oAuth2User, boolean newUser) {
        this.user = user;
        this.oAuth2User = oAuth2User;
        this.newUser = newUser;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public String getName() {
        return user.getName() != null ? user.getName() : user.getEmail();
    }

    public Map<String, Object> getClaims() {
        if (oAuth2User instanceof OidcUser oidcUser) {
            return oidcUser.getClaims();
        }
        return Map.of();
    }

    public OidcIdToken getIdToken() {
        if (oAuth2User instanceof OidcUser oidcUser) {
            return oidcUser.getIdToken();
        }
        return null;
    }

    public OidcUserInfo getUserInfo() {
        if (oAuth2User instanceof OidcUser oidcUser) {
            return oidcUser.getUserInfo();
        }
        return null;
    }

    public UUID getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getProvider() {
        return user.getProvider();
    }

    public String getProviderId() {
        return user.getProvider_id();
    }

    public String getPictureUrl() {
        return user.getPicture_url();
    }

    public Instant getCreatedAt() {
        return user.getCreated_at();
    }

    public boolean isNewUser() {
        return newUser;
    }
}
