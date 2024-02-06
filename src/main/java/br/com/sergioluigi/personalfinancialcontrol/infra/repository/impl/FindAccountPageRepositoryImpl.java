package br.com.sergioluigi.personalfinancialcontrol.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.application.gateway.FindAccountPageRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.AccountRepository;
import br.com.sergioluigi.personalfinancialcontrol.infra.repository.entity.AccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FindAccountPageRepositoryImpl implements FindAccountPageRepository {

    private final AccountRepository accountRepository;

    @Override
    public Page<AccountModel> execute(String username, Pageable pageable) {
        return accountRepository.findAllByUser_username(pageable, username).map(AccountEntity::toModel);
    }
}
