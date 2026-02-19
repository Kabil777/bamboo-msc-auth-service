package com.kabi.auth_msc.config;

import com.kabi.auth_msc.filters.CustomAuthorizationRequestResolver;
import com.kabi.auth_msc.handlers.Oauth2SuccessHandler;
import com.kabi.auth_msc.service.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityClient {

    private final CustomUserDetailsService customUserDetailsService;
    private final Oauth2SuccessHandler oauth2SuccessHandler;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public WebSecurityClient(
            CustomUserDetailsService customUserDetailsService,
            Oauth2SuccessHandler oauth2SuccessHandler,
            ClientRegistrationRepository clientRegistrationRepository) {
        this.customUserDetailsService = customUserDetailsService;
        this.oauth2SuccessHandler = oauth2SuccessHandler;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(
                                                "/.well-known/**",
                                                "/oauth2/jwks/**",
                                                "/actuator/**",
                                                "/api/v1/auth/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .oauth2Login(
                        oauthLogin ->
                                oauthLogin
                                        .authorizationEndpoint(
                                                auth ->
                                                        auth.baseUri("/api/v1/auth/login")
                                                                .authorizationRequestResolver(
                                                                        new CustomAuthorizationRequestResolver(
                                                                                clientRegistrationRepository)))
                                        .userInfoEndpoint(
                                                info ->
                                                        info.oidcUserService(
                                                                customUserDetailsService))
                                        .successHandler(oauth2SuccessHandler));
        // .addFilterBefore(jwtAuthFilter, OAuth2LoginAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
