package com.ilyanin.client_service.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.ilyanin.client_service.dto.ClientResponse;
import com.ilyanin.client_service.dto.CreateClientRequest;
import com.ilyanin.client_service.dto.UpdateClientRequest;
import com.ilyanin.client_service.service.ClientAccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clients")
@Tag(name = "Clients", description = "Учёт клиентов")
public class ClientAccountController {

    private final ClientAccountService service;

    public ClientAccountController(ClientAccountService service) {
        this.service = service;
    }

    @Operation(summary = "Создать клиента", description = "Клиент создаётся со статусом ACTIVE")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Клиент создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody CreateClientRequest request,
                                                       UriComponentsBuilder uriBuilder) {
        ClientResponse created = service.createClient(request);
        URI location = uriBuilder.path("/api/clients/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Список всех клиентов")
    @GetMapping
    public List<ClientResponse> getClients() {
        return service.getClients();
    }

    @Operation(summary = "Клиент по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Клиент найден"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    public ClientResponse getClientById(@PathVariable Long id) {
        return service.getClientById(id);
    }

    @Operation(summary = "Полная замена данных клиента",
            description = "Все поля обязательны. Если клиента нет — 404, запись не создаётся")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Клиент обновлён"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Клиент не найден",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    public ClientResponse updateClient(@PathVariable Long id,
                                       @Valid @RequestBody UpdateClientRequest request) {
        return service.updateClient(id, request);
    }

    @Operation(summary = "Удалить клиента")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Клиент удалён"),
            @ApiResponse(responseCode = "404", description = "Клиент не найден",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable Long id) {
        service.deleteClient(id);
    }
}