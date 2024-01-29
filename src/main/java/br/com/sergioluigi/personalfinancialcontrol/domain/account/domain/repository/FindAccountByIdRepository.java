package br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.repository;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.model.AccountModel;

import java.util.Optional;
import java.util.UUID;

public interface FindAccountByIdRepository {
    Optional<AccountModel> execute(UUID id);
}
