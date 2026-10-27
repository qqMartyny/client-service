package com.ilyanin.client_service.exception;
public class ClientAccountNotFoundException extends RuntimeException {

    public ClientAccountNotFoundException(Long id) {
        super("Client account with id " + id + " not found");
    }
}
