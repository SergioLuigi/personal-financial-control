package br.com.sergioluigi.personal_financial_control.commons.exception;

import lombok.Getter;

import java.util.List;

/**
 * The single exception for a business rule violation — a duplicate, a missing record, a forbidden
 * operation. The {@link BusinessMessage} it is built from sets the status and the message.
 */
@Getter
public class BusinessException extends ApplicationException {

    /** The violation this exception reports. */
    private final BusinessMessage businessMessage;

    /**
     * Creates the exception from the violation it reports.
     *
     * @param businessMessage the violation: its status, optional field and text
     */
    public BusinessException(BusinessMessage businessMessage) {
        super(businessMessage.status(), problemDetail(
                businessMessage.status(),
                businessMessage.message(),
                fieldErrors(businessMessage)));
        this.businessMessage = businessMessage;
    }

    /**
     * Lists the violation as a field error when it refers to a request field.
     *
     * @param businessMessage the violation being reported
     * @return one entry for the field, or an empty list when the violation concerns the request as a whole
     */
    private static List<FieldErrorDetail> fieldErrors(BusinessMessage businessMessage) {
        var field = businessMessage.field();

        return field == null
                ? List.of()
                : List.of(new FieldErrorDetail(field, businessMessage.message()));
    }
}
