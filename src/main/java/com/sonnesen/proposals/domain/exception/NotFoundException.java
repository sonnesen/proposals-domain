package com.sonnesen.proposals.domain.exception;

/**
 * Exception thrown when a domain entity is not found.
 */
public class NotFoundException extends DomainException {

    /**
     * Constructor for NotFoundException.
     *
     * @param message The exception message.
     */
    public NotFoundException(String message) {
        super(message);
    }
}
