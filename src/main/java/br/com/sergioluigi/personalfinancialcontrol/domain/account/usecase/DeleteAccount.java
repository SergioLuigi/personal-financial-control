package br.com.sergioluigi.personalfinancialcontrol.domain.account.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.repository.DeleteAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteAccount {

    private final DeleteAccountRepository deleteAccountRepository;

    @Transactional
    public void execute(UUID id) {
        deleteAccountRepository.execute(id);
    }
}
