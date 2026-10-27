package com.ilyanin.client_service.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ilyanin.client_service.dto.ClientResponse;
import com.ilyanin.client_service.dto.CreateClientRequest;
import com.ilyanin.client_service.dto.UpdateClientRequest;
import com.ilyanin.client_service.service.ClientAccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clients")
public class ClientAccountController {

    private final ClientAccountService service;

    public ClientAccountController(ClientAccountService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody CreateClientRequest request) {
        ClientResponse created = service.createClient(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<ClientResponse> getClients() {
        return service.getClients();
    }

    @GetMapping("/{id}")
    public ClientResponse getClientById(@PathVariable Long id) {
        return service.getClientById(id);
    }

    @PutMapping("/{id}")
    public ClientResponse updateClient(
        @PathVariable Long id,
        @Valid @RequestBody UpdateClientRequest request
    ) {
        return service.updateClient(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable Long id) {
        service.deleteClient(id);
    }
}