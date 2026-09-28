package br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository;

import br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.entity.BankAccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface BankAccountJpaRepository extends JpaRepository<BankAccountJpaEntity, UUID> {

    // The column collation compares names ignoring case and accents.
    boolean existsByAuditCreatedByAndName(String createdBy, String name);
}
