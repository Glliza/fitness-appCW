package com.fitnesscenter.app.exception;


public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityType, Long id) {
        super(entityType + " with id " + id + " not found");
    }

    public EntityNotFoundException(String entityType, String login) {
        super(entityType + " with login " + login + " not found");
    }
}
