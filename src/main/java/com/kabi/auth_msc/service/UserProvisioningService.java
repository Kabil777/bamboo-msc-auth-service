package com.kabi.auth_msc.service;

import com.kabi.auth_msc.dto.ProvisionUserRequest;
import com.kabi.auth_msc.entity.CustomUserDetails;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class UserProvisioningService {

    private final RestClient restClient;

    public UserProvisioningService(
            RestClient.Builder restClientBuilder,
            @Value("${user.service.url}") String userServiceUrl) {
        this.restClient = restClientBuilder.baseUrl(userServiceUrl).build();
    }

    public void provisionIfNeeded(CustomUserDetails userDetails) {
        if (!userDetails.isNewUser()) {
            return;
        }

        ProvisionUserRequest request =
                new ProvisionUserRequest(
                        userDetails.getId(),
                        userDetails.getEmail(),
                        userDetails.getName(),
                        userDetails.getPictureUrl(),
                        userDetails.getName());

        restClient
                .post()
                .uri("/internal/user/provision")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
