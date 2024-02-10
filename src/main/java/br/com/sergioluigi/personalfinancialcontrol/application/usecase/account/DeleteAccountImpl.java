package br.com.sergioluigi.personalfinancialcontrol.application.usecase.account;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.DeleteAccountRepository;
import br.com.sergioluigi.personalfinancialcontrol.usecase.account.DeleteAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteAccountImpl implements DeleteAccount {

    private final DeleteAccountRepository deleteAccountRepository;

    @Override
    @Transactional
    public void execute(UUID id) {
        deleteAccountRepository.execute(id);
    }
}
