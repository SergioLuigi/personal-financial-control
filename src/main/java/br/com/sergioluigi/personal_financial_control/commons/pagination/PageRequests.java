package br.com.sergioluigi.personal_financial_control.commons.pagination;

import br.com.sergioluigi.personal_financial_control.commons.exception.BusinessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/** Builds the page requests of the list endpoints, which all use the same page size. */
public final class PageRequests {

    /** Number of items in each page. */
    public static final int PAGE_SIZE = 10;

    /** Not instantiable: the class only has static methods. */
    private PageRequests() {
    }

    /**
     * A page of {@link #PAGE_SIZE} items, numbered from 0; a negative page is a business error.
     *
     * @param page the page number, from 0
     * @param sort the order of the items
     * @return the page request
     */
    public static Pageable of(int page, Sort sort) {
        if (page < 0) {
            throw new BusinessException(PaginationMessage.INVALID_PAGE);
        }

        return PageRequest.of(page, PAGE_SIZE, sort);
    }

    /**
     * The page and size the client asked for, in the order the list defines: a client never chooses the order,
     * so paging always visits each item once.
     *
     * @param requested the page request read from the query parameters
     * @param sort the order of the items
     * @return the page request with the fixed order
     */
    public static Pageable sorted(Pageable requested, Sort sort) {
        return PageRequest.of(requested.getPageNumber(), requested.getPageSize(), sort);
    }
}
