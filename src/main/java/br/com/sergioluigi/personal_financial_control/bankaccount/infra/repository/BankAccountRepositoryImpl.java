package br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.BankAccountRepository;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.model.BankAccountPageFilter;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.entity.BankAccountJpaEntity;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.spec.BankAccountFilterEqualOwnerSpec;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.spec.BankAccountFilterLikeDescriptionSpec;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.spec.BankAccountFilterLikeNameSpec;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.spec.BankAccountFilterRangeBalanceSpec;
import br.com.sergioluigi.personal_financial_control.commons.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
class BankAccountRepositoryImpl implements BankAccountRepository {

    private final BankAccountJpaRepository repository;
    private final CurrentUser currentUser;

    @Override
    public Optional<BankAccount> findByNameAndOwner(String name) {
        return repository.findByNameAndAudit_CreatedBy(name, currentUser.getUsername())
                .map(BankAccountJpaEntity::toDomain);
    }

    @Override
    public Optional<BankAccount> findByIdAndOwner(String id) {
        return repository.findByIdAndAudit_CreatedBy(id, currentUser.getUsername())
                .map(BankAccountJpaEntity::toDomain);
    }

    @Override
    public Page<BankAccount> findPage(BankAccountPageFilter filter, Pageable pageable) {

        var spec = new BankAccountFilterEqualOwnerSpec(currentUser.getUsername())
                .and(new BankAccountFilterLikeNameSpec(filter.name()))
                .and(new BankAccountFilterLikeDescriptionSpec(filter.description()))
                .and(new BankAccountFilterRangeBalanceSpec(filter.balanceRange()));

        return repository.findAll(spec, pageable).map(BankAccountJpaEntity::toDomain);
    }

    @Override
    public BankAccount save(BankAccount bankAccount) {

        var entity = BankAccountJpaEntity.from(bankAccount);

        return repository.save(entity).toDomain();
    }
}
