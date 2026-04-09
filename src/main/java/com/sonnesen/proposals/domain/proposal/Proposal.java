package com.sonnesen.proposals.domain.proposal;

import java.math.BigDecimal;
import java.time.Instant;

import com.sonnesen.proposals.domain.exception.IllegalProposalStateException;

/**
 * Represents a proposal with its details.
 */
public class Proposal {
    private Long proposalId;
    private String customerName;
    private BigDecimal amount;
    private Integer termInMonths;
    private ProposalStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    private Proposal(Long id, String customerName, BigDecimal amount, Integer termInMonths,
            ProposalStatus status, Instant createdAt, Instant updatedAt) {
        this(customerName, amount, termInMonths, status, createdAt, updatedAt);
        this.proposalId = id;
    }

    private Proposal(String customerName, BigDecimal amount, Integer termInMonths,
            ProposalStatus status, Instant createdAt, Instant updatedAt) {
        this.customerName = customerName;
        this.amount = amount;
        this.termInMonths = termInMonths;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.selfValidate();
    }

    private void selfValidate() {
        if (this.customerName == null || this.customerName.isBlank()) {
            throw new IllegalArgumentException("Customer name must not be null or blank.");
        }
        if (this.amount == null || this.amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        if (this.termInMonths == null || this.termInMonths <= 0) {
            throw new IllegalArgumentException("Term in months must be greater than zero.");
        }
        if (this.termInMonths > 60) {
            throw new IllegalArgumentException("Term in months must not exceed 60 months.");
        }
        if (this.status == null) {
            throw new IllegalArgumentException("Status must not be null.");
        }
    }

    /**
     * Creates a new proposal with the given details.
     *
     * @param customerName the name of the customer
     * @param amount       the proposal amount
     * @param termInMonths the term in months
     * @return the newly created proposal
     */
    public static Proposal newProposal(String customerName, BigDecimal amount,
            Integer termInMonths) {
        final var now = Instant.now();
        return new Proposal(customerName, amount, termInMonths, ProposalStatus.IN_PROGRESS, now,
                now);
    }

    /**
     * Approves the proposal if it is in progress.
     *
     * @return the updated proposal
     * @throws IllegalProposalStateException if the proposal is not in progress
     */
    public Proposal approve() {
        if (this.status != ProposalStatus.IN_PROGRESS) {
            throw new IllegalProposalStateException("Only proposals in progress can be approved.");
        }
        this.status = ProposalStatus.APPROVED;
        this.updatedAt = Instant.now();
        return this;
    }

    /**
     * Rejects the proposal if it is in progress.
     *
     * @return the updated proposal
     * @throws IllegalProposalStateException if the proposal is not in progress
     */
    public Proposal reject() {
        if (this.status != ProposalStatus.IN_PROGRESS) {
            throw new IllegalProposalStateException("Only proposals in progress can be rejected.");
        }
        this.status = ProposalStatus.REJECTED;
        this.updatedAt = Instant.now();
        return this;
    }

    /**
     * Cancels the proposal if it is in progress.
     *
     * @return the updated proposal
     * @throws IllegalProposalStateException if the proposal is not in progress
     */
    public Proposal cancel() {
        if (this.status != ProposalStatus.IN_PROGRESS) {
            throw new IllegalProposalStateException("Only proposals in progress can be cancelled.");
        }
        this.status = ProposalStatus.CANCELLED;
        this.updatedAt = Instant.now();
        return this;
    }

    /**
     * Updates the proposal details.
     *
     * @param customerName the new customer name
     * @param amount       the new amount
     * @param termInMonths the new term in months
     * @return the updated proposal
     * @throws IllegalProposalStateException if the proposal is not in progress
     */
    public Proposal update(String customerName, BigDecimal amount, Integer termInMonths) {
        if (this.status != ProposalStatus.IN_PROGRESS) {
            throw new IllegalProposalStateException("Only proposals in progress can be updated.");
        }
        this.customerName = customerName;
        this.amount = amount;
        this.termInMonths = termInMonths;
        this.updatedAt = Instant.now();
        this.selfValidate();
        return this;
    }

    public Long getProposalId() {
        return proposalId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getTermInMonths() {
        return termInMonths;
    }

    public ProposalStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public static Proposal with(Long id, String customerName, BigDecimal amount,
            Integer termInMonths, ProposalStatus status, Instant createdAt, Instant updatedAt) {
        return new Proposal(id, customerName, amount, termInMonths, status, createdAt, updatedAt);
    }

}
