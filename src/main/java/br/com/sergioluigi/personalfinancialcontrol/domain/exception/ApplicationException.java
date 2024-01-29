package br.com.sergioluigi.personalfinancialcontrol.domain.exception;

import org.springframework.web.server.ResponseStatusException;

import java.io.Serial;

public class ApplicationException extends ResponseStatusException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ApplicationException(ExceptionsConstant exceptionConstant) {
        super(exceptionConstant.getStatus(),exceptionConstant.getReason());
    }

}
