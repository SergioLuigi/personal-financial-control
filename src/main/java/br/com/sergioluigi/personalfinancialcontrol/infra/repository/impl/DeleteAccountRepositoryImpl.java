package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.DeleteAccountRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeleteAccountRepositoryImpl implements DeleteAccountRepository {

    private final AccountRepository accountRepository;

    @Override
    public void execute(UUID id){
        accountRepository.deleteById(id);
    }
}
