package com.kabi.auth_msc.service;

import com.kabi.auth_msc.dto.ProvisionUserRequest;
import com.kabi.auth_msc.entity.CustomUserDetails;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserProvisioningService {

    private final RabbitTemplate rabbitTemplate;

    public UserProvisioningService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
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

        rabbitTemplate.convertAndSend("user.events", "user.created", request);
    }
}
