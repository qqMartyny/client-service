package com.ilyanin.client_service.dto;

import com.ilyanin.client_service.model.Gender;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateClientRequest(
        @Schema(example = "Иванов Иван Иванович")
        @NotBlank
        @Size(max = 255)
        String fullName,

        @Schema(example = "M")
        @NotNull
        Gender gender
) {}
