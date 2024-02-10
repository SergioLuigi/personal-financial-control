package br.com.sergioluigi.personalfinancialcontrol.infra.repository.entity;

import br.com.sergioluigi.personalfinancialcontrol.domain.AccountModel;
import br.com.sergioluigi.personalfinancialcontrol.domain.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

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

    @Column(unique = true, length = 150)
    private String name;

    @Column(nullable = false)
    private Double overdraftLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType type;

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
                .type(accountModel.getType())
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
                .type(type)
                .user(user.toModel())
                .createdOn(createdOn)
                .lastUpdateOn(lastUpdateOn)
                .build();
    }
}