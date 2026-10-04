package br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.impl;

import br.com.sergioluigi.personal_financial_control.commons.exception.BusinessException;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.message.BankAccountMessage;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.NewBankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.BankAccountRepository;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.rule.BankAccountNameIsUniqueForOwnerRule;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateBankAccountUseCaseImplTest {

    private final BankAccountRepository repository = mock(BankAccountRepository.class);
    private final CreateBankAccountUseCaseImpl useCase = new CreateBankAccountUseCaseImpl(
            new BankAccountNameIsUniqueForOwnerRule(repository), repository);

    @Test
    void savesTheAccountForTheOwner() {
        var newAccount = new NewBankAccount("Savings", null, null);
        var saved = new BankAccount(UUID.randomUUID(), "alice", "savings", null, newAccount.balance());
        when(repository.save("alice", newAccount)).thenReturn(saved);

        var result = useCase.execute("alice", newAccount);

        assertThat(result).isEqualTo(saved);
    }

    @Test
    void savesNothingWhenTheNameIsTaken() {
        when(repository.existsByOwnerAndName("alice", "savings")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute("alice", new NewBankAccount("savings", null, null)))
                .isInstanceOfSatisfying(BusinessException.class, exception ->
                        assertThat(exception.getBusinessMessage()).isEqualTo(BankAccountMessage.NAME_ALREADY_EXISTS));
        verify(repository, never()).save(any(), any());
    }
}
