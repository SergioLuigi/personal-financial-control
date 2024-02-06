package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

import java.util.UUID;

public interface DeleteAccountRepository {
    void execute(UUID id);
}
