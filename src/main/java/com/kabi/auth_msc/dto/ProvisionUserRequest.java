package com.kabi.auth_msc.dto;

import java.util.UUID;

public record ProvisionUserRequest(
        UUID id, String email, String name, String coverUrl, String handle) {}
