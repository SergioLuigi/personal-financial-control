package com.sergioluigi.personalfinancialcontrol.adapter.database.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "bank_account")
@EntityListeners(AuditingEntityListener::class)
class BankAccount(

    @Id
    @GeneratedValue
    val id: UUID,

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    val user: User,

    @ManyToOne
    @JoinColumn(name="bank_id", nullable = false)
    val bank: Bank,

    @Column(name="number", length = 25, nullable = false)
    val number: String,

    @Column(name="number_digit", length = 3, nullable = false)
    val numberDigit: String,

    @Column(name="branch", length = 4, nullable = false)
    val branch: String,

    @Column(name="branch_digit", length = 3, nullable = false)
    val branchDigit: String,

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false)
    val type: BankAccountType,

    @Column(name="limit", nullable = false)
    val limit: BigDecimal = BigDecimal.ZERO,

    @Column(name="balance", nullable = false)
    val balance: BigDecimal = BigDecimal.ZERO,

): Serializable {

    @OneToMany(mappedBy = "bankAccount")
    var cards: Set<Card> = emptySet()

    @OneToMany(mappedBy = "bankAccount")
    var transactions: Set<BankAccountTransaction> = emptySet()

    @CreatedDate
    lateinit var createDate: LocalDateTime

    @LastModifiedDate
    lateinit var updateDate: LocalDateTime
}