package br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.NewBankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.BankAccountRepository;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.repository.model.BankAccountFilter;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.entity.BankAccountJpaEntity;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.spec.BankAccountDescriptionContainsSpec;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.spec.BankAccountNameContainsSpec;
import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.spec.BankAccountOwnerSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** JPA implementation of {@link BankAccountRepository}. */
@Repository
@RequiredArgsConstructor
class BankAccountRepositoryImpl implements BankAccountRepository {

    /** The Spring Data repository it delegates to. */
    private final BankAccountJpaRepository jpaRepository;

    /** Stores the account with the owner as its creator. */
    @Override
    public BankAccount save(String owner, NewBankAccount bankAccount) {
        return jpaRepository.save(BankAccountJpaEntity.from(owner, bankAccount)).toDomain();
    }

    /** Copies the new values onto the stored account. */
    @Override
    public BankAccount update(BankAccount bankAccount) {
        var entity = jpaRepository.findByIdAndAuditCreatedBy(bankAccount.id(), bankAccount.owner()).orElseThrow();
        entity.update(bankAccount);

        return jpaRepository.save(entity).toDomain();
    }

    /** Reads an account of the owner. */
    @Override
    public Optional<BankAccount> findByOwnerAndId(String owner, UUID id) {
        return jpaRepository.findByIdAndAuditCreatedBy(id, owner).map(BankAccountJpaEntity::toDomain);
    }

    /**
     * Reads the owner's accounts whose name and description contain the filter text, ordered by name and id.
     */
    @Override
    public List<BankAccount> findAllByOwner(String owner, BankAccountFilter filter) {
        Specification<BankAccountJpaEntity> specification = Specification.allOf(
                new BankAccountOwnerSpec(owner),
                new BankAccountNameContainsSpec(filter.name()),
                new BankAccountDescriptionContainsSpec(filter.description()));

        return jpaRepository.findAll(specification, Sort.by("name", "id")).stream()
                .map(BankAccountJpaEntity::toDomain)
                .toList();
    }

    /** Reads the owner's accounts among those ids. */
    @Override
    public List<BankAccount> findAllByOwnerAndIdIn(String owner, Collection<UUID> ids) {
        return jpaRepository.findAllByAuditCreatedByAndIdIn(owner, ids).stream()
                .map(BankAccountJpaEntity::toDomain)
                .toList();
    }

    /** Tells whether the owner has an account with that name. */
    @Override
    public boolean existsByOwnerAndName(String owner, String name) {
        return jpaRepository.existsByAuditCreatedByAndName(owner, name);
    }

    /** Tells whether the owner has an account with that name other than the excluded one. */
    @Override
    public boolean existsByOwnerAndNameExcludingId(String owner, String name, UUID excludedId) {
        return jpaRepository.existsByAuditCreatedByAndNameAndIdNot(owner, name, excludedId);
    }

    /** Marks the record deleted, or erases the row. */
    @Override
    public void delete(BankAccount account, boolean keepRecord) {
        if (keepRecord) {
            jpaRepository.markDeleted(account.id(), account.id().toString());
        } else {
            jpaRepository.deleteById(account.id());
        }
    }
}
