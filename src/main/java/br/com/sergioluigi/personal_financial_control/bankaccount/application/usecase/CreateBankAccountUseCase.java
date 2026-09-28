package br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.NewBankAccount;

public interface CreateBankAccountUseCase {

    BankAccount execute(NewBankAccount newBankAccount);
}
