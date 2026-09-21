package br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.entity;

import br.com.sergioluigi.personal_financial_control.commons.audit.AuditEntity;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bank_account")
@EntityListeners(AuditingEntityListener.class)
public class BankAccountJpaEntity {

    @Id
    @UuidGenerator
    private String id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", nullable = false, length = 150)
    private String description;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Embedded
    private AuditEntity audit;

    public static BankAccountJpaEntity from(BankAccount bankAccount) {
        return new BankAccountJpaEntity(
                bankAccount.id(),
                bankAccount.name(),
                bankAccount.description(),
                bankAccount.balance(),
                new AuditEntity(
                        bankAccount.createdBy(),
                        bankAccount.updatedBy(),
                        bankAccount.createdAt(),
                        bankAccount.updatedAt()
                )
        );
    }

    public BankAccount toDomain() {
        return new BankAccount(
                this.id,
                this.name,
                this.description,
                this.balance,
                audit.getCreatedBy(),
                audit.getUpdatedBy(),
                audit.getCreatedAt(),
                audit.getUpdatedAt()
        );
    }
}
