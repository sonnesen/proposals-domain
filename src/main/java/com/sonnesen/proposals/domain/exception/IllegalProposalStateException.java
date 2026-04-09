package com.sonnesen.proposals.domain.exception;

/**
 * Exception thrown when an illegal state transition is attempted on a proposal.
 */
public class IllegalProposalStateException extends DomainException {

    /**
     * Constructor for IllegalProposalStateException.
     *
     * @param message The exception message.
     */
    public IllegalProposalStateException(String message) {
        super(message);
    }
}
