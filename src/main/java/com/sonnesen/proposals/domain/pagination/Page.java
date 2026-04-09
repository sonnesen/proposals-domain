package com.sonnesen.proposals.domain.pagination;

/**
 * Represents pagination information with the current page number and the number
 * of items per page.
 */
public record Page(int currentPage, int perPage) {

}
