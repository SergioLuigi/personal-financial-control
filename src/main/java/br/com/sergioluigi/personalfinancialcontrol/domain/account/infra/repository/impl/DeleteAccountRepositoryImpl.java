package br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.repository.DeleteAccountRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.repository.AccountRepository;
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
