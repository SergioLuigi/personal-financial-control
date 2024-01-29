package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.entity;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.entity.AccountEntity;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.model.CreditCardModel;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static jakarta.persistence.CascadeType.REMOVE;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "credit_card")
@SoftDelete(columnName = "wasDeleted")
@SQLRestriction("was_deleted = false")
@EntityListeners(AuditingEntityListener.class)
public class CreditCardEntity {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(unique = true)
    private String name;

    @ManyToOne(cascade = REMOVE)
    private AccountEntity account;

    @Column(nullable = false)
    private Double limit;

    @Column(nullable = false)
    private Double balance;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private LocalDate closingDate;

    @CreatedDate
    private LocalDateTime createdOn;

    @LastModifiedDate
    private LocalDateTime lastUpdateOn;

    private boolean wasDeleted;

    public CreditCardEntity(CreditCardModel creditCardModel) {
        this.id = creditCardModel.getId();
        this.name = creditCardModel.getName();
        this.limit = creditCardModel.getLimit();
        this.balance = creditCardModel.getBalance();
        this.closingDate = creditCardModel.getClosingDate();
        this.dueDate = creditCardModel.getDueDate();
        this.createdOn = creditCardModel.getCreatedOn();
        this.lastUpdateOn = creditCardModel.getLastUpdateOn();
    }

    public CreditCardModel toModel() {
        return CreditCardModel.builder()
                .id(this.id)
                .name(this.name)
                .balance(this.balance)
                .closingDate(this.closingDate)
                .dueDate(this.dueDate)
                .createdOn(this.createdOn)
                .lastUpdateOn(this.lastUpdateOn)
                .build();
    }
}
