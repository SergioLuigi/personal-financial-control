package br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.controller.validation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@PreAuthorize("@isAuthenticatedUserAccountOwnerValidation.execute(#id)")
public @interface IsAuthenticatedUserAccountOwner {
}
