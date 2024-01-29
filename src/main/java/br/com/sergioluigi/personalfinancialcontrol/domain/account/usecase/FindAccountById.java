package br.com.sergioluigi.personalfinancialcontrol.domain.account.usecase;

import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.model.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.repository.FindAccountByIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.ACCOUNT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FindAccountById {

    private final FindAccountByIdRepository findAccountByIdRepository;

    public AccountModel execute(UUID id) {
        return findAccountByIdRepository.execute(id)
                .orElseThrow(() -> new ApplicationException(ACCOUNT_NOT_FOUND));
    }
}
