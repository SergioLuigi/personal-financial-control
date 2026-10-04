package br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.impl;
import br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.ListBankAccountsUseCase;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.BankAccountRepository;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.model.BankAccountFilter;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.rule.BankAccountBalanceRangeIsValidRule;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implements {@link ListBankAccountsUseCase}. */
@Service
@RequiredArgsConstructor
class ListBankAccountsUseCaseImpl implements ListBankAccountsUseCase {

    /** Rejects a maximum balance lower than the minimum. */
    private final BankAccountBalanceRangeIsValidRule balanceRangeIsValidRule;
    /** Reads the accounts. */
    private final BankAccountRepository repository;
    /** Calculates the balance each account shows. */
    private final BankAccountBalances balances;

    /**
     * Validates the balance range, then reads the requested page of the accounts in range, ordered by name. The
     * balance filtered is the stored one; the accounts show their effective balance.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BankAccount> execute(String owner, BankAccountFilter filter, Pageable pageable) {
        balanceRangeIsValidRule.check(filter.minBalance(), filter.maxBalance());

        return repository.findAllByOwner(owner, filter, pageable).map(balances::effective);
    }
}
