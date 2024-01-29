package br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.repository.impl;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.model.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.repository.FindAccountPageRepository;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.entity.AccountEntity;
import br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.repository.AccountRepository;
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
