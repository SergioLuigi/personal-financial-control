package br.com.sergioluigi.personalfinancialcontrol.infra.repository.entity;

import br.com.sergioluigi.personalfinancialcontrol.domain.SubscriptionType;
import br.com.sergioluigi.personalfinancialcontrol.domain.UserModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
@SoftDelete(columnName = "wasDeleted")
@SQLRestriction("was_deleted = false")
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 11, unique = true)
    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private SubscriptionType subscriptionType;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdOn;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastUpdateOn;

    public UserEntity(UserModel userModel) {
        this.id = userModel.getId();
        this.name = userModel.getName();
        this.username = userModel.getUsername();
        this.email = userModel.getEmail();
        this.cpf = userModel.getCpf();
        this.subscriptionType = userModel.getSubscriptionType();
        this.createdOn = userModel.getCreatedOn();
        this.lastUpdateOn = userModel.getLastUpdateOn();
    }

    public UserModel toModel() {
        return UserModel.builder()
                .id(this.id)
                .name(name)
                .username(this.username)
                .email(this.email)
                .cpf(this.cpf)
                .subscriptionType(this.subscriptionType)
                .createdOn(createdOn)
                .build();
    }
}
