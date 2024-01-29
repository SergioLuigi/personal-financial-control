package br.com.sergioluigi.personalfinancialcontrol.domain.account.infra.entity;

import br.com.sergioluigi.personalfinancialcontrol.domain.account.domain.model.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.creditcard.infra.entity.CreditCardEntity;
import br.com.sergioluigi.personalfinancialcontrol.domain.user.infra.repository.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.CascadeType.REMOVE;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
@SoftDelete(columnName = "wasDeleted")
@SQLRestriction("was_deleted = false")
@EntityListeners(AuditingEntityListener.class)
public class AccountEntity {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(unique = true)
    private String name;

    @Column(nullable = false)
    private Double overdraftLimit;

    @Column(nullable = false)
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column
    @CreatedDate
    private LocalDateTime createdOn;

    @Column
    @LastModifiedDate
    private LocalDateTime lastUpdateOn;

    public static AccountEntity of(AccountModel accountModel){
        return AccountEntity.builder()
                .id(accountModel.getId())
                .name(accountModel.getName())
                .overdraftLimit(accountModel.getOverdraftLimit())
                .balance(accountModel.getBalance())
                .createdOn(accountModel.getCreatedOn())
                .lastUpdateOn(accountModel.getLastUpdateOn())
                .user(new UserEntity(accountModel.getUser()))
                .build();
    }

    public AccountModel toModel() {
        return AccountModel.builder()
                .id(id)
                .name(name)
                .overdraftLimit(overdraftLimit)
                .balance(balance)
                .user(user.toModel())
                .createdOn(createdOn)
                .lastUpdateOn(lastUpdateOn)
                .build();
    }
}