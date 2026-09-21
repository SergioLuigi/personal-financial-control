package br.com.sergioluigi.personal_financial_control.bankaccount.application.usecase;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;

public interface UpdateBankAccountUseCase {

    BankAccount execute(String id, BankAccount changes);
}
