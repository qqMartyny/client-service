package com.ilyanin.client_service.dto;

import com.ilyanin.client_service.model.ClientStatus;
import com.ilyanin.client_service.model.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateClientRequest(
        @NotBlank
        @Size(max = 255)
        String fullName,

        @NotNull
        Gender gender,

        @NotNull
        ClientStatus status
) {}
