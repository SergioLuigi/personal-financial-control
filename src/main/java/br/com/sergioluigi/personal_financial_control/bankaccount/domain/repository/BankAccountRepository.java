package br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.model.BankAccountPageFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BankAccountRepository {

    Optional<BankAccount> findByNameAndOwner(String name);

    Optional<BankAccount> findByIdAndOwner(String id);

    Page<BankAccount> findPage(BankAccountPageFilter filter, Pageable pageable);

    BankAccount save(BankAccount bankAccount);
}
