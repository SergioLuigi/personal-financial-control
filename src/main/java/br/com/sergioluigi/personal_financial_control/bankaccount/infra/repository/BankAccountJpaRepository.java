package br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository;

import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.entity.BankAccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

interface BankAccountJpaRepository extends JpaRepository<BankAccountJpaEntity, String>,
        JpaSpecificationExecutor<BankAccountJpaEntity> {

    Optional<BankAccountJpaEntity> findByNameAndAudit_CreatedBy(String name, String owner);

    Optional<BankAccountJpaEntity> findByIdAndAudit_CreatedBy(String id, String owner);
}
