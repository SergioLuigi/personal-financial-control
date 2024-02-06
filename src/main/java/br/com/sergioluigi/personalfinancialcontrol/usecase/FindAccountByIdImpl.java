package br.com.sergioluigi.personalfinancialcontrol.usecase;

import br.com.sergioluigi.personalfinancialcontrol.application.usecase.FindAccountById;
import br.com.sergioluigi.personalfinancialcontrol.domain.exception.ApplicationException;
import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.application.gateway.FindAccountByIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static br.com.sergioluigi.personalfinancialcontrol.domain.exception.ExceptionsConstant.ACCOUNT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FindAccountByIdImpl implements FindAccountById {

    private final FindAccountByIdRepository findAccountByIdRepository;

    public AccountModel execute(UUID id) {
        return findAccountByIdRepository.execute(id)
                .orElseThrow(() -> new ApplicationException(ACCOUNT_NOT_FOUND));
    }
}
