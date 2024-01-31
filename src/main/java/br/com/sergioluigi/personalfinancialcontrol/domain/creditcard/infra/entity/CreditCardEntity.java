package br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.entity;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.entity.AccountEntity;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.domain.model.CreditCardModel;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
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
    @JoinColumn(name = "account_id")
    private AccountEntity account;

    @Column(name = "credit_card_limit", nullable = false)
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

    public static CreditCardEntity of(CreditCardModel creditCardModel) {
        return CreditCardEntity.builder()
                .id(creditCardModel.getId())
                .name(creditCardModel.getName())
                .limit(creditCardModel.getLimit())
                .balance(creditCardModel.getBalance())
                .closingDate(creditCardModel.getClosingDate())
                .dueDate(creditCardModel.getDueDate())
                .account(AccountEntity.of(creditCardModel.getAccountModel()))
                .createdOn(creditCardModel.getCreatedOn())
                .lastUpdateOn(creditCardModel.getLastUpdateOn())
                .build();
    }

    public CreditCardModel toModel() {
        return CreditCardModel.builder()
                .id(this.id)
                .name(this.name)
                .balance(this.balance)
                .closingDate(this.closingDate)
                .dueDate(this.dueDate)
                .accountModel(this.account.toModel())
                .createdOn(this.createdOn)
                .lastUpdateOn(this.lastUpdateOn)
                .build();
    }
}
