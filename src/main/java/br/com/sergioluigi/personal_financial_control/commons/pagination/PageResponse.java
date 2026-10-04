package br.com.sergioluigi.personal_financial_control.commons.pagination;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * A page of a list: {@code items}, {@code page}, {@code total_items} and {@code total_pages} (minimum 1).
 *
 * @param items the items of this page
 * @param page the page number, from 0
 * @param totalItems the number of items across all pages
 * @param totalPages the number of pages, at least 1 even for an empty list
 * @param <T> the type of the items
 */
public record PageResponse<T>(List<T> items, int page, long totalItems, int totalPages) {

    /**
     * Converts a page of domain items into a response page.
     *
     * @param page the page read from the repository
     * @param mapper converts each domain item into its response item
     * @param <D> the domain type
     * @param <T> the response type
     * @return the response page
     */
    public static <D, T> PageResponse<T> from(Page<D> page, Function<D, T> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getTotalElements(),
                Math.max(1, page.getTotalPages()));
    }
}
