package com.sonnesen.proposals.domain.exception;

/**
 * Base class for domain-specific exceptions.
 */
public class DomainException extends RuntimeException {

    /**
     * Constructor for DomainException.
     *
     * @param message The exception message.
     */
    public DomainException(String message) {
        super(message, null, true, false);
    }

}
