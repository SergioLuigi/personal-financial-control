package br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.repository;

import java.util.UUID;

public interface DeleteAccountRepository {
    void execute(UUID id);
}
