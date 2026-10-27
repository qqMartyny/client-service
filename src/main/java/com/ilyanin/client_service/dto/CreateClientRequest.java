package com.ilyanin.client_service.dto;

import com.ilyanin.client_service.model.Gender;

public record CreateClientRequest(
    String fullname,
    Gender gender
) {

}
