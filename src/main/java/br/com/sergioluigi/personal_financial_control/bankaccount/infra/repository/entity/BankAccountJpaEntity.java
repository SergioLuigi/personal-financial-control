package br.com.sergioluigi.personal_financial_control.bankaccount.infra.repository.entity;

import br.com.sergioluigi.personal_financial_control.commons.audit.AuditEntity;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.BankAccount;
import br.com.sergioluigi.personal_financial_control.bankaccount.domain.model.NewBankAccount;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.UUID;

/** The {@code bank_account} table. */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "bank_account")
@SQLRestriction("deleted_id = ''")
@EntityListeners(AuditingEntityListener.class)
public class BankAccountJpaEntity {

    /** The id of the account, generated on insert. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** The name of the account, in lowercase. */
    private String name;

    /** The free text about the account. */
    private String description;

    /** The balance the account was stored with, before the balance sources are added. */
    private BigDecimal balance;

    /** Empty while the bank account is in use; holds its own id once it was deleted logically, which hides it everywhere. */
    private String deletedId = "";

    /** The audit columns; {@code createdBy} holds the owner. */
    @Embedded
    private AuditEntity audit = new AuditEntity();

    /**
     * Builds the entity of a new account.
     *
     * @param owner the user who owns the account
     * @param bankAccount the account to store
     * @return the entity, not yet stored
     */
    public static BankAccountJpaEntity from(String owner, NewBankAccount bankAccount) {
        var entity = new BankAccountJpaEntity();
        entity.name = bankAccount.name();
        entity.description = bankAccount.description();
        entity.balance = bankAccount.balance();
        entity.audit.setCreatedBy(owner);
        return entity;
    }

    /**
     * Copies the values a user may change; the owner and id stay as they are.
     *
     * @param bankAccount the account with the new values
     */
    public void update(BankAccount bankAccount) {
        this.name = bankAccount.name();
        this.description = bankAccount.description();
        this.balance = bankAccount.balance();
    }

    /**
     * Converts the entity into the domain model.
     *
     * @return the account
     */
    public BankAccount toDomain() {
        return new BankAccount(id, audit.getCreatedBy(), name, description == null ? "" : description, balance);
    }
}
