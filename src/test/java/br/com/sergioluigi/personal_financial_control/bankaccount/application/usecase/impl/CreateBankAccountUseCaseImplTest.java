package br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase.impl;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.NewBankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.BankAccountRepository;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.rule.BankAccountNameIsUniqueForOwnerRule;
import br.com.sergioluigi.personal_financial_control.commons.exception.BusinessException;
import br.com.sergioluigi.personal_financial_control.commons.security.CurrentUser;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.math.BigDecimal;

import static br.com.sergioluigi.personal_financial_control.bankaccount.domain.message.BankAccountMessage.NAME_ALREADY_EXISTS;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class CreateBankAccountUseCaseImplTest {

    private final CurrentUser currentUser = mock(CurrentUser.class);

    private final BankAccountNameIsUniqueForOwnerRule nameIsUniqueForOwner = mock(BankAccountNameIsUniqueForOwnerRule.class);

    private final BankAccountRepository bankAccountRepository = mock(BankAccountRepository.class);

    private final CreateBankAccountUseCaseImpl useCase =
            new CreateBankAccountUseCaseImpl(currentUser, nameIsUniqueForOwner, bankAccountRepository);

    @Test
    void createsTheAccountForTheCurrentUserAfterCheckingTheName() {
        given(currentUser.getUsername()).willReturn("alice");
        given(bankAccountRepository.create(any())).willAnswer(invocation -> invocation.getArgument(0));

        var created = useCase.execute(new NewBankAccount("Savings", null, new BigDecimal("1000.00")));

        assertThat(created.owner()).isEqualTo("alice");
        assertThat(created.name()).isEqualTo("savings");
        assertThat(created.balance()).isEqualTo(new BigDecimal("1000.00"));

        InOrder order = inOrder(nameIsUniqueForOwner, bankAccountRepository);
        order.verify(nameIsUniqueForOwner).check("alice", "savings");
        order.verify(bankAccountRepository).create(any(BankAccount.class));
    }

    @Test
    void persistsNothingWhenTheNameIsTaken() {
        given(currentUser.getUsername()).willReturn("alice");
        willThrow(new BusinessException(NAME_ALREADY_EXISTS))
                .given(nameIsUniqueForOwner).check("alice", "savings");

        assertThatThrownBy(() -> useCase.execute(new NewBankAccount("savings", null, null)))
                .hasMessageContaining("Bank account name already exists");

        verify(bankAccountRepository, never()).create(any());
    }
}
