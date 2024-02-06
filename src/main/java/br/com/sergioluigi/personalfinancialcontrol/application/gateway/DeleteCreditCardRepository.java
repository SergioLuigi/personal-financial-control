package br.com.sergioluigi.personalfinancialcontrol.application.gateway;

import java.util.UUID;

public interface DeleteCreditCardRepository {
    void execute(UUID id);
}
