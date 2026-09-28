package br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.BankAccountRepository;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.entity.BankAccountJpaEntity;
import br.com.sergioluigi.personal_financial_control.commons.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Locale;

import static br.com.sergioluigi.personal_financial_control.bankaccount.domain.message.BankAccountMessage.NAME_ALREADY_EXISTS;

@Repository
@RequiredArgsConstructor
class BankAccountRepositoryImpl implements BankAccountRepository {

    private static final String NAME_UNIQUE_KEY = "uk_bank_account_created_by_name";

    private final BankAccountJpaRepository bankAccountJpaRepository;

    @Override
    public BankAccount create(BankAccount bankAccount) {
        try {
            return bankAccountJpaRepository.saveAndFlush(BankAccountJpaEntity.from(bankAccount)).toDomain();
        } catch (DataIntegrityViolationException e) {
            // A concurrent request may take the name between the rule check and this insert.
            if (violates(e, NAME_UNIQUE_KEY)) {
                throw new BusinessException(NAME_ALREADY_EXISTS);
            }
            throw e;
        }
    }

    @Override
    public boolean existsByOwnerAndName(String owner, String name) {
        return bankAccountJpaRepository.existsByAuditCreatedByAndName(owner, name);
    }

    private static boolean violates(DataIntegrityViolationException e, String constraint) {
        for (Throwable cause = e; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException violation) {
                var name = violation.getConstraintName();
                return name != null && name.toLowerCase(Locale.ROOT).endsWith(constraint);
            }
        }
        return false;
    }
}
