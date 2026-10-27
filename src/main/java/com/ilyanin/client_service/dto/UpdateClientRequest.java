package com.ilyanin.client_service.dto;

import com.ilyanin.client_service.model.ClientStatus;
import com.ilyanin.client_service.model.Gender;

public record UpdateClientRequest(
    String fullName,
    Gender gender,
    ClientStatus status
) {

}
