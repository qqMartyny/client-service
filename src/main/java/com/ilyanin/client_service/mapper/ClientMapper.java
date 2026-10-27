package com.ilyanin.client_service.mapper;

import org.springframework.stereotype.Component;

import com.ilyanin.client_service.dto.ClientResponse;
import com.ilyanin.client_service.dto.CreateClientRequest;
import com.ilyanin.client_service.entity.ClientAccountEntity;

@Component 
public class ClientMapper {
    public ClientAccountEntity toEntity(CreateClientRequest request) {
        return new ClientAccountEntity(request.fullName(), request.gender());
    }

    public ClientResponse toResponse(ClientAccountEntity entity) {
        return new ClientResponse(
            entity.getId(),
            entity.getFullName(),
            entity.getGender(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
