package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository;

import java.util.UUID;

public interface DeleteCreditCardRepository {
    void execute(UUID id);
}
