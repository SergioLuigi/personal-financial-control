package br.com.sergioluigi.personal_financial_control.commons.pagination;

import br.com.sergioluigi.personal_financial_control.commons.exception.BusinessException;

import java.util.Map;

/** Reads page numbers from query parameters. */
public final class PageParam {

    /** Not instantiable: the class only has static methods. */
    private PageParam() {
    }

    /**
     * Reads a page number from a query parameter such as {@code expenses_page}: absent means
     * the first page, and anything that is not a non-negative integer is an invalid page.
     *
     * @param name the parameter name, also reported as the field of the error
     * @param params the query parameters of the request
     * @return the page number, 0 when the parameter is absent or blank
     */
    public static int read(String name, Map<String, String> params) {
        var text = params.get(name);

        if (text == null || text.isBlank()) {
            return 0;
        }

        try {
            var page = Integer.parseInt(text.strip());

            if (page >= 0) {
                return page;
            }
        } catch (NumberFormatException e) {
            // falls through to the same error as a negative number
        }

        throw new BusinessException(new InvalidPageMessage(name));
    }

    /**
     * The invalid-page violation, naming the query parameter that held the bad value.
     *
     * @param field the query parameter that held the bad value
     */
    private record InvalidPageMessage(String field) implements br.com.sergioluigi.personal_financial_control.commons.exception.BusinessMessage {

        /** Always 400. */
        @Override
        public org.springframework.http.HttpStatus status() {
            return org.springframework.http.HttpStatus.BAD_REQUEST;
        }

        /** The text shared with {@link PaginationMessage#INVALID_PAGE}. */
        @Override
        public String message() {
            return PaginationMessage.INVALID_PAGE.message();
        }
    }
}
