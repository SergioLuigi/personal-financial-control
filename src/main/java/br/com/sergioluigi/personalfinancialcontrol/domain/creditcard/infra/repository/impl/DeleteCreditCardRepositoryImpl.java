package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.repository.DeleteCreditCardRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeleteCreditCardRepositoryImpl implements DeleteCreditCardRepository {

    private final CreditCardRepository creditCardRepository;

    @Override
    public void execute(UUID id) {
        creditCardRepository.deleteById(id);
    }
}
