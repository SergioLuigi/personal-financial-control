package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;

import java.util.Optional;
import java.util.UUID;

public interface FindAccountByIdRepository {
    Optional<AccountModel> execute(UUID id);
}
