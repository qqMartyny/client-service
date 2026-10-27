package com.ilyanin.client_service.exception;
public class ClientAccountNotFoundException extends RuntimeException{
    public ClientAccountNotFoundException(Long id) {
        super("Cannot find client account with id: " + id);
    }
}
