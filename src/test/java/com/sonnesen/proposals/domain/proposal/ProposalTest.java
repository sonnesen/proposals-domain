package com.sonnesen.proposals.domain.proposal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.sonnesen.proposals.domain.exception.IllegalProposalStateException;

@DisplayName("Proposal Domain Tests")
class ProposalTest {

    @Nested
    @DisplayName("When creating a new proposal")
    class CreatingNewProposal {

        @Test
        @DisplayName("Should create proposal with valid data")
        void shouldCreateProposalWithValidData() {
            // Given
            String customerName = "John Doe";
            BigDecimal amount = new BigDecimal("10000.00");
            Integer termInMonths = 12;

            // When
            Proposal proposal = Proposal.newProposal(customerName, amount, termInMonths);

            // Then
            assertAll(
                    () -> assertNotNull(proposal),
                    () -> assertEquals(customerName, proposal.getCustomerName()),
                    () -> assertEquals(amount, proposal.getAmount()),
                    () -> assertEquals(termInMonths, proposal.getTermInMonths()),
                    () -> assertEquals(ProposalStatus.IN_PROGRESS, proposal.getStatus()),
                    () -> assertNotNull(proposal.getCreatedAt()),
                    () -> assertNotNull(proposal.getUpdatedAt()));
        }

