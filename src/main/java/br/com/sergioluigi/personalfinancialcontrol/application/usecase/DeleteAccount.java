package br.com.sergioluigi.personalfinancialcontrol.application.usecase;

import java.util.UUID;

public interface DeleteAccount {
    void execute(UUID id);
}
