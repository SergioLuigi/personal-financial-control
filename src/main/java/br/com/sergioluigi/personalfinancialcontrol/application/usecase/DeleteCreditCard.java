package br.com.sergioluigi.personalfinancialcontrol.application.usecase;

import java.util.UUID;

public interface DeleteCreditCard {
    void execute(UUID id);
}