        @Test
        @DisplayName("Should throw exception when customer name is null")
        void shouldThrowExceptionWhenCustomerNameIsNull() {
            // Given
            String customerName = null;
            BigDecimal amount = new BigDecimal("10000.00");
            Integer termInMonths = 12;

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Proposal.newProposal(customerName, amount, termInMonths));
            assertEquals("Customer name must not be null or blank.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when customer name is blank")
        void shouldThrowExceptionWhenCustomerNameIsBlank() {
            // Given
            String customerName = "   ";
            BigDecimal amount = new BigDecimal("10000.00");
            Integer termInMonths = 12;

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Proposal.newProposal(customerName, amount, termInMonths));
            assertEquals("Customer name must not be null or blank.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when amount is null")
        void shouldThrowExceptionWhenAmountIsNull() {
            // Given
            String customerName = "John Doe";
            BigDecimal amount = null;
            Integer termInMonths = 12;

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Proposal.newProposal(customerName, amount, termInMonths));
            assertEquals("Amount must be greater than zero.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when amount is zero")
        void shouldThrowExceptionWhenAmountIsZero() {
            // Given
            String customerName = "John Doe";
            BigDecimal amount = BigDecimal.ZERO;
            Integer termInMonths = 12;

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Proposal.newProposal(customerName, amount, termInMonths));
            assertEquals("Amount must be greater than zero.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when amount is negative")
        void shouldThrowExceptionWhenAmountIsNegative() {
            // Given
            String customerName = "John Doe";
            BigDecimal amount = new BigDecimal("-1000.00");
            Integer termInMonths = 12;

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Proposal.newProposal(customerName, amount, termInMonths));
            assertEquals("Amount must be greater than zero.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when term is null")
        void shouldThrowExceptionWhenTermIsNull() {
            // Given
            String customerName = "John Doe";
            BigDecimal amount = new BigDecimal("10000.00");
            Integer termInMonths = null;

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Proposal.newProposal(customerName, amount, termInMonths));
            assertEquals("Term in months must be greater than zero.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when term is zero")
        void shouldThrowExceptionWhenTermIsZero() {
            // Given
            String customerName = "John Doe";
            BigDecimal amount = new BigDecimal("10000.00");
            Integer termInMonths = 0;

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Proposal.newProposal(customerName, amount, termInMonths));
            assertEquals("Term in months must be greater than zero.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when term exceeds 60 months")
        void shouldThrowExceptionWhenTermExceeds60Months() {
            // Given
            String customerName = "John Doe";
            BigDecimal amount = new BigDecimal("10000.00");
            Integer termInMonths = 61;

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Proposal.newProposal(customerName, amount, termInMonths));
            assertEquals("Term in months must not exceed 60 months.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("When approving a proposal")
    class ApprovingProposal {

        @Test
        @DisplayName("Should approve proposal when status is IN_PROGRESS")
        void shouldApproveProposalWhenStatusIsInProgress() {
            // Given
            Proposal proposal = Proposal.newProposal("John Doe", new BigDecimal("10000.00"), 12);
            Instant originalUpdatedAt = proposal.getUpdatedAt();

            // When
            Proposal approvedProposal = proposal.approve();

            // Then
            assertAll(
                    () -> assertEquals(ProposalStatus.APPROVED, approvedProposal.getStatus()),
                    () -> assertTrue(approvedProposal.getUpdatedAt().isAfter(originalUpdatedAt)));
        }

        @Test
        @DisplayName("Should throw exception when trying to approve non-IN_PROGRESS proposal")
        void shouldThrowExceptionWhenTryingToApproveNonInProgressProposal() {
            // Given
            Proposal proposal = Proposal.with(1L, "John Doe", new BigDecimal("10000.00"),
                    12, ProposalStatus.APPROVED, Instant.now(), Instant.now());

            // When & Then
            IllegalProposalStateException exception = assertThrows(
                    IllegalProposalStateException.class,
                    () -> proposal.approve());
            assertEquals("Only proposals in progress can be approved.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("When rejecting a proposal")
    class RejectingProposal {

        @Test
        @DisplayName("Should reject proposal when status is IN_PROGRESS")
        void shouldRejectProposalWhenStatusIsInProgress() {
            // Given
            Proposal proposal = Proposal.newProposal("John Doe", new BigDecimal("10000.00"), 12);
            Instant originalUpdatedAt = proposal.getUpdatedAt();

            // When
            Proposal rejectedProposal = proposal.reject();

            // Then
            assertAll(
                    () -> assertEquals(ProposalStatus.REJECTED, rejectedProposal.getStatus()),
                    () -> assertTrue(rejectedProposal.getUpdatedAt().isAfter(originalUpdatedAt)));
        }

        @Test
        @DisplayName("Should throw exception when trying to reject non-IN_PROGRESS proposal")
        void shouldThrowExceptionWhenTryingToRejectNonInProgressProposal() {
            // Given
            Proposal proposal = Proposal.with(1L, "John Doe", new BigDecimal("10000.00"),
                    12, ProposalStatus.REJECTED, Instant.now(), Instant.now());

            // When & Then
            IllegalProposalStateException exception = assertThrows(
                    IllegalProposalStateException.class,
                    () -> proposal.reject());
            assertEquals("Only proposals in progress can be rejected.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("When cancelling a proposal")
    class CancellingProposal {

        @Test
        @DisplayName("Should cancel proposal when status is IN_PROGRESS")
        void shouldCancelProposalWhenStatusIsInProgress() {
            // Given
            Proposal proposal = Proposal.newProposal("John Doe", new BigDecimal("10000.00"), 12);
            Instant originalUpdatedAt = proposal.getUpdatedAt();

            // When
            Proposal cancelledProposal = proposal.cancel();

            // Then
            assertAll(
                    () -> assertEquals(ProposalStatus.CANCELLED, cancelledProposal.getStatus()),
                    () -> assertTrue(cancelledProposal.getUpdatedAt().isAfter(originalUpdatedAt)));
        }

        @Test
        @DisplayName("Should throw exception when trying to cancel non-IN_PROGRESS proposal")
        void shouldThrowExceptionWhenTryingToCancelNonInProgressProposal() {
            // Given
            Proposal proposal = Proposal.with(1L, "John Doe", new BigDecimal("10000.00"),
                    12, ProposalStatus.CANCELLED, Instant.now(), Instant.now());

            // When & Then
            IllegalProposalStateException exception = assertThrows(
                    IllegalProposalStateException.class,
                    () -> proposal.cancel());
            assertEquals("Only proposals in progress can be cancelled.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("When updating a proposal")
    class UpdatingProposal {

        @Test
        @DisplayName("Should update proposal when status is IN_PROGRESS")
        void shouldUpdateProposalWhenStatusIsInProgress() {
            // Given
            Proposal proposal = Proposal.newProposal("John Doe", new BigDecimal("10000.00"), 12);
            String newCustomerName = "Jane Smith";
            BigDecimal newAmount = new BigDecimal("15000.00");
            Integer newTermInMonths = 24;
            Instant originalUpdatedAt = proposal.getUpdatedAt();

            // When
            Proposal updatedProposal = proposal.update(newCustomerName, newAmount, newTermInMonths);

            // Then
            assertAll(
                    () -> assertEquals(newCustomerName, updatedProposal.getCustomerName()),
                    () -> assertEquals(newAmount, updatedProposal.getAmount()),
                    () -> assertEquals(newTermInMonths, updatedProposal.getTermInMonths()),
                    () -> assertEquals(ProposalStatus.IN_PROGRESS, updatedProposal.getStatus()),
                    () -> assertTrue(updatedProposal.getUpdatedAt().isAfter(originalUpdatedAt)));
        }

        @Test
        @DisplayName("Should throw exception when trying to update non-IN_PROGRESS proposal")
        void shouldThrowExceptionWhenTryingToUpdateNonInProgressProposal() {
            // Given
            Proposal proposal = Proposal.with(1L, "John Doe", new BigDecimal("10000.00"),
                    12, ProposalStatus.APPROVED, Instant.now(), Instant.now());

            // When & Then
            IllegalProposalStateException exception = assertThrows(
                    IllegalProposalStateException.class,
                    () -> proposal.update("Jane Smith", new BigDecimal("15000.00"), 24));
            assertEquals("Only proposals in progress can be updated.", exception.getMessage());
        }

        @Test
        @DisplayName("Should validate updated data")
        void shouldValidateUpdatedData() {
            // Given
            Proposal proposal = Proposal.newProposal("John Doe", new BigDecimal("10000.00"), 12);

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> proposal.update("", new BigDecimal("15000.00"), 24));
            assertEquals("Customer name must not be null or blank.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("When creating proposal with existing data")
    class CreatingProposalWithExistingData {

        @Test
        @DisplayName("Should create proposal with all data including ID")
        void shouldCreateProposalWithAllDataIncludingId() {
            // Given
            Long id = 1L;
            String customerName = "John Doe";
            BigDecimal amount = new BigDecimal("10000.00");
            Integer termInMonths = 12;
            ProposalStatus status = ProposalStatus.APPROVED;
            Instant createdAt = Instant.now().minusSeconds(3600);
            Instant updatedAt = Instant.now();

            // When
            Proposal proposal = Proposal.with(id, customerName, amount, termInMonths,
                    status, createdAt, updatedAt);

            // Then
            assertAll(
                    () -> assertEquals(id, proposal.getProposalId()),
                    () -> assertEquals(customerName, proposal.getCustomerName()),
                    () -> assertEquals(amount, proposal.getAmount()),
                    () -> assertEquals(termInMonths, proposal.getTermInMonths()),
                    () -> assertEquals(status, proposal.getStatus()),
                    () -> assertEquals(createdAt, proposal.getCreatedAt()),
                    () -> assertEquals(updatedAt, proposal.getUpdatedAt()));
        }
    }
}