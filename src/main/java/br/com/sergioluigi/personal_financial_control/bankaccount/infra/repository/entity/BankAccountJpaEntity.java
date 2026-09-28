package br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.entity;

import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.commons.audit.AuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "bank_account")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BankAccountJpaEntity implements Persistable<UUID> {

    @Id
    @Column(length = 36)
    private UUID id;

    @Column(nullable = false, length = 60)
    private String name;

    @Column
    private @Nullable String description;

    @Column(nullable = false, precision = 13, scale = 2)
    private BigDecimal balance;

    @Embedded
    private AuditEntity audit;

    // The id is assigned by the domain, so Spring Data cannot tell a new entity by it.
    @Transient
    private boolean persisted;

    private BankAccountJpaEntity(UUID id, String name, @Nullable String description, BigDecimal balance, AuditEntity audit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.balance = balance;
        this.audit = audit;
    }

    public static BankAccountJpaEntity from(BankAccount bankAccount) {
        var audit = new AuditEntity();
        audit.setCreatedBy(bankAccount.owner());

        return new BankAccountJpaEntity(
                bankAccount.id(),
                bankAccount.name(),
                bankAccount.description(),
                bankAccount.balance(),
                audit
        );
    }

    public BankAccount toDomain() {
        return new BankAccount(id, audit.getCreatedBy(), name, description, balance);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return !persisted;
    }

    @PostLoad
    @PostPersist
    void markPersisted() {
        persisted = true;
    }
}
