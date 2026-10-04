package br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository;

import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.entity.BankAccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data access to the {@code bank_account} table. The owner is the {@code createdBy} audit column.
 */
interface BankAccountJpaRepository
        extends JpaRepository<BankAccountJpaEntity, UUID>, JpaSpecificationExecutor<BankAccountJpaEntity> {

    /** Reads an account of the owner. */
    Optional<BankAccountJpaEntity> findByIdAndAuditCreatedBy(UUID id, String createdBy);

    /** Reads the owner's accounts among those ids. */
    List<BankAccountJpaEntity> findAllByAuditCreatedByAndIdIn(String createdBy, Collection<UUID> ids);

    /** Tells whether the owner has an account with that name. */
    boolean existsByAuditCreatedByAndName(String createdBy, String name);

    /** Tells whether the owner has an account with that name other than the one with the given id. */
    boolean existsByAuditCreatedByAndNameAndIdNot(String createdBy, String name, UUID id);

    /** Marks the bank account deleted logically: its own id goes to {@code deletedId}, so its name can be used again. */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update BankAccountJpaEntity e set e.deletedId = :deletedId where e.id = :id")
    void markDeleted(@Param("id") UUID id, @Param("deletedId") String deletedId);
}
