package br.com.sergioluigi.personalfinancialcontrol.application.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;

import java.util.UUID;

public interface FindAccountById {
    AccountModel execute(UUID id);
}
