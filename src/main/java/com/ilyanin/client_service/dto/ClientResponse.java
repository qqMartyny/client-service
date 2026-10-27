package com.ilyanin.client_service.dto;

import java.time.OffsetDateTime;

import com.ilyanin.client_service.model.ClientStatus;
import com.ilyanin.client_service.model.Gender;

public record ClientResponse(
    Long id, 
    String fullName,
    Gender gender,
    ClientStatus status,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {

}
