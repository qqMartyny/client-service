package com.ilyanin.client_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ilyanin.client_service.dto.ClientResponse;
import com.ilyanin.client_service.dto.CreateClientRequest;
import com.ilyanin.client_service.dto.UpdateClientRequest;
import com.ilyanin.client_service.exception.ClientAccountNotFoundException;
import com.ilyanin.client_service.mapper.ClientMapper;
import com.ilyanin.client_service.repository.ClientAccountRepository;

@Service 
public class ClientAccountService {

    private final ClientAccountRepository repository;
    private final ClientMapper mapper;

    public ClientAccountService(ClientAccountRepository repository, ClientMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> getClients() {
        var entities = repository.findAll();
        return entities.stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public ClientResponse createClient(CreateClientRequest request) {
        var entityToSave = mapper.toEntity(request);
        var savedEntity = repository.save(entityToSave);
        return mapper.toResponse(savedEntity);
    }

    @Transactional(readOnly = true)
    public ClientResponse getClientById(Long id) {
        var entity = repository.findById(id).orElseThrow(
            () -> new ClientAccountNotFoundException(id)
        );
        return mapper.toResponse(entity);
    }

    @Transactional
    public ClientResponse updateClient(Long id, UpdateClientRequest request) {
        var entityToUpdate = repository.findById(id).orElseThrow(
            () -> new ClientAccountNotFoundException(id)
        );

        entityToUpdate.update(
            request.fullName(),
            request.gender(),
            request.status()
        );

        repository.flush();

        return mapper.toResponse(entityToUpdate);
    }

    @Transactional 
    public void deleteClient(Long id) {
        var entityToDelete = repository.findById(id).orElseThrow(
            () -> new ClientAccountNotFoundException(id)
        );
        repository.delete(entityToDelete);
    }
}
