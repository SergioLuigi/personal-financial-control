package br.com.sergioluigi.personal_financial_control.commons.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class BusinessException extends ApplicationException {

    private final BusinessMessage businessMessage;

    public BusinessException(BusinessMessage businessMessage) {
        super(businessMessage.status(), problemDetail(
                businessMessage.status(),
                businessMessage.message(),
                fieldErrors(businessMessage)));
        this.businessMessage = businessMessage;
    }

    private static List<FieldErrorDetail> fieldErrors(BusinessMessage businessMessage) {
        var field = businessMessage.field();

        return field == null
                ? List.of()
                : List.of(new FieldErrorDetail(field, businessMessage.message()));
    }
}
