package br.com.sergioluigi.personalfinancialcontrol.usecase.account;

import java.util.UUID;

public interface DeleteAccount {
    void execute(UUID id);
}
