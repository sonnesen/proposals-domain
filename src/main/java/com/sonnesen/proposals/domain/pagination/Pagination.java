package com.sonnesen.proposals.domain.pagination;

import java.util.List;
import java.util.function.Function;

/**
 * A generic pagination record that holds paginated data.
 *
 * @param currentPage the current page number
 * @param perPage     the number of items per page
 * @param totalPages  the total number of pages
 * @param totalItems  the total number of items
 * @param items       the list of items on the current page
 */
public record Pagination<T>(int currentPage, int perPage, int totalPages, long totalItems,
        List<T> items) {

    /**
     * Maps the items of the current pagination to another type using the provided
     * mapper function.
     *
     * @param <T>    the type of items being paginated
     * @param <R>    the type of items after mapping
     * @param mapper the function to map items from type T to type R
     * @return a new Pagination object containing the mapped items
     */
    public <R> Pagination<R> mapItems(Function<T, R> mapper) {
        return new Pagination<>(currentPage, perPage, totalPages, totalItems,
                items.stream().map(mapper).toList());
    }
}
