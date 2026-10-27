package com.ilyanin.client_service.exception;
public class ClientAccountNotFoundException extends RuntimeException {

    private final Long id;

    public ClientAccountNotFoundException(Long id) {
        super("Client account with id " + id + " not found");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
